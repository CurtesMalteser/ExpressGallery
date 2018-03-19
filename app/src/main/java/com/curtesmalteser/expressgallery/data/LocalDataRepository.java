package com.curtesmalteser.expressgallery.data;

import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.util.Log;

import com.curtesmalteser.expressgallery.AppExecutors;
import com.curtesmalteser.expressgallery.api.LocalEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */

public class LocalDataRepository {
    private static final String LOG_TAG = LocalDataRepository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static LocalDataRepository sInstance;
    private final LocalDataDao mLocalDataDao;
    private final UserDao mUserDao;
    private final MediaNetworkDataSource mMediaNetworkDataSource;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;
    private boolean mNeedAuth = false;

    private long timestamp;

    private static final long STALE_MS = TimeUnit.MINUTES.toMillis(2);


    private LocalDataRepository(LocalDataDao localDataDao, UserDao userDao,
                                MediaNetworkDataSource mediaNetworkDataSource,
                                AppExecutors executors) {
        this.mLocalDataDao = localDataDao;
        this.mUserDao = userDao;
        this.mMediaNetworkDataSource = mediaNetworkDataSource;
        this.mExecutors = executors;
        this.timestamp = System.currentTimeMillis();

        // As long as the repository exists, observe the network LiveData.
        // If that LiveData changes, update the database.
        LiveData<ArrayList<LocalEntry>> networkData = mediaNetworkDataSource.getCurrentWeatherForecasts();
        networkData.observeForever((localEntries) ->
                mExecutors.diskIO().execute(() -> {
                    // Deletes old historical data
                    deleteOldData();
                    Log.d(LOG_TAG, "Old weather deleted");
                    // Insert our new weather data into Sunshine's database
                    mLocalDataDao.bulkInsert(localEntries);
                    Log.d(LOG_TAG, "New values inserted");
                }));

        LiveData<UserEntry> userNetworkData = mediaNetworkDataSource.getCurrentUser();

        userNetworkData.observeForever(userEntry ->
            mExecutors.diskIO().execute(() -> {
            deleteUserData();
                Log.d(LOG_TAG, "Old weather deleted");
                // Insert our new weather data into Sunshine's database
                mUserDao.insertUser(userEntry);
            })
        );
    }

    public synchronized static LocalDataRepository getInstance(
            LocalDataDao localDataDao,
            UserDao userDao,
            MediaNetworkDataSource mediaNetworkDataSource,
            AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new LocalDataRepository(localDataDao,
                        userDao,
                        mediaNetworkDataSource,
                        executors);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    public synchronized void initializeData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mInitialized) return;
        mInitialized = true;

        mExecutors.diskIO().execute(() -> {
            Log.d(LOG_TAG, "initializeData: " + mNeedAuth);
            if (isFetchNeeded()) {
                startFetchMedia();
            }
        });
    }

    public LiveData<List<LocalEntry>> getAll() {
        initializeData();
        return mLocalDataDao.getAll();
    }

    private void deleteOldData() {
        mLocalDataDao.deleteTable();
    }

    private boolean isFetchNeeded() {
        return System.currentTimeMillis() - timestamp < STALE_MS;
    }

    private void startFetchMedia() {
        mMediaNetworkDataSource.fetchWeather();
    }

    public LiveData<UserEntry> getUser() {
        initializeData();
        return mUserDao.getAll();
    }

    private void deleteUserData() {
        mUserDao.deleteTable();
    }

    private void getUserFromNetwork() {
        mMediaNetworkDataSource.fetchUser();
    }

}
