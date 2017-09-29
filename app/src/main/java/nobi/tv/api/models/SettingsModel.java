package nobi.tv.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Joel on 2/13/2017.
 */

public class SettingsModel {
    @SerializedName("DEFAULT_IMAGE_DURATION")
    private int defaultImageDurationInSeconds;

    public int getDefaultImageDurationInSeconds() {
        return defaultImageDurationInSeconds;
    }

    public void setDefaultImageDurationInSeconds(int defaultImageDurationInSeconds) {
        this.defaultImageDurationInSeconds = defaultImageDurationInSeconds;
    }
}
