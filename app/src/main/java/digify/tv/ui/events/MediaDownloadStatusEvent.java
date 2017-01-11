package digify.tv.ui.events;

import android.util.Log;

/**
 * Created by Joel on 1/10/2017.
 */

public class MediaDownloadStatusEvent {
    private double progressPercent;
    private Integer mediaId;
    private MediaDownloadStatus downloadStatus;

    public MediaDownloadStatusEvent(double progressPercent, Integer mediaId, MediaDownloadStatus downloadStatus) {
        this.progressPercent = progressPercent;
        this.mediaId = mediaId;
        this.downloadStatus = downloadStatus;

        if(downloadStatus.equals(MediaDownloadStatus.Pending))
        Log.v("MediaDownloadStat","media id :"+mediaId+" "+downloadStatus.toString());
    }

    public Integer getMediaId() {
        return mediaId;
    }

    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
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
