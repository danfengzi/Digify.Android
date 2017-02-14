package digify.tv.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.ButterKnife;
import digify.tv.R;
import digify.tv.api.DigifyApiService;
import digify.tv.core.PreferenceManager;
import digify.tv.db.MediaRepository;
import digify.tv.db.models.DeviceInfo;
import digify.tv.db.models.Media;
import digify.tv.jobs.FetchPlaylistJob;
import digify.tv.ui.viewmodels.ScreenOrientation;
import digify.tv.util.Utils;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import jonathanfinerty.once.Once;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static digify.tv.util.Utils.getUniqueDeviceID;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends BaseActivity {

    @Inject
    JobManager jobManager;
    @Inject
    PreferenceManager preferenceManager;
    @Inject
    Bus eventBus;
    @Inject
    MediaRepository mediaRepository;
    @Inject
    DigifyApiService digifyApiService;
    @Inject
    Provider<Realm> database;


    @BindView(R.id.loading_view)
    AVLoadingIndicatorView loadingView;
    @BindView(R.id.status)
    TextView status;


    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Once.initialise(this);

        applicationComponent().inject(this);

        eventBus.register(this);

        if (!preferenceManager.isLoggedIn())
            return;

        fetchPlaylist();
        scheduleMomentarilyJobs();

    }

    public void fetchPlaylist() {
        Toasty.info(this, "Checking for updates...", Toast.LENGTH_SHORT, true).show();
        jobManager.addJobInBackground(new FetchPlaylistJob());

    }

    private void scheduleMomentarilyJobs() {
        if (!Once.beenDone(TimeUnit.MINUTES, 10, "PLAYLIST_SYNC")) {
            jobManager.addJobInBackground(new FetchPlaylistJob());
        }
    }

    public void checkDeviceInfo()
    {
        digifyApiService.getDevice(getUniqueDeviceID(this)).enqueue(new Callback<DeviceInfo>() {
            @Override
            public void onResponse(Call<DeviceInfo> call, final Response<DeviceInfo> response) {
                if(response.isSuccessful())
                {
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


    private void getDeviceInfoFromDb()
    {
        DeviceInfo deviceInfo = database.get().where(DeviceInfo.class).findFirst();

        if(deviceInfo!=null)
        {
            if(deviceInfo.getMode().equals(ScreenOrientation.Landscape.toString()))
            {

            }
        }
    }

    private void startPlayback() {

        List<Media> list = mediaRepository.getMedia();

        for (Media media : list) {

            File file = Utils.getMediaFile(media, this);

            if (file == null)
                continue;

            if (file.exists() && Utils.getThumbnailFile(media, this).exists()) {
                Intent intent = new Intent(this, PlaybackOverlayActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

}
