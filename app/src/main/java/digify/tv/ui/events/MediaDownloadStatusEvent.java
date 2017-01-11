package digify.tv.ui.events;

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
