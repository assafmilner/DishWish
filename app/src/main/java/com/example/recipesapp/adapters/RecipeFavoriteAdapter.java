package com.example.recipesapp.adapters;

// RecipeFavoriteAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipesapp.R;
import com.example.recipesapp.models.RecipeFavorite;

import java.util.List;

public class RecipeFavoriteAdapter extends RecyclerView.Adapter<RecipeFavoriteAdapter.RecipeFavoriteViewHolder> {
    private List<RecipeFavorite> recipeFavoriteList;
    private OnItemClickListener listener;

    public RecipeFavoriteAdapter(List<RecipeFavorite> recipeFavoriteList, OnItemClickListener listener) {
        this.recipeFavoriteList = recipeFavoriteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeFavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_favorite, parent, false);
        return new RecipeFavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeFavoriteViewHolder holder, int position) {
        RecipeFavorite recipeFavorite = recipeFavoriteList.get(position);
        holder.bind(recipeFavorite, listener);
    }

    @Override
    public int getItemCount() {
        return recipeFavoriteList.size();
    }

    public class RecipeFavoriteViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView linkTextView;

        public RecipeFavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            linkTextView = itemView.findViewById(R.id.linkTextView);
        }

        public void bind(RecipeFavorite recipeFavorite, OnItemClickListener listener) {
            nameTextView.setText(recipeFavorite.getName());
            // בדיקה האם שדה ה-link ריק, ואם כן - יצירת URL אקראי להצגה
            String linkToDisplay = recipeFavorite.getLink();
            if (linkToDisplay == null || linkToDisplay.isEmpty()) {
                linkToDisplay = generateRandomLink();
            }
            linkTextView.setText(linkToDisplay);
            itemView.setOnClickListener(v -> listener.onItemClick(recipeFavorite)); // מתבצע כאשר לוחצים על המתכון
        }
    }

    public interface OnItemClickListener {
        void onItemClick(RecipeFavorite recipeFavorite);
    }

    private String generateRandomLink() {
        int length = 10; //
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }
        return "https://DishWish.com/" + sb.toString();
    }
}
