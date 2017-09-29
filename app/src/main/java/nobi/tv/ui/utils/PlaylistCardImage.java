package nobi.tv.ui.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;

import nobi.tv.ui.events.MediaDownloadStatus;

import static nobi.tv.util.Utils.tint;

/**
 * Created by Joel on 2/5/2017.
 */

public class PlaylistCardImage extends ImageViewTarget<GlideDrawable> {
    private final ColorStateList placeholderColor;
    private final ColorStateList resultColor;
    private final ColorStateList errorColor;
    private MediaDownloadStatus mediaDownloadStatus;

    public PlaylistCardImage(ImageView view, MediaDownloadStatus mediaDownloadStatus) {
        super(view);
        this.mediaDownloadStatus = mediaDownloadStatus;
        this.placeholderColor = ColorStateList.valueOf(ContextCompat.getColor(view.getContext(), android.R.color.black));
        this.resultColor = ColorStateList.valueOf(ContextCompat.getColor(view.getContext(), android.R.color.black));
        this.errorColor = ColorStateList.valueOf(ContextCompat.getColor(view.getContext(), android.R.color.holo_red_dark));
    }

    @Override
    public void setDrawable(Drawable drawable) {
        // don't tint, this is called with a cross-fade drawable,
        // and we need the inner drawables tinted, but not this
        super.setDrawable(drawable);
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        if (mediaDownloadStatus.equals(MediaDownloadStatus.Completed))
            super.onLoadStarted(placeholder);
        else
            super.onLoadStarted(tint(placeholder, placeholderColor));
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        if (mediaDownloadStatus.equals(MediaDownloadStatus.Completed))
            super.onLoadFailed(e, errorDrawable);
        else
            super.onLoadFailed(e, tint(errorDrawable, errorColor));
    }

    @Override
    public void onLoadCleared(Drawable placeholder) {
        if (mediaDownloadStatus.equals(MediaDownloadStatus.Completed))
            super.onLoadCleared(placeholder);
        else
            super.onLoadCleared(tint(placeholder, placeholderColor));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onResourceReady(GlideDrawable resource, GlideAnimation glideAnimation) {

        if (mediaDownloadStatus.equals(MediaDownloadStatus.Completed))
            super.onResourceReady(resource, glideAnimation);
        else {
            Drawable tinted = tint(resource, resultColor);
            // animate works with drawable likely because it's accepting Drawables, but declaring GlideDrawable as generics
            if (glideAnimation == null || !glideAnimation.animate(tinted, this)) {
                view.setImageDrawable(tinted);
            }
        }
    }

    @Override
    protected void setResource(GlideDrawable resource) {
        throw new UnsupportedOperationException("onResourceReady is overridden, this shouldn't be called");
    }
}