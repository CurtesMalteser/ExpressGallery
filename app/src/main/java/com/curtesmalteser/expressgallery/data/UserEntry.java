package com.curtesmalteser.expressgallery.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by António "Curtes Malteser" Bastião on 18/03/2018.
 */

@Entity(tableName = "user")
public class UserEntry {

    @PrimaryKey
    @NonNull
    private String id;
    private String fullName;
    private String username;
    private String profilePicture;

    UserEntry(String id, String fullName, String username, String profilePicture) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
