package com.curtesmalteser.expressgallery.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.curtesmalteser.expressgallery.AppExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */

public class LocalDataRepository {

    private static final Object LOCK = new Object();
    private static LocalDataRepository sInstance;
    private final LocalDataDao mLocalDataDao;
    private final UserDao mUserDao;
    private final MediaNetworkDataSource mMediaNetworkDataSource;
    private final AppExecutors mExecutors;

    private LocalDataRepository(LocalDataDao localDataDao, UserDao userDao,
                                MediaNetworkDataSource mediaNetworkDataSource,
                                AppExecutors executors) {
        this.mLocalDataDao = localDataDao;
        this.mUserDao = userDao;
        this.mMediaNetworkDataSource = mediaNetworkDataSource;
        this.mExecutors = executors;

        LiveData<ArrayList<LocalEntry>> networkData = mediaNetworkDataSource.getRecentMedia();
        networkData.observeForever((localEntries) ->
                mExecutors.diskIO().execute(() -> {
                    deleteOldData();
                    mLocalDataDao.bulkInsert(localEntries);
                }));

        LiveData<UserEntry> userNetworkData = mediaNetworkDataSource.getCurrentUser();

        userNetworkData.observeForever(userEntry ->
                mExecutors.diskIO().execute(() -> {
                    deleteUserData();
                    mUserDao.insertUser(userEntry);
                })
        );
    }

    synchronized static LocalDataRepository getInstance(
            LocalDataDao localDataDao,
            UserDao userDao,
            MediaNetworkDataSource mediaNetworkDataSource,
            AppExecutors executors) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new LocalDataRepository(localDataDao,
                        userDao,
                        mediaNetworkDataSource,
                        executors);
            }
        }
        return sInstance;
    }

    private synchronized void initializeData() {

        mExecutors.diskIO().execute(this::startFetchMedia);
    }

    public LiveData<List<LocalEntry>> getAll() {
        initializeData();
        return mLocalDataDao.getAll();
    }

    private int deleteOldData() {
        return mLocalDataDao.deleteTable();
    }

    private void startFetchMedia() {
        mMediaNetworkDataSource.fetchMedia();
    }

    public LiveData<UserEntry> getUser() {
        initializeData();
        return mUserDao.getAll();
    }

    private int deleteUserData() {
        return mUserDao.deleteTable();
    }

    public void getUserFromNetwork(String code) {
        mMediaNetworkDataSource.fetchUser(code);
    }

    public void deleteUser() {

        mExecutors.diskIO().execute(this::deleteUserData);
        mExecutors.diskIO().execute(this::deleteOldData);
        mMediaNetworkDataSource.deletePreferences();

    }

}
