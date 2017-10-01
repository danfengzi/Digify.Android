package genius.tv.core;

import genius.tv.ui.events.MediaDownloadStatus;

/**
 * Created by Joel on 1/13/2017.
 */

public class MediaTag {

    private Integer id;
    private MediaItemType mediaItemType;
    private String title;
    private MediaDownloadStatus mediaDownloadStatus;

    public MediaTag(Integer id, MediaItemType mediaItemType, String title) {
        this.id = id;
        this.mediaItemType = mediaItemType;
        this.title = title;
    }

    public MediaTag(Integer id, MediaItemType mediaItemType, String title, MediaDownloadStatus mediaDownloadStatus) {
        this.id = id;
        this.mediaItemType = mediaItemType;
        this.title = title;
        this.mediaDownloadStatus = mediaDownloadStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MediaItemType getMediaItemType() {
        return mediaItemType;
    }

    public void setMediaItemType(MediaItemType mediaItemType) {
        this.mediaItemType = mediaItemType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MediaDownloadStatus getMediaDownloadStatus() {
        return mediaDownloadStatus;
    }

    public void setMediaDownloadStatus(MediaDownloadStatus mediaDownloadStatus) {
        this.mediaDownloadStatus = mediaDownloadStatus;
    }
}
