package com.example.recipesapp.models;

import java.io.Serializable;

public class Recipe implements Serializable {
    private String id;
    private String name;
    private String image;
    private String description;
    private String category;
    private String calories;
    private String time;

    private String authorID;
    private boolean isFavorite;
    public Recipe() {}

    public Recipe(String name, String description, String time, String category, String calories, String image,String authorID ) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.category = category;
        this.calories = calories;
        this.image = image;
        this.authorID= authorID;
        this.isFavorite = isFavorite;

    }
    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }


}
