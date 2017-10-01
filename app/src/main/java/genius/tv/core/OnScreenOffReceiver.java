package genius.tv.core;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import javax.inject.Inject;

import genius.tv.DigifyApp;
import genius.tv.injection.component.ApplicationComponent;

public class OnScreenOffReceiver extends BroadcastReceiver {

    @Inject
    PreferenceManager preferenceManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        applicationComponent(context).inject(this);

        if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
            DigifyApp ctx = (DigifyApp) context.getApplicationContext();
            // is Kiosk Mode active?
                wakeUpDevice(ctx);

        }
    }

    private void wakeUpDevice(DigifyApp digifyApp) {
        PowerManager.WakeLock wakeLock = digifyApp.getWakeLock(); // get WakeLock reference via AppContext
        if (wakeLock.isHeld()) {
            wakeLock.release(); // release old wake lock
        }

        // create a new wake lock...
        wakeLock.acquire();

        // ... and release again
        wakeLock.release();
    }

    protected ApplicationComponent applicationComponent(Context context) {
        return DigifyApp.get(context).getComponent();
    }


}