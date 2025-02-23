package com.example.recipesapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.recipesapp.databinding.ActivityAddRecipeBinding;
import com.example.recipesapp.models.Category;
import com.example.recipesapp.models.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean isImageSelected = false;
    private Bitmap bitmap;
    private ProgressDialog dialog;
    private ActivityAddRecipeBinding binding;

    private static final String CLOUDINARY_CLOUD_NAME = "ddygnvbr7";
    private static final String CLOUDINARY_UPLOAD_PRESET = "ml_default";
    private static final String CLOUDINARY_UPLOAD_URL = "https://api.cloudinary.com/v1_1/" + CLOUDINARY_CLOUD_NAME + "/image/upload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadCategories();

        binding.btnAddRecipe.setOnClickListener(v -> getData());
        binding.imgRecipe.setOnClickListener(v -> selectImage());

        // כאן אתה מוסיף את הקוד שיאפשר גלילה פנימית בתוך EditText
        binding.etDescription.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                v.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        });
    }

    private void loadCategories() {
        List<String> categories = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        binding.etCategory.setAdapter(adapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Categories");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        categories.add(dataSnapshot.getValue(Category.class).getName());
                    }
                    adapter.notifyDataSetChanged(); // מעדכן את האדפטר עם הקטגוריות
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                binding.imgRecipe.setImageBitmap(bitmap);
                binding.imgRecipe.setScaleType(ImageView.ScaleType.CENTER_CROP);
                isImageSelected = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getData() {
        String recipeName = Objects.requireNonNull(binding.etRecipeName.getText()).toString();
        String recipeDescription = Objects.requireNonNull(binding.etDescription.getText()).toString();
        String cookingTime = Objects.requireNonNull(binding.etCookingTime.getText()).toString();
        String recipeCategory = binding.etCategory.getText().toString();
        String calories = Objects.requireNonNull(binding.etCalories.getText()).toString();

        if (recipeName.isEmpty()) {
            binding.etRecipeName.setError("Please enter Recipe Name");
        } else if (recipeDescription.isEmpty()) {
            binding.etDescription.setError("Please enter Recipe Description");
        } else if (cookingTime.isEmpty()) {
            binding.etCookingTime.setError("Please enter Cooking Time");
        } else if (recipeCategory.isEmpty()) {
            binding.etCategory.setError("Please enter Recipe Category");
        } else if (calories.isEmpty()) {
            binding.etCalories.setError("Please enter Calories");
        } else if (!isImageSelected) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        } else {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading Recipe...");
            dialog.setCancelable(false);
            dialog.show();

            Recipe recipe = new Recipe(recipeName, recipeDescription, cookingTime, recipeCategory, calories, "", FirebaseAuth.getInstance().getUid());
            uploadImageToCloudinary(recipe);
        }
    }

    private void uploadImageToCloudinary(Recipe recipe) {
        String imageName = "IMG_" + System.currentTimeMillis();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();
        String base64Image = Base64.encodeToString(imageData, Base64.DEFAULT);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CLOUDINARY_UPLOAD_URL,
                response -> {
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String imageUrl = jsonObject.getString("secure_url");
                        Toast.makeText(AddRecipeActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        saveDataInDataBase(recipe, imageUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    dialog.dismiss();
                    // Log the response error to get more information
                    if (error.networkResponse != null) {
                        Log.e("Cloudinary", "Error Code: " + error.networkResponse.statusCode);
                        Log.e("Cloudinary", "Error Body: " + new String(error.networkResponse.data));
                    }
                    Toast.makeText(AddRecipeActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                    Log.e("Cloudinary", "Upload Error: " + error.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Remove the API key for unsigned upload
                params.put("file", "data:image/jpeg;base64," + base64Image);
                params.put("upload_preset", CLOUDINARY_UPLOAD_PRESET);  // Your upload preset name
                params.put("public_id", "image_" + System.currentTimeMillis());  // ככה נוודא שאין slashes בשם

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }



    private void saveDataInDataBase(Recipe recipe, String imageUrl) {
        recipe.setImage(imageUrl);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Recipes");
        String id = reference.push().getKey();
        recipe.setId(id);
        if (id != null) {
            reference.child(id).setValue(recipe).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(AddRecipeActivity.this, "Recipe Added Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(AddRecipeActivity.this, "Failed to add Recipe", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
