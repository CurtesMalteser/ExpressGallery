package com.curtesmalteser.expressgallery.activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.curtesmalteser.expressgallery.BuildConfig;
import com.curtesmalteser.expressgallery.R;
import com.curtesmalteser.expressgallery.api.TokenModel;
import com.curtesmalteser.expressgallery.retrofit.MediaAPI;
import com.curtesmalteser.expressgallery.retrofit.MediaAPIInterface;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ButterKnife.bind(this);
        setSupportActionBar(userToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String redirectURI = "https://com.curtesmalteser.picgallery";

        Uri uri = getIntent().getData();
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
                    if (response.body().getAccessToken() != null) {
                        savePreferences(response.body().getAccessToken());
                        getUserProfilePic(response.body().getUser().getProfilePicture());
                        // tvWelcome.setText();
                        tvFullName.setText(response.body().getUser().getFullName());
                    }
                }

                @Override
                public void onFailure(Call<TokenModel> call, Throwable t) {

                }
            });
        }
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
                        //Try again online if cache failed
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

    /*@Override
    protected void onResume() {
        super.onResume();

        String redirectURI = "https://com.curtesmalteser.picgallery";

        Uri uri = getIntent().getData();
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

    private void savePreferences(String token) {
        SharedPreferences.Editor sharedPreferences = this.getSharedPreferences("pictures_preferences", MODE_PRIVATE).edit();
        sharedPreferences.putString("token", token);
        sharedPreferences.apply();
    }


}
