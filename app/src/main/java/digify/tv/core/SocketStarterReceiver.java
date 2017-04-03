package digify.tv.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SocketStarterReceiver extends BroadcastReceiver {
    public SocketStarterReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        context.startService(new Intent(context,SocketService.class));
    }
}
