package com.curtesmalteser.expressgallery.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by António "Curtes Malteser" Bastião on 18/03/2018.
 */

@Dao // Required annotation for Dao to be recognized by Room
public interface UserDao {

    @Query("SELECT * FROM user")
    LiveData<UserEntry> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntry user);

    @Query("DELETE FROM user")
    void deleteTable();
}
