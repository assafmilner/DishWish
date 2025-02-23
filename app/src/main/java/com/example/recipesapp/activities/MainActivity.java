package com.example.recipesapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.example.recipesapp.R;
import com.example.recipesapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Enable and set Action Bar color
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.navigation_bar_color)));
        }

        // Make Status Bar match Navigation Bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            window.setStatusBarColor(getResources().getColor(R.color.navigation_bar_color));
        }

        // Ensure Toolbar color consistency
        setTheme(R.style.Theme_RecipesApp);

        // Set up Navigation Component
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Floating Action Button to add a new recipe
        binding.floatingActionButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AddRecipeActivity.class));
        });
    }
}
