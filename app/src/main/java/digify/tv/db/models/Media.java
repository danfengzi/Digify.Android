package digify.tv.db.models;

import java.util.Comparator;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Joel on 12/12/2016.
 */

public class Media extends RealmObject implements Comparable<Media> {

    @PrimaryKey
    private Integer id;
    private Integer position;
    private String name;
    private Date createdAt;
    private Date updatedAt;
    private String contentType;
    private String type;
    private String location;
    private String thumbLocation;
    private String length;
    private int durationSeconds;
    private Date startTime;
    private Date endTime;
    private String extension;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getThumbLocation() {
        return thumbLocation;
    }

    public void setThumbLocation(String thumbLocation) {
        this.thumbLocation = thumbLocation;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public int compareTo(Media media) {
        return (this.getPosition()).compareTo(media.getPosition());
    }

    public static final Comparator<Media> ASCENDING_COMPARATOR = new Comparator<Media>() {
        public int compare(Media lhs, Media rhs) {
            return lhs.getPosition() - rhs.getPosition();
        }
    };
}