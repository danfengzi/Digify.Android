package digify.tv.core;

import android.app.IntentService;
import android.content.Intent;

import com.squareup.otto.Bus;

import java.io.IOException;

import javax.inject.Inject;

import digify.tv.DigifyApp;
import digify.tv.api.DigifyApiService;
import digify.tv.api.models.UserDeviceModel;
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


        Call<UserDeviceModel> request = digifyApiService.checkAssignment(Utils.getUniqueDeviceID(getApplicationContext()));

        request.enqueue(new Callback<UserDeviceModel>() {
            @Override
            public void onResponse(Call<UserDeviceModel> call, Response<UserDeviceModel> response) {


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
                        Timber.e(response.errorBody().string(),null);
                    } catch (IOException e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<UserDeviceModel> call, Throwable t) {
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
