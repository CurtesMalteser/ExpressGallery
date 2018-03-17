package com.curtesmalteser.expressgallery.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.curtesmalteser.expressgallery.api.LocalModel;

/**
 * Created by António "Curtes Malteser" Bastião on 15/03/2018.
 */

@Database(entities = {LocalModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract LocalDataDao localDataDao();

    private static final String DATABASE_NAME = "local_data";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static volatile AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, AppDatabase.DATABASE_NAME).build();
                }
            }
        }
        return sInstance;
    }
}
