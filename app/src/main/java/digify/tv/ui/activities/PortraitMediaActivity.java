package digify.tv.ui.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import digify.tv.R;
import digify.tv.db.MediaRepository;
import digify.tv.db.models.MediaType;

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
    RelativeLayout portraitLogo;
    @BindView(R.id.portrait)
    LinearLayout portrait;

    private LandscapeMediaActivity.LeanbackPlaybackState mPlaybackState = LandscapeMediaActivity.LeanbackPlaybackState.IDLE;
    private MediaSession mSession;

    @Inject
    MediaRepository mediaRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portrait);
        ButterKnife.bind(this);
        applicationComponent().inject(this);

        generateSlider();
        generateVideoView();
    }

    public void fetchPortraitInfo() {

    }

    public void generateSlider() {
        sliderLayout.setVisibility(View.GONE);
        List<MediaViewModel> pictures = mediaRepository.getMediaViewModelsByType(MediaType.Image);

        if (pictures.isEmpty())
            return;

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

        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoPath(mediaViewModel.getMediaUrl());

        if (position == 0 || mPlaybackState == LandscapeMediaActivity.LeanbackPlaybackState.IDLE) {
            setupCallbacks();
            mPlaybackState = LandscapeMediaActivity.LeanbackPlaybackState.IDLE;
        }

        if (playPause && mPlaybackState != LandscapeMediaActivity.LeanbackPlaybackState.PLAYING) {
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
        updateMetadata(mediaViewModel);
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

    private void updateMetadata(final MediaViewModel mediaViewModel) {
        final MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder();

        String title = mediaViewModel.getTitle().replace("_", " -");

        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, title);
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE,
                mediaViewModel.getDescription());
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI,
                mediaViewModel.getCardImageUrl());

        // And at minimum the title and artist for legacy support
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, title);
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, mediaViewModel.getStudio());

        Glide.with(this)
                .load(Uri.parse(mediaViewModel.getCardImageUrl()))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(500, 500) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ART, bitmap);
                        mSession.setMetadata(metadataBuilder.build());
                    }
                });
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
                return false;
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
}
