package genius.tv.core;

/**
 * Created by Joel on 1/8/2017.
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import genius.tv.db.models.Media;
import io.realm.RealmList;

/**
 * Created by Joel on 6/11/2016.
 */
public class MediaGsonConverter implements JsonSerializer<RealmList<Media>>,JsonDeserializer<RealmList<Media>> {

    @Override
    public JsonElement serialize(RealmList<Media> src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        JsonArray ja = new JsonArray();
        for (Media tag : src) {
            ja.add(context.serialize(tag));
        }
        return ja;
    }

    @Override
    public RealmList<Media> deserialize(JsonElement json, Type typeOfT,
                                                   JsonDeserializationContext context)
            throws JsonParseException {
        RealmList<Media> tags = new RealmList<>();
        JsonArray ja = json.getAsJsonArray();
        for (JsonElement je : ja) {
            tags.add((Media) context.deserialize(je, Media.class));
        }
        return tags;
    }

}