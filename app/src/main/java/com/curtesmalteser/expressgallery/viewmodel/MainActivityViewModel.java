package com.curtesmalteser.expressgallery.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.curtesmalteser.expressgallery.api.LocalEntry;
import com.curtesmalteser.expressgallery.data.LocalDataRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */


public class MainActivityViewModel extends ViewModel {


    private final LiveData<List<LocalEntry>> mWeather;

    private final LocalDataRepository mRepository;

    public MainActivityViewModel(LocalDataRepository repository) {
        mRepository = repository;
        mWeather = mRepository.getAll();
    }

    public LiveData<List<LocalEntry>> getWeather() {
        return mWeather;
    }
}
