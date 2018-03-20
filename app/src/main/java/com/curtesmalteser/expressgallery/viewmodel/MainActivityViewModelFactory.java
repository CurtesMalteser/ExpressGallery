package com.curtesmalteser.expressgallery.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;

import com.curtesmalteser.expressgallery.data.InjectorUtils;
import com.curtesmalteser.expressgallery.data.LocalDataRepository;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */

public class MainActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final LocalDataRepository mRepository;

    private final Context mApplication;

    public MainActivityViewModelFactory(Context application, LocalDataRepository repository) {
        this.mApplication = application;
        this.mRepository = repository;

    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainActivityViewModel(mApplication, mRepository);
    }
}
