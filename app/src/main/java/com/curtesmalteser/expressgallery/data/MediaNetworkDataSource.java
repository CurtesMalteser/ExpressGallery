package com.curtesmalteser.expressgallery.data;

import android.annotation.SuppressLint;
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
import com.curtesmalteser.expressgallery.api.TokenModel;
import com.curtesmalteser.expressgallery.retrofit.MediaAPI;
import com.curtesmalteser.expressgallery.retrofit.MediaAPIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */

class MediaNetworkDataSource {

    private static final String LOG_TAG = MediaNetworkDataSource.class.getSimpleName();

    static final String SHARED_PREFERENCES_NAME = "pictures_preferences";
    static final String TOKEN = "token";

    private static final Object LOCK = new Object();
    @SuppressLint("StaticFieldLeak")
    private static MediaNetworkDataSource sInstance;
    private final Context mContext;

    private final MutableLiveData<ArrayList<LocalEntry>> mDownloadedMedia;
    private final MutableLiveData<UserEntry> mDownloadedUser;
    private final AppExecutors mExecutors;

    private MediaNetworkDataSource(Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
        mDownloadedMedia = new MutableLiveData<>();
        mDownloadedUser = new MutableLiveData<>();
    }

    public static MediaNetworkDataSource getInstance(Context context, AppExecutors executors) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MediaNetworkDataSource(context.getApplicationContext(), executors);
            }
        }
        return sInstance;
    }

    LiveData<ArrayList<LocalEntry>> getRecentMedia() {
        return mDownloadedMedia;
    }

    void fetchMedia() {
        mExecutors.networkIO().execute(() -> {
            SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
            String token = preferences.getString(TOKEN, "noValues");
            if (!token.equals("noValues")) {
                MediaAPIInterface apiInterface = MediaAPI.getClient(mContext.getString(R.string.base_url)).create(MediaAPIInterface.class);
                Call<Datum> call;
                call = apiInterface.getMedia(token);
                call.enqueue(new Callback<Datum>() {
                    @Override
                    public void onResponse(Call<Datum> call, Response<Datum> response) {
                        if (response.code() == 200) {
                            response.body().getData().size();

                            ArrayList<LocalEntry> entry = new ArrayList<>();

                            for (Datum datum : response.body().getData()) {
                                entry.add(new LocalEntry(datum.getImages().getStandardResolution().getUrl(),
                                        datum.getLikes().getCount(),
                                        datum.getComments().getCount()));
                            }
                            mDownloadedMedia.postValue(entry);
                        } else if (response.code() == 400) {
                            requestAuthentication();
                        }
                    }

                    @Override
                    public void onFailure(Call<Datum> call, Throwable t) {

                    }
                });
            } else {
                requestAuthentication();
            }
        });
    }

    private void requestAuthentication() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.instagram.com/oauth/authorize/" + "?client_id=" + BuildConfig.CLIENT_ID + "&redirect_uri=" + mContext.getString(R.string.redirect_uri) + "&response_type=code"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }


    LiveData<UserEntry> getCurrentUser() {
        return mDownloadedUser;
    }

    void fetchUser(String code) {
        Log.d("XPTO", "fetchUser: " + code);
        String redirectURI = "https://com.curtesmalteser.picgallery";

        MediaAPIInterface apiInterface = MediaAPI.getClient(mContext.getString(R.string.auth_url)).create(MediaAPIInterface.class);

        if (!code.equals(null)) {
            Call<TokenModel> call;
            call = apiInterface.getAuth(
                    BuildConfig.CLIENT_ID,
                    BuildConfig.CLIENT_SECRET,
                    "authorization_code",
                    redirectURI,
                    code
            );

            call.enqueue(new Callback<TokenModel>() {
                @Override
                public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                    if (response.code() == 200) {
                        if (response.body().getAccessToken() != null) {
                            savePreferences(response.body().getAccessToken());
                            mDownloadedUser.postValue(new UserEntry(response.body().getUser().getId(),
                                    response.body().getUser().getFullName(),
                                    response.body().getUser().getUsername(),
                                    response.body().getUser().getProfilePicture()
                            ));
                        }
                    }
                }

                @Override
                public void onFailure(Call<TokenModel> call, Throwable t) {

                }
            });
        }
    }

    private void savePreferences(String token) {
        SharedPreferences.Editor sharedPreferences = mContext.getSharedPreferences("pictures_preferences", MODE_PRIVATE).edit();
        sharedPreferences.putString("token", token);
        sharedPreferences.apply();
    }

    void deletePreferences() {
        SharedPreferences.Editor sharedPreferences = mContext.getSharedPreferences("pictures_preferences", MODE_PRIVATE).edit();
        sharedPreferences.clear().apply();
    }
}



