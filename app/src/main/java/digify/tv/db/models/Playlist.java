package digify.tv.db.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Joel on 12/12/2016.
 */

public class Playlist extends RealmObject {

    @PrimaryKey
    private String playlistId;
    private String name;
    private RealmList<Media> media;

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<Media> getMedia() {
        return media;
    }

    public void setMedia(RealmList<Media> media) {
        this.media = media;
    }
}
