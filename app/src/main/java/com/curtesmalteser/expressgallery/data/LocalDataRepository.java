package com.curtesmalteser.expressgallery.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.curtesmalteser.expressgallery.AppExecutors;
import com.curtesmalteser.expressgallery.R;
import com.curtesmalteser.expressgallery.api.Datum;
import com.curtesmalteser.expressgallery.api.LocalEntry;
import com.curtesmalteser.expressgallery.retrofit.MediaAPI;
import com.curtesmalteser.expressgallery.retrofit.MediaAPIInterface;

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
    private final MediaNetworkDataSource mMediaNetworkDataSource;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;

    private long timestamp;

    private static final long STALE_MS = TimeUnit.MINUTES.toMillis(2);



    private LocalDataRepository(LocalDataDao localDataDao,
                                MediaNetworkDataSource mediaNetworkDataSource,
                                AppExecutors executors) {
        this.mLocalDataDao = localDataDao;
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
    }

    public synchronized static LocalDataRepository getInstance(
            LocalDataDao localDataDao,
            MediaNetworkDataSource mediaNetworkDataSource,
            AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new LocalDataRepository(localDataDao, mediaNetworkDataSource,
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

        // mMediaNetworkDataSource.scheduleRecurringFetchWeatherSync();

        mExecutors.diskIO().execute(() -> {
            if (isFetchNeeded()) {
                startFetchWeatherService();
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

    private void startFetchWeatherService() {
       // mMediaNetworkDataSource.startFetchWeatherService();
        mMediaNetworkDataSource.fetchWeather();
    }

}
