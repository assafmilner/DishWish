package com.example.recipesapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.recipesapp.R;
import com.example.recipesapp.activities.RecipeDetailsActivity;
import com.example.recipesapp.databinding.ItemRecipeBinding;
import com.example.recipesapp.models.Recipe;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder>{

    private List<Recipe> recipeList;

    // ✅ הוספת קונסטרקטור לקבלת רשימת מתכונים
    public RecipeAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }
    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecipeAdapter.RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeBinding binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecipeHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.RecipeHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.onBind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeHolder extends RecyclerView.ViewHolder {
        ItemRecipeBinding binding;

        public RecipeHolder(@NonNull ItemRecipeBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind(Recipe recipe) {
            Glide.with(binding.getRoot().getContext())
                    .load(recipe.getImage())
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .into(binding.bgImgRecipe);

            binding.tvRecipeName.setText(recipe.getName());

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(binding.getRoot().getContext(), RecipeDetailsActivity.class);
                    intent.putExtra("recipe", recipe);
                    binding.getRoot().getContext().startActivity(intent);
                }
            });
        }
    }
}
