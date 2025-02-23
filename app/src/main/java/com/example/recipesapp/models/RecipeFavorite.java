package com.example.recipesapp.models;

import java.io.Serializable;

public class RecipeFavorite implements Serializable {
    private String name;
    private String id;
    private String image;
    private String link;

    // נדרש על ידי Firebase
    public RecipeFavorite() {
    }

    // קונסטרקטור למתכון עם מזהה, שם ותמונה
    public RecipeFavorite(String name, String id, String image) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.link = null;
    }

    // קונסטרקטור למתכון עם לינק (מהאינטרנט)
    public RecipeFavorite(String name, String id, String image, String link) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.link = link;
    }

    public String getName() { return name; }
    public String getId() { return id; }
    public String getImage() { return image; }
    public String getLink() { return link; }

    // בדיקה אם המתכון הוא חיצוני (מגיע מהאינטרנט ולא מהמאגר המקומי)
    public boolean isExternal() {
        return link != null ;
    }
}
