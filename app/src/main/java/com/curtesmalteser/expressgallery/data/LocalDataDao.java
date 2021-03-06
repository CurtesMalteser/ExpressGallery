package com.curtesmalteser.expressgallery.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */

@Dao
public interface LocalDataDao {

    @Query("SELECT * FROM local_data")
    LiveData<List<LocalEntry>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(ArrayList<LocalEntry> locals);

    @Query("DELETE FROM local_data")
    int deleteTable();
}
