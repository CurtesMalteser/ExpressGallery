package com.curtesmalteser.expressgallery.retrofit;

import com.curtesmalteser.expressgallery.api.Datum;
import com.curtesmalteser.expressgallery.api.ModelAPI;
import com.curtesmalteser.expressgallery.api.TokenModel;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by António "Curtes Malteser" Bastião on 13/03/2018.
 */


public interface MediaAPIInterface {
    @GET("users/self/media/recent/")
    Call<Datum> getMedia(@Query("access_token") String accessToken);

    @FormUrlEncoded
    @POST("oauth/access_token")
    Call<TokenModel> getAuth(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("grant_type") String grantType,
            @Field("redirect_uri") String redirectUri,
            @Field("code") String code
    );
}
