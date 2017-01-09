package digify.tv.core;

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
    private static final String IS_LOGIN = "IsLoggedIn";


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


    public String getName() {
        return preferences.getString(KEY_NAME, "");
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



