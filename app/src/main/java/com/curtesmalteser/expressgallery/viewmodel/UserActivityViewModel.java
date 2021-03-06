package com.curtesmalteser.expressgallery.viewmodel;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;

import com.curtesmalteser.expressgallery.activity.MainActivity;
import com.curtesmalteser.expressgallery.data.LocalDataRepository;
import com.curtesmalteser.expressgallery.data.UserEntry;

/**
 * Created by António "Curtes Malteser" Bastião on 18/03/2018.
 */

public class UserActivityViewModel extends ViewModel {

    private LiveData<UserEntry> mUserEntry;

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;

    private final LocalDataRepository mRepository;

    public UserActivityViewModel(Context context, LocalDataRepository repository) {
        mContext = context;
        mRepository = repository;
        mUserEntry = mRepository.getUser();
    }

    public LiveData<UserEntry> getUser() {
        return mUserEntry;
    }

    public void onClickPost() {
        Intent intent = new Intent(mContext.getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void deleteUser() {
        mRepository.deleteUser();
    }
}
