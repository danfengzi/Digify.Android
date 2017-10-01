package genius.tv.ui.activities;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.intrications.systemuihelper.SystemUiHelper;

import javax.inject.Inject;

import genius.tv.GeniusApp;
import genius.tv.R;
import genius.tv.core.AdminReceiver;
import genius.tv.core.PreferenceManager;
import genius.tv.injection.component.ApplicationComponent;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Joel on 12/15/2016.
 */

public class BaseActivity extends FragmentActivity {

    protected DevicePolicyManager mDpm;

    @Inject
    PreferenceManager preferenceManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applicationComponent().inject(this);

        if (!preferenceManager.isLoggedIn() && isAutoLogOutEnabled()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }

        if (preferenceManager.isPortrait()) {
            if (getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            if (getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        setupInAppbasedKioskMode();
    }

    protected ApplicationComponent applicationComponent() {
        return GeniusApp.get(this).getComponent();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    protected void setupInAppbasedKioskMode() {
        if (preferenceManager.isKioskModeEnabled()) {
            hideSystemUI();
        }
    }

    protected void setupAndroidBasedKioskMode() {
        if (!preferenceManager.isKioskModeEnabled()) {
            ComponentName deviceAdmin = new ComponentName(this, AdminReceiver.class);
            mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (!mDpm.isAdminActive(deviceAdmin)) {
                Log.e("Kiosk Mode Error", getString(R.string.not_device_admin));
            }

            if (mDpm.isDeviceOwnerApp(getPackageName())) {
                mDpm.setLockTaskPackages(deviceAdmin, new String[]{getPackageName()});
            } else {
                Log.e("Kiosk Mode Error", getString(R.string.not_device_owner));
            }

            enableKioskMode(true);
            //TODO : for clear device Owner
//        } else {
//            mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
//            mDpm.clearDeviceOwnerApp(getPackageName());
        }

        hideSystemUI();
    }

    protected void enableKioskMode(boolean enabled) {
        try {
            if (enabled) {
                if (mDpm.isLockTaskPermitted(this.getPackageName())) {
                    preferenceManager.setKioskMode(true);
                    startLockTask();
                } else {
                    preferenceManager.setKioskMode(false);
                    Log.e("Kiosk Mode Error", getString(R.string.kiosk_not_permitted));
                }
            } else {
                preferenceManager.setKioskMode(false);
                stopLockTask();
            }
        } catch (Exception e) {
            preferenceManager.setKioskMode(false);
            // TODO: Log and handle appropriately
            Log.e("Kiosk Mode Error", e.getMessage());
        }
    }

    protected void hideSystemUI() {

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        SystemUiHelper uiHelper = new SystemUiHelper(this, SystemUiHelper.LEVEL_IMMERSIVE, flags);
        uiHelper.hide();
    }

    /**
     * Useful so that the login activity can extend this without triggering log out.
     * @return
     */
    public boolean isAutoLogOutEnabled() {
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus && preferenceManager.isKioskModeEnabled()) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }



    @Override
    protected void onStop() {
        // allow backup authorized devices only

        super.onStop();
    }
}
