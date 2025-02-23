package com.example.recipesapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.recipesapp.R;
import com.example.recipesapp.activities.RecipeListActivity;
import com.example.recipesapp.databinding.ItemCategoryBinding;
import com.example.recipesapp.models.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private List<Category> categoryList = new ArrayList<>();

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.onBind(category);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        public CategoryHolder(@NonNull ItemCategoryBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind(Category category) {
            binding.tvName.setText(category.getName());

            Glide.with(binding.getRoot().getContext())
                    .load(category.getImage())
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .into(binding.imgBgCategory);

            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(binding.getRoot().getContext(), RecipeListActivity.class);
                intent.putExtra("category", category.getName()); 
                binding.getRoot().getContext().startActivity(intent);
            });
        }
    }
}
