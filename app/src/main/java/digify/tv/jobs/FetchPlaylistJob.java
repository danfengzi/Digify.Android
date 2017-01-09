package digify.tv.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import digify.tv.DigifyApp;
import digify.tv.api.DigifyApiService;
import digify.tv.db.models.Media;
import digify.tv.ui.events.PlaylistUpdatedEvent;
import digify.tv.util.Utils;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Joel on 1/6/2017.
 */

public class FetchPlaylistJob extends Job {
    public static final int PRIORITY = 1;

    @Inject
    DigifyApiService digifyApiService;
    @Inject
    Realm database;
    @Inject
    Bus eventBus;

    public FetchPlaylistJob() {
        super(new Params(PRIORITY).requireNetwork().persist());
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {

        DigifyApp.get(getApplicationContext()).getComponent().inject(this);

        eventBus.register(this);

        Call<List<Media>> request = digifyApiService.getDevicePlaylist(Utils.getUniquePsuedoID());

        request.enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(Call<List<Media>> call, Response<List<Media>> response) {

                if (response.body() != null) {
                    for (final Media media : response.body()) {

                        database.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealmOrUpdate(media);
                            }
                        });
                    }

                    eventBus.post(new PlaylistUpdatedEvent());
                }

                eventBus.unregister(this);
            }

            @Override
            public void onFailure(Call<List<Media>> call, Throwable t) {
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
