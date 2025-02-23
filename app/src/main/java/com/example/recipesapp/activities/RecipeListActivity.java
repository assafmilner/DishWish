package com.example.recipesapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recipesapp.adapters.RecipeAdapter;
import com.example.recipesapp.databinding.ActivityRecipeListBinding;
import com.example.recipesapp.models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {

    private ActivityRecipeListBinding binding;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList = new ArrayList<>();
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // קבלת שם הקטגוריה שהועברה דרך Intent
        selectedCategory = getIntent().getStringExtra("category");

        if (selectedCategory == null || selectedCategory.isEmpty()) {
            Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("RecipeListActivity", "Selected Category: " + selectedCategory);


        recipeAdapter = new RecipeAdapter(recipeList);
        binding.rvRecipes.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvRecipes.setAdapter(recipeAdapter);


        loadRecipesByCategory();
    }

    private void loadRecipesByCategory() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Recipes");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    if (recipe != null && recipe.getCategory().equals(selectedCategory)) {
                        recipeList.add(recipe);
                    }
                }

                if (recipeList.isEmpty()) {
                    Toast.makeText(RecipeListActivity.this, "No recipes found in this category", Toast.LENGTH_SHORT).show();
                }

                recipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RecipeListActivity", "Database error: " + error.getMessage());
            }
        });
    }
}
