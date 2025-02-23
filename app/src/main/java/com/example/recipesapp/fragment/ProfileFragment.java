package com.example.recipesapp.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.recipesapp.R;
import com.example.recipesapp.adapters.RecipeAdapter;
import com.example.recipesapp.databinding.FragmentProfileBinding;
import com.example.recipesapp.models.Recipe;
import com.example.recipesapp.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private User user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadProfile();
        loadUserRecipes();
        init();
    }

    private void init() {
        binding.imgEdit.setOnClickListener(v -> {

            PickImageDialog.build(new PickSetup()).show(requireActivity()).setOnPickResult(r -> {
                Log.e("ProfileFragment", "onPickResult: " + r.getUri());
                binding.imgProfole.setImageBitmap(r.getBitmap());
                uploadImage(r.getBitmap());
            }).setOnPickCancel(() -> Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show());
        });
    }

    private void uploadImage(Bitmap bitmap) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images/" + FirebaseAuth.getInstance().getUid() + "image.jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return storageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                Toast.makeText(requireContext(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                user.setImage(Objects.requireNonNull(downloadUri).toString());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(user);
            } else {
                Log.e("ProfileFragment", "onComplete: " + Objects.requireNonNull(task.getException()).getMessage());
            }
        });

    }

    private void loadUserRecipes() {

        binding.rvProfile.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rvProfile.setAdapter(new RecipeAdapter(new ArrayList<>()));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Recipes").orderByChild("authorID").equalTo(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Recipe> recipes = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Recipe recipe = dataSnapshot.getValue(Recipe.class);
                            recipes.add(recipe);
                        }
                        ((RecipeAdapter) Objects.requireNonNull(binding.rvProfile.getAdapter())).setRecipeList(recipes);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ProfileFragment", "onCancelled: " + error.getMessage());
                    }
                });

    }

    // עוזרים להמרת ערכים בצורה בטוחה
    private String getString(DataSnapshot snapshot) {
        Object value = snapshot.getValue();
        if (value instanceof String) {
            return (String) value;
        }
        return "";
    }

    private List<String> getListFromSnapshot(DataSnapshot snapshot) {
        List<String> list = new ArrayList<>();
        if (snapshot.exists()) {
            Object data = snapshot.getValue();
            if (data instanceof List) {
                list = (List<String>) data;
            } else if (data instanceof Map) {
                Map<String, String> map = (Map<String, String>) data;
                list.addAll(map.values());
            }
        }
        return list;
    }

    private void loadProfile() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            // שליפת השדות בצורה ידנית כדי למנוע המרה שגויה
                            String id = getString(snapshot.child("id"));
                            String name = getString(snapshot.child("name"));
                            String email = getString(snapshot.child("email"));
                            String image = getString(snapshot.child("image"));
                            List<String> favoriteRecipes = getListFromSnapshot(snapshot.child("favoriteRecipes"));
                            List<String> externalRecipes = getListFromSnapshot(snapshot.child("externalRecipes"));

                            user = new User(id, name, email, image, favoriteRecipes, externalRecipes);

                            binding.tvEmail.setText(user.getEmail());

                            if (!image.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(image)
                                        .centerCrop()
                                        .placeholder(R.mipmap.ic_launcher)
                                        .into(binding.imgProfole);
                            }
                        } else {
                            Log.e("ProfileFragment", "User is null");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ProfileFragment", "onCancelled: " + error.getMessage());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
