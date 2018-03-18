package com.curtesmalteser.expressgallery.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.curtesmalteser.expressgallery.activity.UserActivity;
import com.curtesmalteser.expressgallery.api.LocalEntry;
import com.curtesmalteser.expressgallery.data.LocalDataRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */


public class MainActivityViewModel extends ViewModel {


    private final LiveData<List<LocalEntry>> mData;

    private final Context mContext;

    private final LocalDataRepository mRepository;

    public MainActivityViewModel(Context context, LocalDataRepository repository) {
        mContext = context;
        mRepository = repository;
        mData = mRepository.getAll();
    }

    public LiveData<List<LocalEntry>> getData() {
        return mData;
    }

    public void onClickPost() {Log.d("AJDB", "onClick: you'r there");
        Intent intent = new Intent(mContext.getApplicationContext(), UserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
