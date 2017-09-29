package nobi.tv.ui.events;

import nobi.tv.core.MediaTag;

/**
 * Created by Joel on 1/10/2017.
 */

public class MediaDownloadStatusEvent {
    private double progressPercent;
    private MediaTag mediaTag;
    private MediaDownloadStatus downloadStatus;

    public MediaDownloadStatusEvent(double progressPercent, MediaTag mediaTag, MediaDownloadStatus downloadStatus) {
        this.progressPercent = progressPercent;
        this.mediaTag = mediaTag;
        this.downloadStatus = downloadStatus;

        if (downloadStatus != null)
            mediaTag.setMediaDownloadStatus(downloadStatus);

    }

    public MediaTag getMediaTag() {
        return mediaTag;
    }

    public void setMediaTag(MediaTag mediaTag) {
        this.mediaTag = mediaTag;
    }

    public double getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(double progressPercent) {
        this.progressPercent = progressPercent;
    }

    public MediaDownloadStatus getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(MediaDownloadStatus downloadStatus) {
        this.downloadStatus = downloadStatus;
    }
}
