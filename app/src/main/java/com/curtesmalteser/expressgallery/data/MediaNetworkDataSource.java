package com.curtesmalteser.expressgallery.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.curtesmalteser.expressgallery.AppExecutors;
import com.curtesmalteser.expressgallery.BuildConfig;
import com.curtesmalteser.expressgallery.R;
import com.curtesmalteser.expressgallery.api.Datum;
import com.curtesmalteser.expressgallery.api.LocalEntry;
import com.curtesmalteser.expressgallery.retrofit.MediaAPI;
import com.curtesmalteser.expressgallery.retrofit.MediaAPIInterface;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */


public class MediaNetworkDataSource {

    // The number of days we want our API to return, set to 14 days or two weeks
    public static final int NUM_DAYS = 14;
    private static final String LOG_TAG = MediaNetworkDataSource.class.getSimpleName();

    // Interval at which to sync with the weather. Use TimeUnit for convenience, rather than
    // writing out a bunch of multiplication ourselves and risk making a silly mistake.
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String SUNSHINE_SYNC_TAG = "sunshine-sync";

    public static final String SHARED_PREFERENCES_NAME = "pictures_preferences";
    public static final String TOKEN = "token";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static MediaNetworkDataSource sInstance;
    private final Context mContext;

    // LiveData storing the latest downloaded weather forecasts
    private final MutableLiveData<ArrayList<LocalEntry>> mDownloadedMedia;
    private final AppExecutors mExecutors;

    private MediaNetworkDataSource(Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
        mDownloadedMedia = new MutableLiveData<ArrayList<LocalEntry>>();
    }

    /**
     * Get the singleton for this class
     */
    public static MediaNetworkDataSource getInstance(Context context, AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MediaNetworkDataSource(context.getApplicationContext(), executors);
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    public LiveData<ArrayList<LocalEntry>> getCurrentWeatherForecasts() {
        return mDownloadedMedia;
    }

    public void startFetchWeatherService() {
        Intent intentToFetch = new Intent(mContext, MediaSyncIntentService.class);
        mContext.startService(intentToFetch);
        Log.d(LOG_TAG, "Service created");
    }


    public void scheduleRecurringFetchWeatherSync() {
        Driver driver = new GooglePlayDriver(mContext);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Create the Job to periodically sync Sunshine
        Job syncSunshineJob = dispatcher.newJobBuilder()

                .setService(MediaFirebaseJobService.class)

                .setTag(SUNSHINE_SYNC_TAG)

                .setConstraints(Constraint.ON_ANY_NETWORK)

                .setLifetime(Lifetime.FOREVER)

                .setRecurring(true)

                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))

                .setReplaceCurrent(true)

                .build();

        // Schedule the Job with the dispatcher
        dispatcher.schedule(syncSunshineJob);
        Log.d(LOG_TAG, "Job scheduled");
    }


    void fetchWeather() {
        Log.d(LOG_TAG, "Fetch weather started");
        mExecutors.networkIO().execute(() -> {
            SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
            String token = preferences.getString(TOKEN, "noValues");
            if (preferences != null && !token.equals("noValues")) {
                MediaAPIInterface apiInterface = MediaAPI.getClient(mContext.getString(R.string.base_url)).create(MediaAPIInterface.class);
                Call<Datum> call;


                call = apiInterface.getMedia(token);
                call.enqueue(new Callback<Datum>() {
                    @Override
                    public void onResponse(Call<Datum> call, Response<Datum> response) {
                        response.body().getData().size();

                        ArrayList<LocalEntry> entry = new ArrayList<>();

                        for (Datum datum : response.body().getData()) {
                            Log.d("AJDB", "URL: " + datum.getImages().getLowResolution().getUrl());
                            entry.add(new LocalEntry(datum.getImages().getStandardResolution().getUrl(),
                                    datum.getLikes().getCount(),
                                    datum.getComments().getCount()));
                        }
                        mDownloadedMedia.postValue(entry);
                    }

                    @Override
                    public void onFailure(Call<Datum> call, Throwable t) {

                    }
                });
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.instagram.com/oauth/authorize/" + "?client_id=" + BuildConfig.CLIENT_ID + "&redirect_uri=" + mContext.getString(R.string.redirect_uri) + "&response_type=code"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }
}



