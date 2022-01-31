package com.socialcafe.models;

import java.io.Serializable;

public class TableModel implements Serializable {
    private int id;
    private String name;
    private int count;
    private int door_id;
    private String photo;
    private String created_at;
    private String updated_at;
    private boolean is_busy;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public int getDoor_id() {
        return door_id;
    }

    public String getPhoto() {
        return photo;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public boolean isIs_busy() {
        return is_busy;
    }
}


