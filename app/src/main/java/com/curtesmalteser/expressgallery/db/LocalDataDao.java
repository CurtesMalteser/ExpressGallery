package com.curtesmalteser.expressgallery.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.curtesmalteser.expressgallery.api.LocalModel;

import java.util.List;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */

@Dao
public interface LocalDataDao {

    @Query("SELECT * FROM local_data")
    List<LocalModel> getAll();


    @Insert
    void insertAll(LocalModel... locals);

    @Delete
    void delete(LocalModel local);

    @Query("DELETE FROM local_data")
    public void deleteTable();
}
