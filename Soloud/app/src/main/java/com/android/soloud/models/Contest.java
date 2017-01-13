package com.android.soloud.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by f.stamopoulos on 27/11/2016.
 */

public class Contest implements Serializable{

    @SerializedName("$id")
    private String id;

    @SerializedName("hashTags")
    private HashTag[] hashTags;

    @SerializedName("photos")
    private Photo[] photos;

    @SerializedName("user")
    private User user;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("endingAt")
    private String endingAt;

    @SerializedName("productImageUrl")
    private String productImageUrl;

    @SerializedName("category")
    private String category;

    public Photo[] getPhotos() {
        return photos;
    }

    public String getEndingAt() {
        return endingAt;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public HashTag[] getHashTags() {
        return hashTags;
    }

    public class Photo implements Serializable{

        public Photo(String content) {
            this.content = content;
        }

        @SerializedName("content")
        private String content;

        public String getContent() {return content;}

    }

    public class User implements Serializable{

        public User(String email){
            this.email = email;
        }

        @SerializedName("email")
        private String email;

        public String getEmail() {return email;}
    }

    public class HashTag implements Serializable{

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
    }


}
