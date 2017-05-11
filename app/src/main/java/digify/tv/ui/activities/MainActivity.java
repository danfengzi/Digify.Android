package digify.tv.ui.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.ButterKnife;
import digify.tv.R;
import digify.tv.api.DigifyApiService;
import digify.tv.core.PreferenceManager;
import digify.tv.core.StartupReceiver;
import digify.tv.db.MediaRepository;
import digify.tv.db.models.DeviceInfo;
import digify.tv.db.models.Media;
import digify.tv.jobs.FetchPlaylistJob;
import digify.tv.ui.events.KioskStatusEvent;
import digify.tv.ui.events.PlayEvent;
import digify.tv.ui.events.QueueStatusEvent;
import digify.tv.ui.events.ScreenOrientationEvent;
import digify.tv.ui.viewmodels.ScreenOrientation;
import digify.tv.util.Utils;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
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


        applicationComponent().inject(this);


        if (!preferenceManager.isLoggedIn())
            return;

        fetchPlaylist();
        checkDeviceInfoBeforePlayback();

    }

    public void fetchPlaylist() {
        Toasty.info(this, "Checking for updates...", Toast.LENGTH_SHORT, true).show();
        jobManager.addJobInBackground(new FetchPlaylistJob());

    }

    public void checkDeviceInfoBeforePlayback() {

        if (!preferenceManager.isInitialSetup()) {
            startPlayback();
            return;
        }
        digifyApiService.getDevice(getUniqueDeviceID(this)).enqueue(new Callback<DeviceInfo>() {
            @Override
            public void onResponse(Call<DeviceInfo> call, final Response<DeviceInfo> response) {
                if (response.isSuccessful()) {
                    preferenceManager.setInitialSetup();

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

                    startPlayback();
                }
            }

            @Override
            public void onFailure(Call<DeviceInfo> call, Throwable t) {
                startPlayback();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        eventBus.register(this);

    }

    private void startPlayback()
    {
        startPlayback(true);
    }
    private void startPlayback(boolean onlyAtStartup) {

        if(onlyAtStartup)
        if (!getIntent().hasExtra(StartupReceiver.FROM_STARTUP_RECEIVER))
            return;


        if (preferenceManager.isQueueModeEnabled()) {
            Intent intent = new Intent(this, QueueModeActivity.class);
            startActivity(intent);

            finish();
        }

        List<Media> list = mediaRepository.getMedia();

        for (Media media : list) {

            File file = Utils.getMediaFile(media, this);
            File thumbnail = Utils.getThumbnailFile(media, this);

            if (file == null || thumbnail == null)
                continue;


            if (file.exists() && thumbnail.exists()) {

                if (!preferenceManager.isPortrait()) {
                    Intent intent = new Intent(this, LandscapeMediaActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, PortraitMediaActivity.class);
                    startActivity(intent);
                }
                break;
            }
        }
    }

    @Subscribe
    public void playbackActionEvent(PlayEvent playEvent)
    {
        startPlayback(false);
    }

    @Subscribe
    public void queueStatusChanged(QueueStatusEvent event)
    {
        if(event.isEnabled())
        {
            Toasty.success(this,"Queue Mode Enabled!").show();
        }
        else
            Toasty.error(this,"Queue Mode Disabled!").show();


    }

    @Subscribe
    public void kioskStatusChanged(KioskStatusEvent event)
    {
        if(event.isEnabled())
        {
            Toasty.success(this,"Kiosk Mode Enabled!").show();
        }
        else
            Toasty.error(this,"Kiosk Mode Disabled!").show();
    }

    @Subscribe
    public void orientationEvent(ScreenOrientationEvent event) {

        if (preferenceManager.isQueueModeEnabled())
            return;

        if (event.getScreenOrientation().equals(ScreenOrientation.Portrait)) {
            if (getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            if (getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onBackPressed() {
        if (!preferenceManager.isKioskModeEnabled())
            super.onBackPressed();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY || event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
            startPlayback();

        return super.onKeyUp(keyCode, event);
    }
}


