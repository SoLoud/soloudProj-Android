package com.android.soloud.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by f.stamopoulos on 27/11/2016.
 */

public class Contest implements Serializable{

    /*@SerializedName("$id")
    private String id;*/

    @SerializedName("category")
    private String mCategory;

    @SerializedName("createdAt")
    private String mCreatedAt;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("endingAt")
    private String mEndingAt;

    @SerializedName("examplePhotos")
    private Photo[] mExamplePhotos;

    @SerializedName("id")
    private String mId;

    @SerializedName("optionalHashTags")
    private String mOptionalHashTags;

    @SerializedName("productPhotos")
    private Photo[] mProductPhotos;

    @SerializedName("requiredHashTags")
    private String mRequiredHashTags;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("user")
    private User mUser;

    @SerializedName("userId")
    private String mUserId;


    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public String getmCreatedAt() {
        return mCreatedAt;
    }

    public void setmCreatedAt(String mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmEndingAt() {
        return mEndingAt;
    }

    public void setmEndingAt(String mEndingAt) {
        this.mEndingAt = mEndingAt;
    }

    public Photo[] getmExamplePhotos() {
        return mExamplePhotos;
    }

    public void setmExamplePhotos(Photo[] mExamplePhotos) {
        this.mExamplePhotos = mExamplePhotos;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmOptionalHashTags() {
        return mOptionalHashTags;
    }

    public void setmOptionalHashTags(String mOptionalHashTags) {
        this.mOptionalHashTags = mOptionalHashTags;
    }

    public Photo[] getmProductPhotos() {
        return mProductPhotos;
    }

    public void setmProductPhotos(Photo[] mProductPhotos) {
        this.mProductPhotos = mProductPhotos;
    }

    public String getmRequiredHashTags() {
        return mRequiredHashTags;
    }

    public void setmRequiredHashTags(String mRequiredHashTags) {
        this.mRequiredHashTags = mRequiredHashTags;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public User getmUser() {
        return mUser;
    }

    public void setmUser(User mUser) {
        this.mUser = mUser;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public class Photo implements Serializable{

        public Photo(String url) {
            this.mUrl = url;
        }

        @SerializedName("url")
        private String mUrl;

        public String getmUrl() {return mUrl;}

    }

    public class User implements Serializable{

        public User(String mUserName){
            this.mUserName = mUserName;
        }

        @SerializedName("userName")
        private String mUserName;

        public String getmUserName() {
            return mUserName;
        }
    }

    /*public class HashTag implements Serializable{

        public HashTag(String name, String id, String itemId) {
            this.name = name;
            this.id = id;
            this.itemId = itemId;
        }

        @SerializedName("id")
        private String id;

        @SerializedName("itemId")
        private String itemId;

        @SerializedName("name")
        private String name;

        public String getId() {
            return id;
        }

        public String getItemId() {
            return itemId;
        }

        public String getName() {
            return name;
        }
    }*/


}
