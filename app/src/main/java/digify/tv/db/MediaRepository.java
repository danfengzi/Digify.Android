package digify.tv.db;

import android.content.Context;

import com.squareup.otto.Bus;

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
import digify.tv.ui.events.PlaylistContentRemovedEvent;
import digify.tv.util.Utils;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Joel on 1/13/2017.
 */

public class MediaRepository extends BaseComponent {
    @Inject
    Provider<Realm> database;

    @Inject
    Bus eventBus;


    public MediaRepository(Context context) {
        super(context);

        applicationComponent().inject(this);
        eventBus.register(this);
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


            MediaViewModel mediaViewModel = new MediaViewModel();
            if (media.getStartTime() != null && media.getEndTime() != null) {
                if (!(new DateTime(media.getStartTime()).isAfterNow() && new DateTime(media.getEndTime()).isBeforeNow())) {
                    mediaViewModel.setNotScheduled(true);
                    if (playlistType.equals(PlaylistType.Playback))
                        continue;
                }
            }
            mediaViewModel.setId(media.getId());
            mediaViewModel.setTitle(media.getName());
            mediaViewModel.setCategory("Playlist");

            File mediaFile = Utils.getMediaFile(media, getContext());

            if (playlistType.equals(PlaylistType.Playback)) {
                if (mediaFile == null)
                    continue;
            }

            if (mediaFile != null)
                mediaViewModel.setMediaUrl(mediaFile.getAbsolutePath());

            mediaViewModel.setMediaType(Utils.getStrongMediaType(media.getType()));

            File thumbnail = Utils.getThumbnailFile(media, getContext());

            if (thumbnail != null)
                mediaViewModel.setCardImageUrl(thumbnail.getAbsolutePath());


            if (Utils.getMediaFile(media, getContext()) != null) {
                if (Utils.getStrongMediaType(media.getType()).equals(MediaType.Image))
                    mediaViewModel.setBackgroundImageUrl(Utils.getMediaFile(media, getContext()).getAbsolutePath());
                else if (thumbnail != null)
                    mediaViewModel.setBackgroundImageUrl(Utils.getThumbnailFile(media, getContext()).getAbsolutePath());
            }

            models.add(mediaViewModel);
        }

        return models;
    }

    public MediaViewModel getMediaViewModel(int mediaId) {
        List<MediaViewModel> models = getMediaViewModels();

        for (MediaViewModel mediaViewModel : models) {
            if (mediaViewModel.getId() == mediaId)
                return mediaViewModel;
        }

        return null;
    }

    public List<MediaViewModel> getMediaViewModelsByType(MediaType mediaType) {
        List<MediaViewModel> list = getMediaViewModels();

        List<MediaViewModel> filtered = new ArrayList<>();

        for (MediaViewModel mediaViewModel : list) {
            if (mediaViewModel.getMediaType().equals(mediaType))
                filtered.add(mediaViewModel);
        }

        return filtered;
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

                eventBus.post(new PlaylistContentRemovedEvent(localMedia.getId()));

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


    public void thumbnailMapper(MediaViewModel mediaViewModel) {
        Media media = getMediaById(mediaViewModel.getId());

        if (media == null)
            return;

        File thumbnail = Utils.getThumbnailFile(media, getContext());

        if (thumbnail == null)
            return;

        if (!thumbnail.exists())
            return;

        if (thumbnail != null)
            mediaViewModel.setCardImageUrl(thumbnail.getAbsolutePath());

        if (mediaViewModel.getMediaType().equals(MediaType.Image)) {
            File image = Utils.getMediaFile(media, getContext());

            if (image != null)
                mediaViewModel.setBackgroundImageUrl(image.getAbsolutePath());
        } else if (thumbnail != null)
            mediaViewModel.setBackgroundImageUrl(thumbnail.getAbsolutePath());

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
