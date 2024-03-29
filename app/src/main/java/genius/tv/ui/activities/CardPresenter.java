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

import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.github.lzyzsd.circleprogress.ArcProgress;

import genius.tv.R;
import genius.tv.ui.events.MediaDownloadStatus;

/*
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class CardPresenter extends Presenter {
    private static final String TAG = "CardPresenter";

    private static final int CARD_WIDTH = 313;
    private static final int CARD_HEIGHT = 176;
    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;
    private Drawable mDefaultCardImage;

    private static void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Log.d(TAG, "onCreateViewHolder");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_row, parent, false);

        sDefaultBackgroundColor = parent.getResources().getColor(R.color.default_background);
        sSelectedBackgroundColor = parent.getResources().getColor(R.color.selected_background);
        mDefaultCardImage = parent.getResources().getDrawable(R.drawable.temp_black_bg);

        ImageCardView cardView = new ImageCardView(parent.getContext()) {
            @Override
            public void setSelected(boolean selected) {
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };


        cardView.setId(R.id.playlist_card_view);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(cardView, false);

        ((FrameLayout) view.findViewById(R.id.card_view_layout)).addView(cardView);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        MediaViewModel mediaViewModel = (MediaViewModel) item;

        ImageCardView cardView = (ImageCardView) viewHolder.view.findViewById(R.id.playlist_card_view);
        ArcProgress progress = ((ArcProgress) viewHolder.view.findViewById(R.id.progress_view));

        if (mediaViewModel.getMediaDownloadStatus() != null) {
            switch (mediaViewModel.getMediaDownloadStatus()) {
                case Downloading:
                    progress.setVisibility(View.VISIBLE);
                    progress.setBottomText("");
                    progress.setProgress((int) mediaViewModel.getProgress());
                    break;

                case Completed:
                    progress.setVisibility(View.GONE);
                    break;

                case Paused:
                    progress.setVisibility(View.VISIBLE);
                    progress.setBottomText("PAUSED");
                    break;
            }

        }
        else
        {
            progress.setVisibility(View.GONE);
        }

        if (mediaViewModel.getMediaDownloadStatus() != null) {
            if (mediaViewModel.getMediaDownloadStatus().equals(MediaDownloadStatus.Downloading) || mediaViewModel.getMediaDownloadStatus().equals(MediaDownloadStatus.Paused))
                cardView.getMainImageView().setColorFilter(ContextCompat.getColor(viewHolder.view.getContext(), R.color.black_transparent));
            else
                cardView.getMainImageView().setColorFilter(null);
        }
            else {
            cardView.getMainImageView().setColorFilter(null);
        }

        Log.d(TAG, "onBindViewHolder");
            cardView.setTitleText(mediaViewModel.getTitle());
            cardView.setContentText(mediaViewModel.getStudio());
            cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);


            Glide.with(viewHolder.view.getContext())
                    .load(mediaViewModel.getCardImageUrl())
                    .centerCrop()
                    .error(mDefaultCardImage)
                    .into(cardView.getMainImageView());

    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        Log.d(TAG, "onUnbindViewHolder");
        ImageCardView cardView = (ImageCardView) viewHolder.view.findViewById(R.id.playlist_card_view);
        // Remove references to images so that the garbage collector can free up memory
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);

    }
}
