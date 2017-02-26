package digify.tv.ui.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.ButterKnife;
import digify.tv.R;
import digify.tv.core.MediaItemType;
import digify.tv.db.MediaRepository;
import digify.tv.db.models.DeviceInfo;
import digify.tv.db.models.MediaType;
import digify.tv.ui.events.MediaDownloadStatus;
import digify.tv.ui.events.MediaDownloadStatusEvent;
import digify.tv.ui.events.PlaylistContentRemovedEvent;
import digify.tv.ui.events.ScreenOrientationEvent;
import digify.tv.ui.viewmodels.ScreenOrientation;
import digify.tv.util.Utils;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;

import static android.text.TextUtils.isEmpty;

public class PortraitMediaActivity extends BaseActivity implements PlaybackOverlayFragment.OnPlayPauseClickedListener {
    private static final String TAG = "PlaybackOverlayActivity";
    public static final String IS_VIDEO_ONLY = "video_only";
    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.slider_layout)
    SliderLayout sliderLayout;
    @BindView(R.id.portrait_message)
    TextView portraitMessage;
    @BindView(R.id.portrait_logo)
    ImageView portraitLogo;
    @BindView(R.id.portrait)
    LinearLayout portrait;
    @BindView(R.id.portrait_logo_layout)
    FrameLayout portraitLogoLayout;
    @BindView(R.id.playback_controls_fragment)
    FrameLayout playbackControlsFragment;

    private LandscapeMediaActivity.LeanbackPlaybackState mPlaybackState = LandscapeMediaActivity.LeanbackPlaybackState.IDLE;
    private MediaSession mSession;

    @Inject
    MediaRepository mediaRepository;

    @Inject
    Provider<Realm> database;

    @Inject
    Bus eventBus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portrait);
        ButterKnife.bind(this);
        applicationComponent().inject(this);

        eventBus.register(this);

        if (!preferenceManager.isPortrait()) {
            Intent intent = new Intent(this, LandscapeMediaActivity.class);
            startActivity(intent);
            finish();
        }

        generateSlider();
        generateVideoView();
        fetchPortraitInfo();
    }

    public void fetchPortraitInfo() {
        DeviceInfo deviceInfo = database.get().where(DeviceInfo.class).findFirst();

        if (deviceInfo != null) {
            portraitLogoLayout.setVisibility(View.VISIBLE);

            if (!isEmpty(deviceInfo.getPortraitMessage()))
                portraitMessage.setText(deviceInfo.getPortraitMessage());

            File file = Utils.getPortraitFile(deviceInfo, this);

            if (file != null) {
                Glide.with(this)
                        .load(file)
                        .centerCrop()
                        .into(portraitLogo);
            }
            else
            {
                if(!TextUtils.isEmpty(deviceInfo.getPortraitLogo()))
                Glide.with(this)
                        .load(deviceInfo.getPortraitLogo())
                        .centerCrop()
                        .into(portraitLogo);
            }
        } else {
            portraitLogoLayout.setVisibility(View.GONE);
        }
    }

    public void generateSlider() {
        sliderLayout.setVisibility(View.GONE);
        List<MediaViewModel> pictures = mediaRepository.getMediaViewModelsByType(MediaType.Image);

        if (pictures.isEmpty())
            return;

        sliderLayout.setDuration(preferenceManager.getImageDuration());

        for (MediaViewModel mediaViewModel : pictures) {
            sliderLayout.addSlider(new DefaultSliderView(this).image(new File(mediaViewModel.getMediaUrl())));
        }

        sliderLayout.setVisibility(View.VISIBLE);
    }

    public void generateVideoView() {
        loadViews();

        setupCallbacks();

        mSession = new MediaSession(this, "Digify");
        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mSession.setActive(true);

        setupFragment();

    }

    public void setupFragment() {

        List<MediaViewModel> list = mediaRepository.getMediaViewModelsByType(MediaType.Video);

        if (list != null) {
            if (list.isEmpty() || list.size() == 0) {
                videoView.setVisibility(View.GONE);
                return;
            } else
                videoView.setVisibility(View.VISIBLE);
        }
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        PlaybackOverlayFragment playbackOverlayFragment = new PlaybackOverlayFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_VIDEO_ONLY, true);
        playbackOverlayFragment.setArguments(bundle);
        transaction.replace(R.id.playback_controls_fragment, playbackOverlayFragment); // newInstance() is a static factory method.

        transaction.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.suspend();
        eventBus.unregister(this);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        PlaybackOverlayFragment playbackOverlayFragment = (PlaybackOverlayFragment) getFragmentManager().findFragmentById(R.id.playback_controls_fragment);
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                playbackOverlayFragment.togglePlayback(false);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                playbackOverlayFragment.togglePlayback(false);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (mPlaybackState == LandscapeMediaActivity.LeanbackPlaybackState.PLAYING) {
                    playbackOverlayFragment.togglePlayback(false);
                } else {
                    playbackOverlayFragment.togglePlayback(true);
                }
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    /**
     * Implementation of OnPlayPauseClickedListener
     */
    public void onFragmentPlayPause(MediaViewModel mediaViewModel, int position, Boolean playPause) {
        if(isFinishing())
            return;

        if (videoView == null)
            return;

        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoPath(mediaViewModel.getMediaUrl());

        if (position == 0 || mPlaybackState == LandscapeMediaActivity.LeanbackPlaybackState.IDLE) {
            setupCallbacks();
            mPlaybackState = LandscapeMediaActivity.LeanbackPlaybackState.IDLE;
        }

        if (playPause) {
            mPlaybackState = LandscapeMediaActivity.LeanbackPlaybackState.PLAYING;
            if (position > 0) {
                videoView.seekTo(position);
                videoView.start();
            }
        } else {
            mPlaybackState = LandscapeMediaActivity.LeanbackPlaybackState.PAUSED;
            videoView.pause();
        }
        updatePlaybackState(position);
    }

    private void updatePlaybackState(int position) {
        @SuppressWarnings("WrongConstant")
        PlaybackState.Builder stateBuilder = new PlaybackState.Builder()
                .setActions(getAvailableActions());
        int state = PlaybackState.STATE_PLAYING;
        if (mPlaybackState == LandscapeMediaActivity.LeanbackPlaybackState.PAUSED) {
            state = PlaybackState.STATE_PAUSED;
        }
        stateBuilder.setState(state, position, 1.0f);
        mSession.setPlaybackState(stateBuilder.build());
    }

    private long getAvailableActions() {
        long actions = PlaybackState.ACTION_PLAY |
                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackState.ACTION_PLAY_FROM_SEARCH;

        if (mPlaybackState == LandscapeMediaActivity.LeanbackPlaybackState.PLAYING) {
            actions |= PlaybackState.ACTION_PAUSE;
        }

        return actions;
    }

    private void loadViews() {
        videoView.setFocusable(false);
        videoView.setFocusableInTouchMode(false);

    }

    private void setupCallbacks() {

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                String msg = "";
                if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                    msg = getString(R.string.video_error_media_load_timeout);
                } else if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    msg = getString(R.string.video_error_server_inaccessible);
                } else {
                    msg = getString(R.string.video_error_unknown_error);
                }
                videoView.stopPlayback();
                mPlaybackState = LandscapeMediaActivity.LeanbackPlaybackState.IDLE;
                return true;
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mPlaybackState == LandscapeMediaActivity.LeanbackPlaybackState.PLAYING) {
                    videoView.start();
                }
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlaybackState = LandscapeMediaActivity.LeanbackPlaybackState.IDLE;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mSession.setActive(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            if (!requestVisibleBehind(true)) {
                // Try to play behind launcher, but if it fails, stop playback.
                stopPlayback();
            }
        } else {
            requestVisibleBehind(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSession.release();
    }


    @Override
    public void onVisibleBehindCanceled() {
        super.onVisibleBehindCanceled();
    }

    private void stopPlayback() {
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }

    /*
     * List of various states that we can be in
     */
    public enum LeanbackPlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }

    private class MediaSessionCallback extends MediaSession.Callback {
    }

    @Subscribe
    public void orientationEvent(ScreenOrientationEvent event) {
        if (event.getScreenOrientation().equals(ScreenOrientation.Landscape)) {
            Intent intent = new Intent(this, LandscapeMediaActivity.class);
            startActivity(intent);
        }
    }

    @Subscribe
    public void OnMediaItemDownloadStatusChanged(MediaDownloadStatusEvent event) {
        if (event.getDownloadStatus().equals(MediaDownloadStatus.Completed) && event.getMediaTag().getMediaItemType().equals(MediaItemType.Content)) {
            Toasty.success(this, "New content was added to this box,Restarting playlist.").show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recreate();
                }
            },10000);
        }
    }

    @Subscribe
    public void onMediaItemDeleted(PlaylistContentRemovedEvent event) {
        Toasty.success(this, "Content was modified on this box,Restarting playlist.").show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recreate();
            }
        },10000);
    }
}
