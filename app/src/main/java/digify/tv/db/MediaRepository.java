package digify.tv.db;

import android.content.Context;

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

    public List<MediaViewModel> getMedia() {
        List<MediaViewModel> models = new ArrayList<>();

        RealmResults<Media> results = database.get().where(Media.class).findAll();

        for (Media media : results) {
            MediaViewModel mediaViewModel = new MediaViewModel();

            mediaViewModel.setTitle(media.getName());
            mediaViewModel.setCategory("Playlist");
            mediaViewModel.setMediaType(Utils.getStrongMediaType(media.getType()));
            mediaViewModel.setCardImageUrl(Utils.getThumbnailFile(media, getContext()).getAbsolutePath());

            if (Utils.getStrongMediaType(media.getType()).equals(MediaType.Image))
                mediaViewModel.setBackgroundImageUrl(Utils.getMediaFile(media, getContext()).getAbsolutePath());
            else
                mediaViewModel.setBackgroundImageUrl(Utils.getThumbnailFile(media, getContext()).getAbsolutePath());

            models.add(mediaViewModel);
        }

        return models;

    }
}
