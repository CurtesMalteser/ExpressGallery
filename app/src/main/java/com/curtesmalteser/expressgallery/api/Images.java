
package com.curtesmalteser.expressgallery.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Images {

    @SerializedName("standard_resolution")
    @Expose
    private StandardResolution standardResolution;

    public StandardResolution getStandardResolution() {
        return standardResolution;
    }

}
