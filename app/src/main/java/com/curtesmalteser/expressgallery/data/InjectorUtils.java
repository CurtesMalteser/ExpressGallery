package com.curtesmalteser.expressgallery.data;

import android.content.Context;

import com.curtesmalteser.expressgallery.AppExecutors;
import com.curtesmalteser.expressgallery.viewmodel.MainActivityViewModelFactory;
import com.curtesmalteser.expressgallery.viewmodel.UserViewModelFactory;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */

public class InjectorUtils {
    public static LocalDataRepository provideRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        MediaNetworkDataSource networkDataSource =
                MediaNetworkDataSource.getInstance(context.getApplicationContext(), executors);
        return LocalDataRepository.getInstance(database.localDataDao(), database.userDao(), networkDataSource, executors);
    }

    public static MainActivityViewModelFactory provideDetailViewModelFactory(Context context) {
        LocalDataRepository repository = provideRepository(context.getApplicationContext());
        return new MainActivityViewModelFactory(context.getApplicationContext(), repository);
    }

    public static UserViewModelFactory provideUserViewModelFactory(Context context) {
        LocalDataRepository repository = provideRepository(context.getApplicationContext());
        return new UserViewModelFactory(context.getApplicationContext(), repository);
    }
}
