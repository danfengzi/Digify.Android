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

import butterknife.BindView;
import butterknife.ButterKnife;
import digify.tv.R;
import digify.tv.core.PreferenceManager;
import digify.tv.db.MediaRepository;
import digify.tv.db.models.Media;
import digify.tv.jobs.FetchPlaylistJob;
import digify.tv.util.Utils;
import es.dmoral.toasty.Toasty;
import jonathanfinerty.once.Once;

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
        startPlayback();

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
