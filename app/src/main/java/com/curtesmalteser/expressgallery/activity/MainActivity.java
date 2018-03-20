package com.curtesmalteser.expressgallery.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.curtesmalteser.expressgallery.R;
import com.curtesmalteser.expressgallery.adapter.ImagesAdapter;
import com.curtesmalteser.expressgallery.data.LocalEntry;
import com.curtesmalteser.expressgallery.data.InjectorUtils;
import com.curtesmalteser.expressgallery.viewmodel.MainActivityViewModelFactory;
import com.curtesmalteser.expressgallery.viewmodel.MainActivityViewModel;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

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

        Uri uri = getIntent().getData();
        if (uri != null) {
            mViewModel.getUserData(uri.getQueryParameter("code"));
            initializeObserveData(savedInstanceState);
        } else {
            initializeObserveData(savedInstanceState);
        }

        imageUser.setOnClickListener(v -> mViewModel.onClickPost());

    }

    private void initializeObserveData(Bundle savedInstanceState) {
        mViewModel.getData().observe(this, localModel -> {
            if (localModel != null) bindView(localModel);

            if (savedInstanceState != null && mRecyclerView != null)
                mRecyclerView.getLayoutManager().onRestoreInstanceState(stateRecyclerView);
        });
    }

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

    private void bindView(List<LocalEntry> localModel) {
        ImagesAdapter listAdapter;
        resultList = (ArrayList<LocalEntry>) localModel;

        listAdapter = new ImagesAdapter(this, resultList, this);
        mRecyclerView.setAdapter(listAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(numberOfColumns(), LinearLayoutManager.VERTICAL));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (stateRecyclerView != null)
        stateRecyclerView = mRecyclerView.getLayoutManager().onSaveInstanceState();
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }
}
