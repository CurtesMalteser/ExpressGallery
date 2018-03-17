package com.curtesmalteser.expressgallery.data;

import android.content.Context;

import com.curtesmalteser.expressgallery.AppExecutors;
import com.curtesmalteser.expressgallery.data.AppDatabase;
import com.curtesmalteser.expressgallery.data.LocalDataRepository;
import com.curtesmalteser.expressgallery.data.MediaNetworkDataSource;
import com.curtesmalteser.expressgallery.viewmodel.MainActivityViewModelFactory;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */


public class InjectorUtils {
    public static LocalDataRepository provideRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        MediaNetworkDataSource networkDataSource =
                MediaNetworkDataSource.getInstance(context.getApplicationContext(), executors);
        return LocalDataRepository.getInstance(database.localDataDao(), networkDataSource, executors);
    }

   /* public static MediaNetworkDataSource provideNetworkDataSource(Context context) {
        // This call to provide repository is necessary if the app starts from a service - in this
        // case the repository will not exist unless it is specifically created.
        provideRepository(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        return MediaNetworkDataSource.getInstance(context.getApplicationContext(), executors);
    }*/

    public static MainActivityViewModelFactory provideDetailViewModelFactory(Context context) {
        LocalDataRepository repository = provideRepository(context.getApplicationContext());
        return new MainActivityViewModelFactory(repository);
    }

   /* public static MainViewModelFactory provideMainActivityViewModelFactory(Context context) {
        SunshineRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);
    }*/
}
