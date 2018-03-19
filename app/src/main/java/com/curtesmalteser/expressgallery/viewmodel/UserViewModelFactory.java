package com.curtesmalteser.expressgallery.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.curtesmalteser.expressgallery.data.LocalDataRepository;

/**
 * Created by António "Curtes Malteser" Bastião on 19/03/2018.
 */


public class UserViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final LocalDataRepository mRepository;

    private final Context mApplication;

    public UserViewModelFactory(Context application, LocalDataRepository mRepository) {
        this.mRepository = mRepository;
        this.mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UserActivityViewModel(mApplication, mRepository);
    }


}
