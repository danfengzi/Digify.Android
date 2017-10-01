/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package genius.tv.ui.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.registerable.connection.Connectable;
import com.novoda.merlin.registerable.disconnection.Disconnectable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import genius.tv.R;
import genius.tv.core.MediaItemType;
import genius.tv.core.PreferenceManager;
import genius.tv.db.models.MediaType;
import genius.tv.ui.events.MediaDownloadStatus;
import genius.tv.ui.events.MediaDownloadStatusEvent;
import genius.tv.ui.events.PlaylistContentRemovedEvent;
import genius.tv.ui.events.QueueModeEvent;
import genius.tv.ui.events.ScreenOrientationEvent;
import genius.tv.ui.events.VideoMuteEvent;
import genius.tv.ui.viewmodels.ScreenOrientation;
import es.dmoral.toasty.Toasty;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static genius.tv.util.Utils.register;
import static genius.tv.util.Utils.unregister;

/**
 * PlaybackOverlayActivity for video playback that loads PlaybackOverlayFragment
 */
public class QueueModeActivity extends BaseActivity implements
        PlaybackOverlayFragment.OnPlayPauseClickedListener {
    private static final String TAG = "PlaybackOverlayActivity";
    private static final int MY_DATA_CHECK_CODE = 234;

    @BindView(R.id.online_text)
    TextView onlineText;
    @BindView(R.id.online_layout)
    RelativeLayout onlineLayout;
    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.playback_controls_fragment)
    FrameLayout playbackControlsFragment;
    @BindView(R.id.background)
    RelativeLayout background;
    @BindView(R.id.tenant_name)
    TextView tenantName;
    @BindView(R.id.company_details)
    RelativeLayout companyDetails;
    @BindView(R.id.now_serving)
    TextView nowServing;
    @BindView(R.id.queue_layout)
    LinearLayout queueLayout;
    @BindView(R.id.media_layout)
    FrameLayout mediaLayout;

    private LeanbackPlaybackState mPlaybackState = LeanbackPlaybackState.IDLE;
    private MediaSession mSession;
    private String barcode;
    private Merlin merlin;
    private MediaPlayer currentPlayer;

    @Inject
    PreferenceManager preferenceManager;

    @Inject
    Bus eventBus;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applicationComponent().inject(this);

        setContentView(R.layout.queue_mode);
        ButterKnife.bind(this);

        register(eventBus,this);
        loadViews();

        setupCallbacks();


        mSession = new MediaSession(this, "Digify");
        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mSession.setActive(true);

        setupFragment();
        tenantName.setText(preferenceManager.getTenant());

    }

    public void setupFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.playback_controls_fragment, new PlaybackOverlayFragment()); // newInstance() is a static factory method.
        transaction.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.suspend();
        unregister(eventBus,this);
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
                if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
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

        if (isFinishing())
            return;

        if (videoView == null || imageView == null)
            return;

        if (mediaViewModel.getMediaType().equals(MediaType.Video)) {
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            videoView.setVideoPath(mediaViewModel.getMediaUrl());

            if (position == 0 || mPlaybackState == LeanbackPlaybackState.IDLE) {
                setupCallbacks();
                mPlaybackState = LeanbackPlaybackState.IDLE;
            }

            if (playPause) {
                mPlaybackState = LeanbackPlaybackState.PLAYING;
                if (position > 0) {
                    videoView.seekTo(position);
                    videoView.start();
                }
            } else {
                mPlaybackState = LeanbackPlaybackState.PAUSED;
                videoView.pause();
            }
            updatePlaybackState(position);
        } else if (mediaViewModel.getMediaType().equals(MediaType.Image)) {
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);

            Glide.with(this).load(mediaViewModel.getMediaUrl()).into(imageView);
        }
    }

    private void updatePlaybackState(int position) {
        @SuppressWarnings("WrongConstant")
        PlaybackState.Builder stateBuilder = new PlaybackState.Builder()
                .setActions(getAvailableActions());
        int state = PlaybackState.STATE_PLAYING;
        if (mPlaybackState == LeanbackPlaybackState.PAUSED) {
            state = PlaybackState.STATE_PAUSED;
        }
        stateBuilder.setState(state, position, 1.0f);
        mSession.setPlaybackState(stateBuilder.build());
    }

    private long getAvailableActions() {
        long actions = PlaybackState.ACTION_PLAY |
                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackState.ACTION_PLAY_FROM_SEARCH;

        if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
            actions |= PlaybackState.ACTION_PAUSE;
        }

        return actions;
    }


    private void loadViews() {
        videoView.setFocusable(false);
        videoView.setFocusableInTouchMode(false);

        imageView.setFocusable(false);
        imageView.setFocusableInTouchMode(false);
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
                mPlaybackState = LeanbackPlaybackState.IDLE;
                return true;
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
                    videoView.start();
                    currentPlayer = mp;
                }
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlaybackState = LeanbackPlaybackState.IDLE;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mSession.setActive(true);


        setupMerlin();
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

        if (merlin != null)
            merlin.unbind();
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
        if (event.getScreenOrientation().equals(ScreenOrientation.Portrait)) {
            Intent intent = new Intent(this, PortraitMediaActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        barcode = barcode + (char) event.getUnicodeChar();

        return super.onKeyDown(keyCode, event);
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
            }, 10000);

            Log.v("Item downloaded", event.getMediaTag().getTitle());
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
        }, 10000);
    }


    public void setupMerlin() {

        if (merlin == null)
            merlin = new Merlin.Builder().withConnectableCallbacks().withDisconnectableCallbacks().build(this);

        merlin.bind();

        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (onlineLayout != null) {
                            onlineLayout.setBackgroundResource(R.drawable.online);
                        }

                        if (onlineText != null) {
                            onlineText.setText("Online");
                        }

                    }
                });

            }
        });

        merlin.registerDisconnectable(new Disconnectable() {
            @Override
            public void onDisconnect() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (onlineLayout != null) {
                            onlineLayout.setBackgroundResource(R.drawable.offline);
                        }

                        if (onlineText != null) {
                            onlineText.setText("Offline");
                        }
                    }
                });

            }
        });


    }

    @Subscribe
    public void queueModeEvent(QueueModeEvent event) {
        if (!event.isEnabled()) {
            if (preferenceManager.isPortrait()) {
                Intent intent = new Intent(this, PortraitMediaActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, LandscapeMediaActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }


    @Subscribe
    public void modifyPlayerVolume(VideoMuteEvent event) {
        if (currentPlayer != null) {
            if (currentPlayer.isPlaying()) {

                if (event.getMuteStatus().equals(VideoMuteEvent.MuteStatus.Mute)) {
                    currentPlayer.setVolume(0, 0);
                } else if (event.getMuteStatus().equals(VideoMuteEvent.MuteStatus.UnMute)) {
                    currentPlayer.setVolume(100, 100);

                }
            }
        }
    }




}