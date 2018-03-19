package com.curtesmalteser.expressgallery.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.curtesmalteser.expressgallery.R;
import com.curtesmalteser.expressgallery.data.InjectorUtils;
import com.curtesmalteser.expressgallery.viewmodel.UserActivityViewModel;
import com.curtesmalteser.expressgallery.viewmodel.UserViewModelFactory;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = UserActivity.class.getSimpleName();

    @BindView(R.id.user_toolbar)
    Toolbar userToolbar;

    @BindView(R.id.profileImage)
    ImageView profileImage;

    @BindView(R.id.tvWelcome)
    TextView tvWelcome;

    @BindView(R.id.tvFullName)
    TextView tvFullName;

    @BindView(R.id.btnLogOut)
    Button btnLogOut;

    @BindView(R.id.imageGalley)
    ImageButton imageGalley;

    private UserActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        UserViewModelFactory factory = InjectorUtils.provideUserViewModelFactory(this.getApplicationContext());

        mViewModel = ViewModelProviders.of(this, factory).get(UserActivityViewModel.class);

        ButterKnife.bind(this);
        setSupportActionBar(userToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Uri uri = getIntent().getData();
        if (uri != null) {
            mViewModel.getUser(uri.getQueryParameter("code"));
        }

        mViewModel.getUser().observe(this, userEntry -> {
            if (userEntry != null) {
                getUserProfilePic(userEntry.getProfilePicture());
                // tvWelcome.setText();
                tvFullName.setText(userEntry.getFullName());
            }
        });

        imageGalley.setOnClickListener(v -> mViewModel.onClickPost());

        btnLogOut.setOnClickListener(v -> {

            // todo -> complete the logout
            Log.d(TAG, "onCreate: clicked");
        });
    }

    private void getUserProfilePic(String url) {
        Picasso.with(this)
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(profileImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(getBaseContext())
                                .load(url)
                                .error(R.drawable.ic_launcher_background)
                                .into(profileImage, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("Picasso", "Could not fetch image");
                                    }
                                });
                    }
                });
    }
}
