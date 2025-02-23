package com.example.recipesapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipesapp.R;
import com.example.recipesapp.activities.RecipeDetailsActivity;
import com.example.recipesapp.adapters.RecipeFavoriteAdapter;
import com.example.recipesapp.models.Recipe;
import com.example.recipesapp.models.RecipeFavorite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeFavoriteAdapter recipeFavoriteAdapter;
    private List<RecipeFavorite> recipeFavoriteList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        recyclerView = view.findViewById(R.id.recipesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recipeFavoriteList = new ArrayList<>();
        recipeFavoriteAdapter = new RecipeFavoriteAdapter(recipeFavoriteList, recipe -> {
            Toast.makeText(getContext(), "Opening " + recipe.getName(), Toast.LENGTH_SHORT).show();


            fetchFullRecipeDetails(recipe.getId());
        });

        recyclerView.setAdapter(recipeFavoriteAdapter);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");


        Button addRecipeButton = view.findViewById(R.id.addRecipeButton);
        addRecipeButton.setOnClickListener(v -> showAddRecipeDialog());

        loadFavoriteRecipes();

        return view;
    }

    private void loadFavoriteRecipes() {
        recipeFavoriteList.clear();
        String userId = mAuth.getCurrentUser().getUid();


        mDatabase.child(userId).child("favoriteRecipes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> favoriteRecipeIds = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String recipeId = data.getValue(String.class);
                    if (recipeId != null) {
                        favoriteRecipeIds.add(recipeId);
                    }
                }
                if (!favoriteRecipeIds.isEmpty()) {
                    fetchRecipesDetails(favoriteRecipeIds);
                } else {
                    Log.e("FavouritesFragment", "Favorite recipes list is empty.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FavouritesFragment", "onCancelled: " + error.getMessage());
            }
        });


        loadExternalFavorites();
    }

    private void fetchRecipesDetails(List<String> favoriteRecipeIds) {
        DatabaseReference recipesRef = FirebaseDatabase.getInstance().getReference("Recipes");

        for (String recipeId : favoriteRecipeIds) {
            recipesRef.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    RecipeFavorite recipe = snapshot.getValue(RecipeFavorite.class);
                    if (recipe != null) {
                        recipeFavoriteList.add(recipe);
                    } else {
                        Log.e("FavouritesFragment", "Recipe not found for id: " + recipeId);
                    }
                    recipeFavoriteAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FavouritesFragment", "onCancelled: " + error.getMessage());
                }
            });
        }
    }

    private void fetchFullRecipeDetails(String recipeId) {
        DatabaseReference recipesRef = FirebaseDatabase.getInstance().getReference("Recipes");

        Log.d("FavouritesFragment", "🔍 Fetching full recipe details for ID: " + recipeId);

        recipesRef.child("recipe" + recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.e("FavouritesFragment", "❌ Recipe ID not found in Recipes database: " + recipeId);
                    Toast.makeText(getContext(), "Recipe not found in database", Toast.LENGTH_SHORT).show();
                    return;
                }

                Recipe recipe = snapshot.getValue(Recipe.class);
                if (recipe != null) {
                    Log.d("FavouritesFragment", "✅ Full recipe found: " + recipe.getName());

                    Intent intent = new Intent(getActivity(), RecipeDetailsActivity.class);
                    intent.putExtra("recipe", recipe);
                    startActivity(intent);
                } else {
                    Log.e("FavouritesFragment", "❌ Recipe data is empty for ID: " + recipeId);
                    Toast.makeText(getContext(), "Recipe details not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FavouritesFragment", "❌ Failed to fetch recipe details: " + error.getMessage());
            }
        });
    }



    private void loadExternalFavorites() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child(userId).child("externalRecipes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeFavoriteList.clear(); // ניקוי לפני הטעינה החדשה

                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        try {
                            RecipeFavorite recipe = child.getValue(RecipeFavorite.class);
                            if (recipe != null) {
                                recipeFavoriteList.add(recipe);
                            }
                        } catch (Exception e) {
                            Log.e("FavouritesFragment", "⚠️ שגיאה בטעינת הנתונים: " + e.getMessage());
                        }
                    }
                    recipeFavoriteAdapter.notifyDataSetChanged();
                } else {
                    Log.e("FavouritesFragment", "🔍 אין נתונים ב-externalRecipes.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FavouritesFragment", "❌ שגיאה בקריאת הנתונים: " + error.getMessage());
            }
        });
    }






    private void showAddRecipeDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_recipe_favorite, null);
        EditText recipeNameEditText = dialogView.findViewById(R.id.recipeNameEditText);
        EditText recipeLinkEditText = dialogView.findViewById(R.id.recipeLinkEditText);
        Button addButton = dialogView.findViewById(R.id.addButton);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        addButton.setOnClickListener(v -> {
            String name = recipeNameEditText.getText().toString();
            String link = recipeLinkEditText.getText().toString();
            addNewRecipe(name, link);
            dialog.dismiss();
        });
    }

    private void addNewRecipe(String name, String link) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRecipesRef = mDatabase.child(userId).child("externalRecipes");

        // יצירת מזהה ייחודי למתכון החדש
        String recipeId = userRecipesRef.push().getKey();

        if (recipeId == null) {
            Log.e("FavouritesFragment", "❌ Failed to generate unique ID for new recipe.");
            return;
        }

        // יצירת אובייקט מתכון
        RecipeFavorite newRecipe = new RecipeFavorite(name, recipeId, null, link);

        // הוספת המתכון החדש לרשימה מבלי למחוק את הקודמים
        userRecipesRef.child(recipeId).setValue(newRecipe).addOnSuccessListener(aVoid -> {
            recipeFavoriteList.add(newRecipe);
            recipeFavoriteAdapter.notifyDataSetChanged();
            Log.d("FavouritesFragment", "✅ Recipe added successfully!");
        }).addOnFailureListener(e -> Log.e("FavouritesFragment", "❌ Failed to save recipe: " + e.getMessage()));
    }



}
