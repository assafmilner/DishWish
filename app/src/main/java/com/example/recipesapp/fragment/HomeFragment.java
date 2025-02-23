package com.example.recipesapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.recipesapp.adapters.RecipeAdapter;
import com.example.recipesapp.databinding.FragmentHomeBinding;
import com.example.recipesapp.models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {




    private FragmentHomeBinding binding;

    List<Recipe> favouriteRecipes;
    List<Recipe> popularRecipes;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadRecipes();

    }

    private void loadPopularRecipes(List<Recipe> recipes) {
        if (recipes == null || recipes.isEmpty()) {
            Log.e("HomeFragment", "No recipes available to display.");
            return;
        }

        List<Recipe> popularRecipes = new ArrayList<>();
        // אם יש פחות מ-5 מתכונים, מציגים את כולם
        if (recipes.size() <= 5) {
            popularRecipes.addAll(recipes);
        } else {
            // מערבבים עותק של הרשימה ובוחרים את 5 המתכונים הראשונים
            List<Recipe> copy = new ArrayList<>(recipes);
            Collections.shuffle(copy);
            popularRecipes.addAll(copy.subList(0, 5));
        }

        RecipeAdapter adapter = (RecipeAdapter) binding.rvPopular.getAdapter();
        if (adapter != null) {
            adapter.setRecipeList(popularRecipes);
        }
    }


    private void loadFavouriteRecipes(List<Recipe> recipes) {
        RecipeAdapter adapter = (RecipeAdapter) binding.rvMeal.getAdapter();
        if (adapter != null) {
            adapter.setRecipeList(recipes); // השתמש בכל המתכונים שנשלחו לפונקציה
        }
    }



    private void loadRecipes() {
        // We will load recipes from our database
        binding.rvPopular.setAdapter(new RecipeAdapter(new ArrayList<>()));
        binding.rvMeal.setAdapter(new RecipeAdapter(new ArrayList<>()));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Recipes");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Recipe> recipes = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    recipes.add(recipe);
                }
                loadPopularRecipes(recipes);
                loadFavouriteRecipes(recipes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}