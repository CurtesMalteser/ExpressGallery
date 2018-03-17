package com.curtesmalteser.expressgallery.data;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by António "Curtes Malteser" Bastião on 17/03/2018.
 */


public class SharedPreferencesManager {
    public static final String SHARED_PREFERENCES_NAME = "pictures_preferences";
    public static final String TOKEN = "token";

    private static SharedPreferencesManager instance;

    private SharedPreferences privateSharedPreferences;

    private SharedPreferencesManager(Context context) {

        this.privateSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }

    public static SharedPreferencesManager getInstance(Context context) {

        synchronized (SharedPreferencesManager.class) {
            if (instance == null) {
                instance = new SharedPreferencesManager(context);
            }
            return instance;
        }
    }

    private void storeStringInSharedPreferences(String key, String content) {

        SharedPreferences.Editor editor = privateSharedPreferences.edit();
        editor.putString(key, content);
        editor.apply();
    }

    private String getStringFromSharedPreferences(String key) {

        return privateSharedPreferences.getString(key, "noValues");
    }

    public void storeToken(String nickname) {

        storeStringInSharedPreferences(TOKEN, nickname);
    }

    public String getToken() {

        return getStringFromSharedPreferences(TOKEN);
    }

}
