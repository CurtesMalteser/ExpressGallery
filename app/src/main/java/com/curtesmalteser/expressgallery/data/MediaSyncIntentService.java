package com.curtesmalteser.expressgallery.data;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */


public class MediaSyncIntentService extends IntentService {
    private static final String LOG_TAG = MediaSyncIntentService.class.getSimpleName();

    public MediaSyncIntentService() {
        super("MediaSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "Intent service started");
        MediaNetworkDataSource networkDataSource =
                InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        networkDataSource.fetchWeather();
    }
}
