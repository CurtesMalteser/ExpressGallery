package com.curtesmalteser.expressgallery.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.curtesmalteser.expressgallery.api.LocalEntry;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */


public class LocalDataViewModel extends ViewModel {

    private MutableLiveData<LocalEntry> mLocalModel;

    public LocalDataViewModel() {
        mLocalModel = new MutableLiveData<LocalEntry>();
    }

    public MutableLiveData getLocalModel() {
        return mLocalModel;
    }

    public void setLocalModel(LocalEntry localModel) {
        mLocalModel.postValue(localModel);
    }
}
