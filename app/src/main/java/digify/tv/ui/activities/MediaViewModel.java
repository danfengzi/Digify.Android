/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package digify.tv.ui.activities;

import android.util.Log;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.Date;

import digify.tv.db.models.MediaType;
import digify.tv.ui.events.MediaDownloadStatus;

/*
 * Movie class represents video entity with title, description, image thumbs and video url.
 *
 */
public class MediaViewModel implements Serializable,Comparable<MediaViewModel> {
    static final long serialVersionUID = 727566175075960653L;
    private static long count = 0;
    private int id;
    private String title;
    private String description;
    private String bgImageUrl;
    private String cardImageUrl;
    private String mediaUrl;
    private String studio;
    private String category;
    private MediaType mediaType;
    private MediaDownloadStatus mediaDownloadStatus;
    private double progress;
    private Boolean notScheduled;
    private Integer position;
    private Date startTime;
    private Date endTime;

    public MediaViewModel() {
    }

    public MediaDownloadStatus getMediaDownloadStatus() {
        return mediaDownloadStatus;
    }

    public void setMediaDownloadStatus(MediaDownloadStatus mediaDownloadStatus) {
        this.mediaDownloadStatus = mediaDownloadStatus;
    }

    public static long getCount() {
        return count;
    }

    public static void incCount() {
        count++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getBackgroundImageUrl() {
        return bgImageUrl;
    }

    public void setBackgroundImageUrl(String bgImageUrl) {
        this.bgImageUrl = bgImageUrl;
    }

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public URI getBackgroundImageURI() {

        if(bgImageUrl==null)
            return null;

        try {
                Log.d("BACK MOVIE: ", bgImageUrl);
            return new URI(getBackgroundImageUrl());
        } catch (URISyntaxException e) {
            Log.d("URI exception: ", bgImageUrl);
            return null;
        }
    }

    public URI getCardImageURI() {
        try {
            return new URI(getCardImageUrl());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", backgroundImageUrl='" + bgImageUrl + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                '}';
    }

    public Boolean getNotScheduled() {
        return notScheduled;
    }

    public void setNotScheduled(Boolean notScheduled) {
        this.notScheduled = notScheduled;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public int compareTo(MediaViewModel media) {
        return (this.getPosition()).compareTo(media.getPosition());
    }

    public static final Comparator<MediaViewModel> ASCENDING_COMPARATOR = new Comparator<MediaViewModel>() {
        public int compare(MediaViewModel lhs, MediaViewModel rhs) {
            return lhs.getPosition() - rhs.getPosition();
        }
    };
}
