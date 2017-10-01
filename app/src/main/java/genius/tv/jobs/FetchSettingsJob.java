package genius.tv.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import genius.tv.GeniusApp;
import genius.tv.api.DigifyApiService;
import genius.tv.api.models.SettingsModel;
import genius.tv.core.PreferenceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Joel on 2/13/2017.
 */

public class FetchSettingsJob extends Job {
    public static final int PRIORITY = 2;

    @Inject
    DigifyApiService digifyApiService;
    @Inject
    Bus eventBus;
    @Inject
    PreferenceManager preferenceManager;

    public FetchSettingsJob() {
        super(new Params(PRIORITY).setDelayMs(3000).requireNetwork().persist());
    }

    @Override
    public void onAdded() {


    }

    @Override
    public void onRun() throws Throwable {
        GeniusApp.get(getApplicationContext()).getComponent().inject(this);


        digifyApiService.getOrganizationSettings().enqueue(new Callback<SettingsModel>() {
            @Override
            public void onResponse(Call<SettingsModel> call, Response<SettingsModel> response) {
                if (response.isSuccessful()) {
                    preferenceManager.setImageDuration(response.body().getDefaultImageDurationInSeconds() * 1000);
                }
            }

            @Override
            public void onFailure(Call<SettingsModel> call, Throwable t) {

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
}
