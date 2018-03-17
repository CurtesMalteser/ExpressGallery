package com.curtesmalteser.expressgallery.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.curtesmalteser.expressgallery.data.LocalDataRepository;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */


public class MainActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final LocalDataRepository mRepository;

    public MainActivityViewModelFactory(LocalDataRepository repository) {
        this.mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainActivityViewModel(mRepository);
    }
}
