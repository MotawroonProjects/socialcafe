package com.socialcafe.models;

import java.io.Serializable;

public class BrandModel implements Serializable {
   private int id;
   private String title;
   private String image;
   private int is_active;
   private String created_at;
   private String updated_at;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public int getIs_active() {
        return is_active;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}


