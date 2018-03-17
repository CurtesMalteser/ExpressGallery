package com.curtesmalteser.expressgallery.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.curtesmalteser.expressgallery.api.LocalModel;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */


public class LocalDataViewModel extends ViewModel {

    private MutableLiveData<LocalModel> mLocalModel;

    public LocalDataViewModel() {
        mLocalModel = new MutableLiveData<LocalModel>();
    }

    public MutableLiveData getLocalModel() {
        return mLocalModel;
    }

    public void setLocalModel(LocalModel localModel) {
        mLocalModel.postValue(localModel);
    }
}
