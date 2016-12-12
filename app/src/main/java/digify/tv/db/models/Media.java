package digify.tv.db.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Joel on 12/12/2016.
 */

public class Media extends RealmObject {

    @PrimaryKey
    String mediaId;
    String mediaType;
}
