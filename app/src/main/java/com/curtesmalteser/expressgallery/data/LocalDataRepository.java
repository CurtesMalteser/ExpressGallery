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

    private LocalDataRepository(LocalDataDao localDataDao,
                                MediaNetworkDataSource mediaNetworkDataSource,
                               AppExecutors executors) {
       this.mLocalDataDao = localDataDao;
        this.mMediaNetworkDataSource = mediaNetworkDataSource;
        this.mExecutors = executors;

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
           // if (isFetchNeeded()) {
                startFetchWeatherService();
           // }
        });
    }

    /**
     * Database related operations
     **/

    public LiveData<List<LocalEntry>> getAll() {
        initializeData();
        return mLocalDataDao.getAll();
    }

    /**
     * Deletes old weather data because we don't need to keep multiple days' data
     */
    private void deleteOldData() {
        mLocalDataDao.deleteTable();
    }

    /**
     * Checks if there are enough days of future weather for the app to display all the needed data.
     *
     * @return Whether a fetch is needed
     */
   /* private boolean isFetchNeeded() {
        Date today = SunshineDateUtils.getNormalizedUtcDateForToday();
        int count = mLocalDataDao.countAllFutureWeather(today);
        return (count < WeatherNetworkDataSource.NUM_DAYS);
    }*/

    /**
     * Network related operation
     */

    private void startFetchWeatherService() {
        mMediaNetworkDataSource.startFetchWeatherService();
    }



}
