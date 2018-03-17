package com.curtesmalteser.expressgallery.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.curtesmalteser.expressgallery.BuildConfig;
import com.curtesmalteser.expressgallery.R;
import com.curtesmalteser.expressgallery.adapter.ImagesAdapter;
import com.curtesmalteser.expressgallery.api.LocalEntry;
import com.curtesmalteser.expressgallery.api.TokenModel;
import com.curtesmalteser.expressgallery.data.AppDatabase;
import com.curtesmalteser.expressgallery.data.InjectorUtils;
import com.curtesmalteser.expressgallery.retrofit.MediaAPI;
import com.curtesmalteser.expressgallery.retrofit.MediaAPIInterface;
import com.curtesmalteser.expressgallery.viewmodel.MainActivityViewModelFactory;
import com.curtesmalteser.expressgallery.viewmodel.MainActivityViewModel;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;


import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements ImagesAdapter.ListItemClickListener {

    private String clientID = BuildConfig.CLIENT_ID;
    private String clientSecret = BuildConfig.CLIENT_SECRET;
    private String redirectURI = "https://com.curtesmalteser.picgallery";

    private Uri uri;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.rootLayout)
    ViewGroup rootView;

    private ImagesAdapter listAdapter;
    private ArrayList<LocalEntry> resultList = new ArrayList<>();

    AppDatabase db;
    private MainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivityViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(this.getApplicationContext());
        mViewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);

        Stetho.initializeWithDefaults(this);

        ButterKnife.bind(this);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "media.db")
                .allowMainThreadQueries()
                .build();




        mViewModel.getWeather().observe(this, localModel -> {
            // TODO --->>> Update the UI
           // if(localModel != null )bindWeatherToUI(localModel);
            Log.d("AJDB", "size: " + localModel.size());
            resultList = (ArrayList<LocalEntry>) localModel;
            //listAdapter.notifyDataSetChanged();

            listAdapter = new ImagesAdapter(this, resultList, this);
            recyclerView.setAdapter(listAdapter);

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        uri = getIntent().getData();
        if (uri != null) {
            MediaAPIInterface apiInterface = MediaAPI.getClient(getString(R.string.auth_url)).create(MediaAPIInterface.class);
            Call<TokenModel> call;

            call = apiInterface.getAuth(
                    clientID,
                    clientSecret,
                    "authorization_code",
                    redirectURI,
                    uri.getQueryParameter("code")
            );

            call.enqueue(new Callback<TokenModel>() {
                @Override
                public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                    if (response != null)
                    savePreferences(response.body().getAccessToken());
                }

                @Override
                public void onFailure(Call<TokenModel> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onListItemClick(LocalEntry datum) {
        recyclerView.addOnItemTouchListener(new ImagesAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new ImagesAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", resultList);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void savePreferences(String token) {
        SharedPreferences.Editor sharedPreferences = this.getSharedPreferences("pictures_preferences", MODE_PRIVATE).edit();
        sharedPreferences.putString("token", token);
        sharedPreferences.apply();
    }
}
