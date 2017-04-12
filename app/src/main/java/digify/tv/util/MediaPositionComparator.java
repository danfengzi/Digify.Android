package digify.tv.util;

import java.util.Comparator;

import digify.tv.ui.activities.MediaViewModel;

/**
 * Created by Joel on 4/12/2017.
 */

public class MediaPositionComparator implements Comparator<MediaViewModel> {
    @Override
    public int compare(MediaViewModel media, MediaViewModel t1) {
        return Integer.compare(media.getPosition(),t1.getPosition());
    }
}
