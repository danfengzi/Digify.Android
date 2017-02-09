package digify.tv.db;

import android.content.Context;

import org.joda.time.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import digify.tv.core.BaseComponent;
import digify.tv.db.models.Media;
import digify.tv.db.models.MediaType;
import digify.tv.db.models.PlaylistType;
import digify.tv.ui.activities.MediaViewModel;
import digify.tv.ui.events.MediaDownloadStatus;
import digify.tv.util.Utils;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Joel on 1/13/2017.
 */

public class MediaRepository extends BaseComponent {
    @Inject
    Provider<Realm> database;


    public MediaRepository(Context context) {
        super(context);

        applicationComponent().inject(this);
    }

    public List<Media> getMedia() {
        return database.get().where(Media.class).findAll();

    }

    public List<MediaViewModel> getMediaViewModels() {
        return getMediaViewModels(PlaylistType.Playback);
    }

    public List<MediaViewModel> getMediaViewModels(PlaylistType playlistType) {
        List<MediaViewModel> models = new ArrayList<>();

        RealmResults<Media> results = database.get().where(Media.class).findAll();

        for (Media media : results) {

            /*check to see if time reset is throwing off code. also check to see if boxes will auto update with time. also check to ensure that if box doesnt
doesnt have the right time setting, discuss the fallback that will be appropriate in terms of displaying content.


also check to see the amount of media items being retrieved from the database on start. to see if file issues are taking place.

*/
            if (media.getStartTime() != null && media.getEndTime() != null) {
                if (!(new DateTime(media.getStartTime()).isAfterNow() && new DateTime(media.getEndTime()).isBeforeNow()))
                    continue;
            }

            MediaViewModel mediaViewModel = new MediaViewModel();

            mediaViewModel.setId(media.getId());
            mediaViewModel.setTitle(media.getName());
            mediaViewModel.setCategory("Playlist");

            File mediaFile = Utils.getMediaFile(media, getContext());

            if (playlistType.equals(PlaylistType.Playback)) {
                if (mediaFile == null)
                    continue;

                mediaViewModel.setMediaUrl(mediaFile.getAbsolutePath());
            }
            mediaViewModel.setMediaDownloadStatus(MediaDownloadStatus.Downloading);
            mediaViewModel.setMediaType(Utils.getStrongMediaType(media.getType()));

            File thumbnail = Utils.getThumbnailFile(media, getContext());

            if (thumbnail == null)
                continue;

            mediaViewModel.setCardImageUrl(thumbnail.getAbsolutePath());


            if (Utils.getMediaFile(media, getContext()) != null) {
                if (Utils.getStrongMediaType(media.getType()).equals(MediaType.Image))
                    mediaViewModel.setBackgroundImageUrl(Utils.getMediaFile(media, getContext()).getAbsolutePath());
                else
                    mediaViewModel.setBackgroundImageUrl(Utils.getThumbnailFile(media, getContext()).getAbsolutePath());
            }

            models.add(mediaViewModel);
        }

        return models;
    }

    public void syncMediaDeletion(List<Media> serverPlaylist) {
        List<Media> localPlaylist = database.get().where(Media.class).findAll();

        for (final Media localMedia : localPlaylist) {
            boolean found = false;

            for (Media serverMedia : serverPlaylist) {

                if (localMedia.getId().equals(serverMedia.getId())) {

                    found = true;

                    break;
                }
            }

            if (!found) {

                File mediaFile = Utils.getMediaFile(localMedia, getContext());

                if (mediaFile != null) {
                    if (mediaFile.exists())
                        mediaFile.delete();
                }


                File thumbnailFile = Utils.getThumbnailFile(localMedia, getContext());

                if (thumbnailFile != null) {
                    if (thumbnailFile.exists())
                        thumbnailFile.delete();
                }

                database.get().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        localMedia.deleteFromRealm();
                    }
                });
            }
        }
    }

    public Media getMediaById(int id) {
        return database.get().where(Media.class).equalTo("id", id).findFirst();
    }

    public void saveMedia(final Media media) {
        database.get().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(media);
            }
        });
    }
}
