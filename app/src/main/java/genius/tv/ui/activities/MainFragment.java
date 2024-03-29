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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.net.URI;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import genius.tv.GeniusApp;
import genius.tv.R;
import genius.tv.core.DeviceInfoService;
import genius.tv.core.GetUserDeviceService;
import genius.tv.core.MediaItemType;
import genius.tv.core.PreferenceManager;
import genius.tv.db.MediaRepository;
import genius.tv.db.models.PlaylistType;
import genius.tv.jobs.FetchPlaylistJob;
import genius.tv.jobs.FetchSettingsJob;
import genius.tv.jobs.FetchUserDeviceJob;
import genius.tv.jobs.GetDeviceInfoJob;
import genius.tv.ui.events.DownloadQueueStatusEvent;
import genius.tv.ui.events.MediaDownloadStatus;
import genius.tv.ui.events.MediaDownloadStatusEvent;
import genius.tv.ui.events.PlayEvent;
import genius.tv.ui.events.PlaylistContentRemovedEvent;
import genius.tv.ui.viewmodels.PreferencesItemModel;
import genius.tv.ui.viewmodels.PreferencesItemType;
import es.dmoral.toasty.Toasty;

import static genius.tv.util.Utils.getVersionName;
import static genius.tv.util.Utils.register;
import static genius.tv.util.Utils.unregister;

public class MainFragment extends BrowseFragment {
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;
    private static final int NUM_ROWS = 6;
    private static final int NUM_COLS = 15;

    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mRowsAdapter;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private URI mBackgroundURI;
    private BackgroundManager mBackgroundManager;

    @Inject
    Bus eventBus;

    @Inject
    MediaRepository mediaRepository;

    @Inject
    JobManager jobManager;

