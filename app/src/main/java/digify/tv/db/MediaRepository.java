package digify.tv.db;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import digify.tv.core.BaseComponent;
import digify.tv.db.models.Media;
import digify.tv.db.models.MediaType;
import digify.tv.ui.activities.MediaViewModel;
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
        List<MediaViewModel> models = new ArrayList<>();

        RealmResults<Media> results = database.get().where(Media.class).findAll();

        for (Media media : results) {

         /*   if (media.getStartTime() != null && media.getEndTime() != null) {
                if (!(new DateTime(media.getStartTime()).isAfterNow() && new DateTime(media.getEndTime()).isBeforeNow()))
                    continue;
            }
            */

            if (Utils.getMediaFile(media, getContext()) == null)
                continue;

            MediaViewModel mediaViewModel = new MediaViewModel();

            mediaViewModel.setTitle(media.getName());
            mediaViewModel.setCategory("Playlist");

            File mediaFile = Utils.getMediaFile(media, getContext());

            if (mediaFile == null)
                continue;

            mediaViewModel.setMediaUrl(mediaFile.getAbsolutePath());
            mediaViewModel.setMediaType(Utils.getStrongMediaType(media.getType()));

            File thumbnail = Utils.getThumbnailFile(media, getContext());

            if (thumbnail == null)
                continue;

            mediaViewModel.setCardImageUrl(thumbnail.getAbsolutePath());

            if (Utils.getStrongMediaType(media.getType()).equals(MediaType.Image))
                mediaViewModel.setBackgroundImageUrl(Utils.getMediaFile(media, getContext()).getAbsolutePath());
            else
                mediaViewModel.setBackgroundImageUrl(Utils.getThumbnailFile(media, getContext()).getAbsolutePath());

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
