package nobi.tv.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nobi.tv.ui.activities.SplashActivity;

public class StartupReceiver extends BroadcastReceiver {
    public static final String FROM_STARTUP_RECEIVER="startup_receiver";
    public StartupReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent activity = new Intent(context, SplashActivity.class);
        activity.putExtra(FROM_STARTUP_RECEIVER,true);
        activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(activity);
    }
}
