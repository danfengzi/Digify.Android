package genius.tv.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.squareup.otto.Bus;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import genius.tv.DigifyApp;
import genius.tv.api.DigifyApiService;
import genius.tv.core.MediaTag;
import genius.tv.db.MediaRepository;
import genius.tv.db.models.Media;
import genius.tv.ui.events.DownloadQueueStatusEvent;
import genius.tv.ui.events.MediaDownloadStatus;
import genius.tv.ui.events.MediaDownloadStatusEvent;
import genius.tv.util.Utils;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static genius.tv.util.Utils.createMediaDownloadTask;
import static genius.tv.util.Utils.createThumbnailDownloadTask;

/**
 * Created by Joel on 1/6/2017.
 */

public class FetchPlaylistJob extends Job {
    public static final int PRIORITY = 1;

    @Inject
    DigifyApiService digifyApiService;
    @Inject
    Provider<Realm> database;
    @Inject
    Bus eventBus;

    @Inject
    MediaRepository mediaRepository;

    private static final boolean SERIAL = false;
    private static final boolean PARALLEL = true;


    public FetchPlaylistJob() {
        super(new Params(PRIORITY).requireNetwork().persist());
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {

        DigifyApp.get(getApplicationContext()).getComponent().inject(this);

        final Call<List<Media>> request = digifyApiService.getDevicePlaylist(Utils.getUniqueDeviceID(getApplicationContext()));

        request.enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(Call<List<Media>> call, Response<List<Media>> response) {

                if (response.body() != null) {

                    mediaRepository.syncMediaDeletion(response.body());

                    FileDownloadListener downloadListener = new FileDownloadListener() {
                        @Override
                        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            if (soFarBytes > 0 && totalBytes > 0)
                                eventBus.post(new MediaDownloadStatusEvent((soFarBytes / totalBytes) * 100, (MediaTag) task.getTag(), MediaDownloadStatus.Pending));

                        }

                        @Override
                        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            if (soFarBytes > 0 && totalBytes > 0)
                                eventBus.post(new MediaDownloadStatusEvent(((double) soFarBytes / totalBytes) * 100, (MediaTag) task.getTag(), MediaDownloadStatus.Downloading));

                        }

                        @Override
                        protected void completed(BaseDownloadTask task) {
                            eventBus.post(new MediaDownloadStatusEvent(100, (MediaTag) task.getTag(), MediaDownloadStatus.Completed));

                        }

                        @Override
                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            if (soFarBytes > 0 && totalBytes > 0)
                                eventBus.post(new MediaDownloadStatusEvent(((double) soFarBytes / totalBytes) * 100, (MediaTag) task.getTag(), MediaDownloadStatus.Paused));

                        }

                        @Override
                        protected void error(BaseDownloadTask task, Throwable e) {

                            if (e != null)
                                if (!TextUtils.isEmpty(e.getMessage()))
                                    Log.e(FetchPlaylistJob.class.getName(), e.getMessage());


                            eventBus.post(new MediaDownloadStatusEvent(0.0, (MediaTag) task.getTag(), MediaDownloadStatus.Error));

                        }

                        @Override
                        protected void warn(BaseDownloadTask task) {

                        }
                    };

                    final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(downloadListener);
                    final List<BaseDownloadTask> tasks = new ArrayList<>();


                    for (final Media media : response.body()) {

                        Media item = mediaRepository.getMediaById(media.getId());

                        if (item != null) {
                            if (new DateTime(media.getUpdatedAt()).isAfter(new DateTime(item.getUpdatedAt())) || Utils.getMediaFile(media, getApplicationContext()) == null) {
                                tasks.add(createThumbnailDownloadTask(getApplicationContext(), media));
                                tasks.add(createMediaDownloadTask(getApplicationContext(), media));

                            }
                        } else {
                            tasks.add(createThumbnailDownloadTask(getApplicationContext(), media));
                            tasks.add(createMediaDownloadTask(getApplicationContext(), media));
                        }
                        mediaRepository.saveMedia(media);

                    }

                    queueSet.setAutoRetryTimes(5);

                    if (SERIAL) {
                        // Start downloading in serial order.
                        queueSet.downloadSequentially(tasks);

                    }

                    if (PARALLEL) {
                        queueSet.downloadTogether(tasks);
                    }

                    queueSet.start();

                    eventBus.post(new DownloadQueueStatusEvent(MediaDownloadStatus.DownloadQueueStarted));

                }

            }

            @Override
            public void onFailure(Call<List<Media>> call, Throwable t) {
            }
        });


    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }


}

