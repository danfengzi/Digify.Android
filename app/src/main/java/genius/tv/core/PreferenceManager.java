package genius.tv.core;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Joel on 12/10/2016.
 */

public class PreferenceManager {
    private static SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static final String PREF_FILE_NAME = "digify_preferences";
    public static final String KEY_NAME = "name";
    public static final String KEY_QUEUE_MESSAGE = "queue_message";
    public static final String KEY_QUEUE = "queue";
    public static final String KEY_CUSTOMER_ID = "customer_id";
    public static final String KEY_TENANT = "tenant";
    public static final String KEY_CODE = "code";
    public static final String KEY_ORIENTATION = "orientation";
    public static final String KEY_SETUP = "setup";
    public static final String KEY_KIOSK_MODE = "kiosk_mode";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_BASE_URL = "baseUrl";
    private static final String KEY_IMAGE_DURATION = "image_duration";


    public PreferenceManager(Context context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        editor = this.preferences.edit();
    }

    public void clear() {
        preferences.edit().clear().apply();
    }


    public void setName(String name) {
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public void setQueueMessage(String message) {
        editor.putString(KEY_QUEUE_MESSAGE, message);
        editor.commit();
    }


    public void setCustomerId(String id) {
        editor.putString(KEY_CUSTOMER_ID, id);
        editor.commit();
    }


    public void setTenant(String name) {
        editor.putString(KEY_TENANT, name);
        editor.commit();
    }

    public void setImageDuration(int duration) {
        editor.putInt(KEY_IMAGE_DURATION, duration);
        editor.commit();
    }

    public int getImageDuration() {

        return preferences.getInt(KEY_IMAGE_DURATION, 20000);
    }

    public void setBaseUrl(String baseUrl) {
        editor.putString(KEY_BASE_URL, baseUrl);
        editor.commit();
    }

    public void setCode(String code) {
        editor.putString(KEY_CODE, code);
        editor.commit();
    }



    public String getCustomerId() {
        return preferences.getString(KEY_CUSTOMER_ID, "");
    }

    public String getName() {
        return preferences.getString(KEY_NAME, "");
    }


    public String getQueueMessage() {
        return preferences.getString(KEY_QUEUE_MESSAGE, ",Could you please come to the counter.");
    }

    public String getCode() {
        return preferences.getString(KEY_CODE, "");
    }


    public String getTenant() {
        return preferences.getString(KEY_TENANT, "");
    }


    public String getBaseUrl() {
        return preferences.getString(KEY_BASE_URL, "");
    }


    /**
     * Checks to see if the user is logged in.
     *
     * @return
     */

    public boolean isLoggedIn() {
        return preferences.getBoolean(IS_LOGIN, false);
    }

    /**
     * sets current status of user.
     *
     * @param status
     */
    public void setLoggedInStatus(boolean status) {
        editor.putBoolean(IS_LOGIN, status);
        editor.commit();
    }

    public boolean isKioskModeEnabled() {
        return preferences.getBoolean(KEY_KIOSK_MODE, false);
    }

    public boolean isQueueModeEnabled() {
        return preferences.getBoolean(KEY_QUEUE, true);
    }



    public void setKioskMode(boolean status) {
        editor.putBoolean(KEY_KIOSK_MODE, status);
        editor.commit();
    }

    public void setQueueMode(boolean status) {
        editor.putBoolean(KEY_QUEUE, status);
        editor.commit();
    }

    public void setInitialSetup() {
        editor.putBoolean(KEY_SETUP, true);
        editor.commit();
    }

    public void setPortrait(Boolean value) {
        editor.putBoolean(KEY_ORIENTATION, value);
    }

    public Boolean isPortrait() {
        return preferences.getBoolean(KEY_ORIENTATION, false);
    }


    public Boolean isInitialSetup() {
        return !preferences.getBoolean(KEY_SETUP, false);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }
}