package digify.tv.jobs;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import digify.tv.DigifyApp;
import digify.tv.api.DigifyApiService;
import digify.tv.core.KioskService;
import digify.tv.core.PreferenceManager;
import digify.tv.db.models.DeviceInfo;
import digify.tv.ui.events.QueueModeEvent;
import digify.tv.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Joel on 5/3/2017.
 */

public class FetchUserDeviceJob extends Job {

    public static final int PRIORITY = 4;

    @Inject
    DigifyApiService digifyApiService;
    @Inject
    Bus eventBus;
    @Inject
    PreferenceManager preferenceManager;

    public FetchUserDeviceJob() {
        super(new Params(PRIORITY).setDelayMs(3000).requireNetwork().persist());
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        DigifyApp.get(getApplicationContext()).getComponent().inject(this);

        Call<DeviceInfo> request = digifyApiService.getDevice(Utils.getUniqueDeviceID(getApplicationContext()));

        request.enqueue(new Callback<DeviceInfo>() {
            @Override
            public void onResponse(Call<DeviceInfo> call, Response<DeviceInfo> response) {


                if (response.isSuccessful()) {
                    preferenceManager.setKioskMode(response.body().isKioskMode());

                    if (response.body().isKioskMode()) {
                        startKioskMode();
                    } else {
                        stopKioskMode();
                    }

                    preferenceManager.setQueueMode(response.body().isQueueMode());

                    eventBus.post(new QueueModeEvent(response.body().isQueueMode()));

                } else {
                    try {
                        if(response.errorBody()!=null)
                        Timber.e(response.errorBody().string(), null);
                    } catch (Exception e) {
                    }
                }

            }

            @Override
            public void onFailure(Call<DeviceInfo> call, Throwable t) {
            }
        });


    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
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
