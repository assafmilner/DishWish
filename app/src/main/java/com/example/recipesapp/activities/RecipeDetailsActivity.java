package com.example.recipesapp.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recipesapp.R;
import com.example.recipesapp.models.Recipe;
import com.example.recipesapp.models.RecipeFavorite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity {

    private Recipe currentRecipe; // המתכון הנוכחי
    private ImageView favoriteMeal;
    private TextView tvName, tvCategory, tvCalories, tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        favoriteMeal = findViewById(R.id.favoriteMeal);
        tvName = findViewById(R.id.tv_name1);
        tvCategory = findViewById(R.id.tv_category);
        tvCalories = findViewById(R.id.tv_calories);
        tvDescription = findViewById(R.id.tv_description);

        currentRecipe = (Recipe) getIntent().getSerializableExtra("recipe");

        if (currentRecipe == null) {
            Log.e("RecipeDetailsActivity", "❌ Recipe data is missing!");
            Toast.makeText(this, "Recipe data is missing", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.d("RecipeDetailsActivity", "✅ Received full recipe details: " + currentRecipe.getName());
            displayRecipeDetails(currentRecipe);
        }


        // הצגת פרטי המתכון
        displayRecipeDetails(currentRecipe);

        // הוספת מאזין לכפתור המועדפים
        setupFavoriteButton(currentRecipe);
    }

    private void displayRecipeDetails(Recipe recipe) {
        // הצגת פרטי המתכון
        tvName.setText(recipe.getName());
        tvCategory.setText(recipe.getCategory());
        tvCalories.setText(recipe.getCalories() + " Calories");
        tvDescription.setText(recipe.getDescription());

        // הצגת התמונה מה-URL
        if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
            new DownloadImageTask((ImageView) findViewById(R.id.img_recipe1)).execute(recipe.getImage());
        }
    }

    private void setupFavoriteButton(Recipe recipe) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        String userId = mAuth.getCurrentUser().getUid();
        String recipeKey = getRecipeKey(recipe); // קבלת המפתח האמיתי של המתכון

        // שליפת רשימת המועדפים ובדיקה אם המתכון קיים שם
        mDatabase.child(userId).child("favoriteRecipes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> favoriteRecipes = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    favoriteRecipes.add(data.getValue(String.class));
                }

                // בדיקה אם המתכון כבר במועדפים ועדכון הכוכב בהתאם
                if (favoriteRecipes.contains(recipeKey)) {
                    favoriteMeal.setImageResource(R.drawable.btn_star_big_on); // כוכב מלא
                } else {
                    favoriteMeal.setImageResource(R.drawable.btn_star_big_off); // כוכב ריק
                }

                // מאזין ללחיצות על הכוכב להוספה/הסרה מרשימת המועדפים
                favoriteMeal.setOnClickListener(v -> {
                    if (favoriteRecipes.contains(recipeKey)) {
                        // הסרה מהמועדפים
                        favoriteRecipes.remove(recipeKey);
                        favoriteMeal.setImageResource(R.drawable.btn_star_big_off);
                        Toast.makeText(RecipeDetailsActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        // הוספה למועדפים
                        favoriteRecipes.add(recipeKey);
                        favoriteMeal.setImageResource(R.drawable.btn_star_big_on);
                        Toast.makeText(RecipeDetailsActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }

                    // עדכון הנתונים ב-Firebase
                    mDatabase.child(userId).child("favoriteRecipes").setValue(favoriteRecipes);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecipeDetailsActivity.this, "Error checking favorite status", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private String getRecipeKey(Recipe recipe) {
        return "recipe" + recipe.getId(); // לדוגמה, מחזיר "recipe1" במקום "1"
    }

    // AsyncTask להורדת התמונה מה-URL
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }
}