    @Inject
    PreferenceManager preferenceManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);
        GeniusApp.get(getActivity()).getComponent().inject(this);

        prepareBackgroundManager();
        setupUIElements();
        loadRows();
        setupEventListeners();
        register(eventBus,this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }

        unregister(eventBus,this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    private void loadRows() {
        List<MediaViewModel> list = mediaRepository.getMediaViewModels(PlaylistType.MainFragment);

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter cardPresenter = new CardPresenter();

        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);

        for (MediaViewModel model : list) {
            Log.v("position ", model.getMediaUrl() + " " + " position " + model.getPosition());
            listRowAdapter.add(model);

        }
        HeaderItem header = new HeaderItem(1, "Playlist");
        mRowsAdapter.add(new ListRow(header, listRowAdapter));

        HeaderItem gridHeader = new HeaderItem(2, "Menu");

        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);

        gridRowAdapter.add(new PreferencesItemModel(PreferencesItemType.Play, "Play All"));
        gridRowAdapter.add(new PreferencesItemModel(PreferencesItemType.Refresh, "Refresh Playlist"));
        gridRowAdapter.add(new PreferencesItemModel(PreferencesItemType.Logout, "Log Out"));
        mRowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));

        setAdapter(mRowsAdapter);

    }

    public ArrayObjectAdapter getPlaylistAdapter() {

        Object listRow = getAdapter().get(0);

        if (listRow != null) {
            if (listRow instanceof ListRow) {
                Object adapter = ((ListRow) listRow).getAdapter();

                if (adapter instanceof ArrayObjectAdapter) {
                    return (ArrayObjectAdapter) adapter;
                }
            }
        }

        return null;
    }

    public void removeItem(int mediaId) {
        ArrayObjectAdapter adapter = getPlaylistAdapter();
        for (int x = 0; x < adapter.size(); x++) {
            if (((MediaViewModel) adapter.get(x)).getId() == mediaId) {

                getPlaylistAdapter().remove(adapter.get(x));

                return;
            }
        }
    }

    public void updateMediaViewModel(int mediaId, double progress, MediaDownloadStatus mediaDownloadStatus) {
        ArrayObjectAdapter adapter = getPlaylistAdapter();
        for (int x = 0; x < adapter.size(); x++) {
            if (((MediaViewModel) adapter.get(x)).getId() == mediaId) {
                MediaViewModel model = (MediaViewModel) adapter.get(x);
                model.setProgress(progress);
                mediaRepository.thumbnailMapper(model);
                model.setMediaDownloadStatus(mediaDownloadStatus);
                getPlaylistAdapter().replace(adapter.indexOf(adapter.get(x)), model);

                return;
            }
        }

        MediaViewModel mediaViewModel = mediaRepository.getMediaViewModelForPlaylist(mediaId);

        if (mediaViewModel == null)
            return;

        mediaViewModel.setProgress(progress);
        mediaViewModel.setMediaDownloadStatus(mediaDownloadStatus);

        adapter.add(mediaViewModel);

    }

    private void prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {

        setTitle("Hi-Pro TV - v"+getVersionName(getActivity()));
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Search not implemented as yet.", Toast.LENGTH_LONG)
                        .show();
            }
        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    protected void updateBackground(String uri) {

        try {
            int width = mMetrics.widthPixels;
            int height = mMetrics.heightPixels;
            Glide.with(getActivity())
                    .load(uri)
                    .centerCrop()
                    .error(mDefaultBackground)
                    .into(new SimpleTarget<GlideDrawable>(width, height) {
                        @Override
                        public void onResourceReady(GlideDrawable resource,
                                                    GlideAnimation<? super GlideDrawable>
                                                            glideAnimation) {
                            mBackgroundManager.setDrawable(resource);
                        }
                    });
            mBackgroundTimer.cancel();
        } catch (Exception e) {
        }
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof MediaViewModel) {
                MediaViewModel mediaViewModel = (MediaViewModel) item;

                if (mediaViewModel.getMediaDownloadStatus() != null)
                    if (mediaViewModel.getMediaDownloadStatus().equals(MediaDownloadStatus.Downloading) || mediaViewModel.getMediaDownloadStatus().equals(MediaDownloadStatus.Paused)) {
                        Toasty.info(getActivity(), "Item available after download.").show();
                        return;
                    }

                if (mediaViewModel.getStartTime() != null && mediaViewModel.getEndTime() != null) {
                    if (!(new DateTime(mediaViewModel.getStartTime()).isAfterNow() && new DateTime(mediaViewModel.getEndTime()).isBeforeNow())) {
                        Toasty.info(getActivity(), "Item can be viewed during scheduled time.").show();
                        return;
                    }
                }

                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE, mediaViewModel);

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view.findViewById(R.id.playlist_card_view)).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();

                getActivity().startActivity(intent, bundle);
            } else if (item instanceof PreferencesItemModel) {
                if (((PreferencesItemModel) item).getItemType().equals(PreferencesItemType.Logout)) {
                    preferenceManager.logout();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);

                } else if (((PreferencesItemModel) item).getItemType().equals(PreferencesItemType.Refresh)) {

                    Toasty.normal(getActivity(), "Checking for Updates", Toast.LENGTH_SHORT).show();

                    serverSync();

                    loadRows();
                } else if (((PreferencesItemModel) item).getItemType().equals(PreferencesItemType.Play)) {
                    eventBus.post(new PlayEvent());
                }
            } else if (item instanceof String) {
                if (((String) item).indexOf(getString(R.string.error_fragment)) >= 0) {
                    Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    private void serverSync() {
        jobManager.addJobInBackground(new FetchPlaylistJob());
        jobManager.addJobInBackground(new GetDeviceInfoJob());
        jobManager.addJobInBackground(new FetchSettingsJob());
        jobManager.addJobInBackground(new FetchUserDeviceJob());
        getActivity().startService(new Intent(getActivity(), DeviceInfoService.class));
        getActivity().startService(new Intent(getActivity(), GetUserDeviceService.class));
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof MediaViewModel) {
                mBackgroundURI = ((MediaViewModel) item).getBackgroundImageURI();
                startBackgroundTimer();
            }

        }
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mBackgroundURI != null) {
                        updateBackground(mBackgroundURI.toString());
                    }
                }
            });

        }
    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final Object item) {
            ((TextView) viewHolder.view).setText(((PreferencesItemModel) item).getButtonText());

        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {

        }
    }

    @Subscribe
    public void OnMediaItemDownloadStatusChanged(MediaDownloadStatusEvent event) {

        switch (event.getDownloadStatus()) {
            case Completed:
                if (event.getMediaTag().getMediaItemType().equals(MediaItemType.Content)) {
                    Toasty.success(getActivity(), event.getDownloadStatus().name() + " " + event.getMediaTag().getTitle(), Toast.LENGTH_LONG).show();
                    updateMediaViewModel(event.getMediaTag().getId(), event.getProgressPercent(), event.getDownloadStatus());
                }

                break;

            case Downloading:
                if (event.getMediaTag().getMediaItemType().equals(MediaItemType.Content)) {
                    updateMediaViewModel(event.getMediaTag().getId(), event.getProgressPercent(), event.getDownloadStatus());
                }
        }
    }

    @Subscribe
    public void updatePlaylist(DownloadQueueStatusEvent event)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadRows();
            }
        });
    }

    @Subscribe
    public void onMediaItemDeleted(PlaylistContentRemovedEvent event) {
        Toasty.success(getActivity(), "Content was modified on this box,Updating playlist.").show();
        removeItem(event.getMediaId());
    }


}
