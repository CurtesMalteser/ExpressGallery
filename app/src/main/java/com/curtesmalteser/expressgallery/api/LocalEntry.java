package com.curtesmalteser.expressgallery.api;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by António "Curtes Malteser" Bastião on 16/03/2018.
 */


@Entity(tableName = "local_data")
public class LocalEntry {

    @PrimaryKey(autoGenerate = true)
    int id;

    private String url;
    private int likes;
    private int comments;

    @Ignore
    public LocalEntry(String url, int likes, int comments) {
        this.url = url;
        this.likes = likes;
        this.comments = comments;
    }

    // Construtor used by ROOM
    public LocalEntry(int id, String url, int likes, int comments) {
        this.id = id;
        this.url = url;
        this.likes = likes;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}
