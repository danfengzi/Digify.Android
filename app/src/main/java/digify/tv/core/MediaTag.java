package digify.tv.core;

/**
 * Created by Joel on 1/13/2017.
 */

public class MediaTag {

    private Integer id;
    private MediaItemType mediaItemType;

    public MediaTag(Integer id, MediaItemType mediaItemType) {
        this.id = id;
        this.mediaItemType = mediaItemType;
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
}
