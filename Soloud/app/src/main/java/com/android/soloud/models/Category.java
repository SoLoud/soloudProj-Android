package com.android.soloud.models;

/**
 * Created by f.stamopoulos on 15/10/2016.
 */

public class Category {

    private String name;
    private String type;
    private int imageResourceId;

    public Category() {
        // Empty Constructor
    }

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, int imageResourceId) {
        this.name = name;
        this.imageResourceId = imageResourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
