package digify.tv.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import digify.tv.DigifyApp;
import digify.tv.api.DigifyApiService;
import digify.tv.core.PreferenceManager;

/**
 * Created by Joel on 2/13/2017.
 */

public class GetDeviceInfoJob extends Job {
    public static final int PRIORITY = 1;

    @Inject
    DigifyApiService digifyApiService;
    @Inject
    Bus eventBus;
    @Inject
    PreferenceManager preferenceManager;

    public GetDeviceInfoJob() {
        super(new Params(PRIORITY).requireNetwork().persist());
    }

    @Override
    public void onAdded() {


    }

    @Override
    public void onRun() throws Throwable {
        DigifyApp.get(getApplicationContext()).getComponent().inject(this);

        eventBus.register(this);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
