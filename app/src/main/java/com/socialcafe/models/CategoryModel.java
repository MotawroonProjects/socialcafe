package com.socialcafe.models;

import java.io.Serializable;

public class CategoryModel implements Serializable {
    private int id;
    private String is_sale;
    private String name;
    private String image;
    private String parent_id;
    private int is_active;
    private String created_at;
    private String updated_at;
    private String make;
    private String make_place;

    public int getId() {
        return id;
    }

    public String getIs_sale() {
        return is_sale;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getParent_id() {
        return parent_id;
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

    public String getMake() {
        return make;
    }

    public String getMake_place() {
        return make_place;
    }
}


