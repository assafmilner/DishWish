package com.example.recipesapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.recipesapp.adapters.CategoryAdapter;
import com.example.recipesapp.databinding.FragmentCategoryBinding;
import com.example.recipesapp.models.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private FragmentCategoryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadCategories();
    }

    private void loadCategories() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        binding.rvCategories.setAdapter(new CategoryAdapter());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categoryList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    categoryList.add(category);
                }

                CategoryAdapter adapter = (CategoryAdapter) binding.rvCategories.getAdapter();
                if (adapter != null) {
                    adapter.setCategoryList(categoryList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CategoriesFragment", "onCancelled: " + error.getMessage());
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}