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
import digify.tv.api.models.UserDeviceModel;
import digify.tv.core.KioskService;
import digify.tv.core.PreferenceManager;
import digify.tv.ui.events.QueueModeEvent;
import digify.tv.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            }

            @Override
            public void onFailure(Call<UserDeviceModel> call, Throwable t) {
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
