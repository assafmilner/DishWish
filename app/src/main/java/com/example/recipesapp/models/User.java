package com.example.recipesapp.models;

import java.util.List;

public class User {

    private String id, name, email, image;
    private List<String> favoriteRecipes;
    private List<String> externalRecipes; // תיקון השם מ-externalLinks ל-externalRecipes

    public User() {
    }

    public User(String id, String name, String email, String image, List<String> favoriteRecipes, List<String> externalRecipes) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.image = image;
        this.favoriteRecipes = favoriteRecipes;
        this.externalRecipes = externalRecipes; // גם כאן מעדכן את השם
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public void setFavoriteRecipes(List<String> favoriteRecipes) {
        this.favoriteRecipes = favoriteRecipes;
    }

    public List<String> getExternalRecipes() { // עדכון פונקציה כך שתתאים לשם הנכון
        return externalRecipes;
    }

    public void setExternalRecipes(List<String> externalRecipes) { // עדכון השם כאן גם כן
        this.externalRecipes = externalRecipes;
    }
}
