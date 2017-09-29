package nobi.tv.ui.events;

/**
 * Created by Joel on 2/16/2017.
 */

public class PlaylistContentRemovedEvent {
    private int mediaId;

    public PlaylistContentRemovedEvent(int mediaId) {
        this.mediaId = mediaId;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }
}
