package digify.tv.core;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Joel on 12/10/2016.
 */

public class PreferenceManager {
    private static SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static final String PREF_FILE_NAME = "careful_preferences";
    public static final String KEY_FIRST = "name";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_BEARER = "bearer";
    private static final String KEY_EMAIL = "email";



    public PreferenceManager(Context context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        editor = this.preferences.edit();
    }

    public void clear() {
        preferences.edit().clear().apply();
    }


    public void setName(String name) {
        editor.putString(KEY_FIRST, name);
        editor.commit();
    }


    public String getName() {
        return preferences.getString(KEY_FIRST, "");
    }

    public String getBearerToken() {
        return preferences.getString(KEY_BEARER, "");
    }

    public void setBearerToken(String bearerToken) {
        editor.putString(KEY_BEARER, bearerToken);
        editor.commit();

    }

    public String getEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    public void setEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.commit();

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

    public void logout() {
        editor.clear();
        editor.commit();
    }
}



