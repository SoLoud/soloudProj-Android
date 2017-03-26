package com.android.soloud.models;

/**
 * Created by f.stamopoulos on 26/3/2017.
 */

public class Training {

    public Training() {
    }

    public Training(String title, int imageResourceId, int requiredPoints) {
        this.title = title;
        this.imageResourceId = imageResourceId;
        this.requiredPoints = requiredPoints;
    }

    private String title;

    private int imageResourceId;

    private int requiredPoints;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }

    public void setRequiredPoints(int requiredPoints) {
        this.requiredPoints = requiredPoints;
    }
}
