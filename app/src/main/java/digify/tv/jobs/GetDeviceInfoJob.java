package digify.tv.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.squareup.otto.Bus;

import javax.inject.Inject;
import javax.inject.Provider;

import digify.tv.DigifyApp;
import digify.tv.api.DigifyApiService;
import digify.tv.core.PreferenceManager;
import digify.tv.db.models.DeviceInfo;
import digify.tv.ui.viewmodels.ScreenOrientation;
import digify.tv.util.Utils;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    Provider<Realm> database;
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

        digifyApiService.getDevice(Utils.getUniqueDeviceID(getApplicationContext())).enqueue(new Callback<DeviceInfo>() {
            @Override
            public void onResponse(Call<DeviceInfo> call, final Response<DeviceInfo> response) {
                if (response.isSuccessful()) {

                    if (response.body().getMode().equals(ScreenOrientation.Portrait.toString())) {
                        preferenceManager.setPortrait(true);
                    } else {
                        preferenceManager.setPortrait(false);
                    }



                    database.get().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(response.body());
                        }
                    });
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
}
