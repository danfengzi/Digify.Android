package digify.tv.core;

import android.app.IntentService;
import android.content.Intent;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import digify.tv.DigifyApp;
import digify.tv.api.DigifyApiService;
import digify.tv.db.models.DeviceInfo;
import digify.tv.jobs.GetDeviceInfoJob;
import digify.tv.ui.events.QueueModeEvent;
import digify.tv.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class GetUserDeviceService extends IntentService {
    @Inject
    DigifyApiService digifyApiService;
    @Inject
    Bus eventBus;
    @Inject
    PreferenceManager preferenceManager;

    public GetUserDeviceService() {
        super(GetDeviceInfoJob.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        DigifyApp.get(getApplicationContext()).getComponent().inject(this);


        Call<DeviceInfo> request = digifyApiService.getDevice(Utils.getUniqueDeviceID(getApplicationContext()));

        request.enqueue(new Callback<DeviceInfo>() {
            @Override
            public void onResponse(Call<DeviceInfo> call, Response<DeviceInfo> response) {


                if (response.isSuccessful()) {
                    preferenceManager.setKioskMode(response.body().isKioskMode());

                    if(response.body().isKioskMode())
                    {
                        startKioskMode();
                    }
                    else
                    {
                        stopKioskMode();
                    }

                    preferenceManager.setQueueMode(response.body().isQueueMode());

                    eventBus.post(new QueueModeEvent(response.body().isQueueMode()));

                }
                else
                {
                    try {
                        if(response.errorBody()!=null)
                        Timber.e(response.errorBody().string(),null);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<DeviceInfo> call, Throwable t) {
                Timber.e(t);
            }
        });


    }

    public void startKioskMode()
    {
        if(!Utils.isMyServiceRunning(KioskService.class,getApplicationContext()))
            getApplicationContext().startService(new Intent(getApplicationContext(), KioskService.class));
    }

    public void stopKioskMode()
    {
        if(Utils.isMyServiceRunning(KioskService.class,getApplicationContext()))
            getApplicationContext().stopService(new Intent(getApplicationContext(), KioskService.class));
    }


}
