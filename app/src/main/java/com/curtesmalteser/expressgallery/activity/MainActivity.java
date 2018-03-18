package com.curtesmalteser.expressgallery.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements ImagesAdapter.ListItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.rootLayout)
    ViewGroup rootView;

    @BindView(R.id.my_toolbar)
    Toolbar myToolbar;

    @BindView(R.id.imageUser)
    ImageButton imageUser;

    private ImagesAdapter listAdapter;
    private ArrayList<LocalEntry> resultList = new ArrayList<>();

    private MainActivityViewModel mViewModel;

    private Parcelable stateRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivityViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(this.getApplicationContext());
        mViewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);

        Stetho.initializeWithDefaults(this);

        ButterKnife.bind(this);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mViewModel.getData().observe(this, localModel -> {
            if(localModel != null )bindView(localModel);

             if (savedInstanceState != null && mRecyclerView != null)
                 mRecyclerView.getLayoutManager().onRestoreInstanceState(stateRecyclerView);
        });

        imageUser.setOnClickListener(v -> mViewModel.onClickPost());

    }

  /*  @Override
    protected void onResume() {
        super.onResume();

        String redirectURI = "https://com.curtesmalteser.picgallery";

        uri = getIntent().getData();
        if (uri != null) {
            MediaAPIInterface apiInterface = MediaAPI.getClient(getString(R.string.auth_url)).create(MediaAPIInterface.class);
            Call<TokenModel> call;

            call = apiInterface.getAuth(
                    BuildConfig.CLIENT_ID,
                    BuildConfig.CLIENT_SECRET,
                    "authorization_code",
                    redirectURI,
                    uri.getQueryParameter("code")
            );

            call.enqueue(new Callback<TokenModel>() {
                @Override
                public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                    if (response.body().getAccessToken() != null)
                    savePreferences(response.body().getAccessToken());
                }

                @Override
                public void onFailure(Call<TokenModel> call, Throwable t) {

                }
            });
        }
    }*/

    @Override
    public void onListItemClick(LocalEntry datum) {
        mRecyclerView.addOnItemTouchListener(new ImagesAdapter.RecyclerTouchListener(getApplicationContext(), mRecyclerView, new ImagesAdapter.ClickListener() {
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

   /* private void savePreferences(String token) {
        SharedPreferences.Editor sharedPreferences = this.getSharedPreferences("pictures_preferences", MODE_PRIVATE).edit();
        sharedPreferences.putString("token", token);
        sharedPreferences.apply();
    }*/

    private void bindView(List<LocalEntry> localModel) {
        resultList = (ArrayList<LocalEntry>) localModel;

        listAdapter = new ImagesAdapter(this, resultList, this);
        mRecyclerView.setAdapter(listAdapter);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stateRecyclerView = mRecyclerView.getLayoutManager().onSaveInstanceState();
    }
}
