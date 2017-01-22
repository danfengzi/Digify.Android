package digify.tv.core;

/**
 * Created by Joel on 1/13/2017.
 */

public class MediaTag {

    private Integer id;
    private MediaItemType mediaItemType;
    private String title;

    public MediaTag(Integer id, MediaItemType mediaItemType, String title) {

        this.id = id;
        this.mediaItemType = mediaItemType;
        this.title = title;

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
}
