package digify.tv.ui.events;

/**
 * Created by Joel on 2/2/2017.
 */

public class DownloadQueueStatusEvent {

    private MediaDownloadStatus mediaDownloadStatus;

    public DownloadQueueStatusEvent(MediaDownloadStatus mediaDownloadStatus) {
        this.mediaDownloadStatus = mediaDownloadStatus;
    }

    public MediaDownloadStatus getMediaDownloadStatus() {
        return mediaDownloadStatus;
    }

    public void setMediaDownloadStatus(MediaDownloadStatus mediaDownloadStatus) {
        this.mediaDownloadStatus = mediaDownloadStatus;
    }
}
