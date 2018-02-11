package com.android.soloud.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by f.stamopoulos on 16/2/2017.
 */

public class CurrentState implements Serializable {

    private String photoUri;

    private String companyName;

    private String userPostDescription;

    private String userHashTags;

    private ArrayList<String> userHashTagsList;

    private String contestCategoryName;

    public CurrentState() {
        // Empty Constructor
    }

    public CurrentState(String photoUri, String companyName, String userPostDescription, String userHashTags, String contestCategoryName) {
        this.photoUri = photoUri;
        this.companyName = companyName;
        this.userPostDescription = userPostDescription;
        this.userHashTags = userHashTags;
        this.contestCategoryName = contestCategoryName;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }



    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUserPostDescription() {
        return userPostDescription;
    }

    public void setUserPostDescription(String userPostDescription) {
        this.userPostDescription = userPostDescription;
    }

    public String getUserHashTags() {
        return userHashTags;
    }

    public void setUserHashTags(String userHashTags) {
        this.userHashTags = userHashTags;
    }

    public String getContestCategoryName() {
        return contestCategoryName;
    }

    public void setContestCategoryName(String contestCategoryName) {
        this.contestCategoryName = contestCategoryName;
    }

    public ArrayList<String> getUserHashTagsList() {
        return userHashTagsList;
    }

    public void setUserHashTagsList(ArrayList<String> userHashTagsList) {
        this.userHashTagsList = userHashTagsList;
    }
}
