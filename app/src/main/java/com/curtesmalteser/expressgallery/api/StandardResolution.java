
package com.curtesmalteser.expressgallery.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StandardResolution {

    @SerializedName("url")
    @Expose
    private String url;

    public String getUrl() {
        return url;
    }



}
