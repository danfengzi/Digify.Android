package genius.tv.core;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import javax.inject.Inject;

import genius.tv.GeniusApp;
import genius.tv.injection.component.ApplicationComponent;

public class OnScreenOffReceiver extends BroadcastReceiver {

    @Inject
    PreferenceManager preferenceManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        applicationComponent(context).inject(this);

        if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
            GeniusApp ctx = (GeniusApp) context.getApplicationContext();
            // is Kiosk Mode active?
                wakeUpDevice(ctx);

        }
    }

    private void wakeUpDevice(GeniusApp geniusApp) {
        PowerManager.WakeLock wakeLock = geniusApp.getWakeLock(); // get WakeLock reference via AppContext
        if (wakeLock.isHeld()) {
            wakeLock.release(); // release old wake lock
        }

        // create a new wake lock...
        wakeLock.acquire();

        // ... and release again
        wakeLock.release();
    }

    protected ApplicationComponent applicationComponent(Context context) {
        return GeniusApp.get(context).getComponent();
    }


}