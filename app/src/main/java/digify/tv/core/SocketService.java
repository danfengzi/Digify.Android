package digify.tv.core;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import javax.inject.Inject;

import digify.tv.DigifyApp;
import digify.tv.injection.component.ApplicationComponent;
import timber.log.Timber;

public final class SocketService extends Service {

    PowerManager.WakeLock wl = null;
    boolean manualDestroy = false;

    @Inject
    Pusher pusher;
    @Inject
    PreferenceManager preferenceManager;

    public SocketService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent().inject(this);

        Timber.v("Socket Service has been created.");

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SubscribeAtBoot");
        if (wl != null) {
            wl.acquire();

            Timber.v("Partial Wake Lock : " + wl.isHeld());
        }


        pusher.connect();

        Channel channel = pusher.subscribe(preferenceManager.getTenant());

        channel.bind("", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {

            }
        });

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (wl != null) {
            wl.release();
            Log.i(SocketService.class.getName(), "Partial Wake Lock : " + wl.isHeld());
            wl = null;
        }


        if (!manualDestroy) {
            Intent intent = new Intent("digify.tv.socket.start");
            sendBroadcast(intent);

        }
    }

    protected ApplicationComponent applicationComponent() {
        return DigifyApp.get(getApplicationContext()).getComponent();
    }


}
