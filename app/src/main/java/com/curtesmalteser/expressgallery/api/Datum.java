
package com.curtesmalteser.expressgallery.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Datum {

    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("images")
    @Expose
    private Images images;

    @SerializedName("likes")
    @Expose
    private Likes likes;

    @SerializedName("comments")
    @Expose
    private Comments comments;

    public List<Datum> getData() {
        return data;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Images getImages() {
        return images;
    }

    public Likes getLikes() {
        return likes;
    }

    public Comments getComments() {
        return comments;
    }
}
