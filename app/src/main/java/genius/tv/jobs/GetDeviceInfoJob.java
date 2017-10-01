package genius.tv.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.squareup.otto.Bus;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import genius.tv.DigifyApp;
import genius.tv.api.DigifyApiService;
import genius.tv.core.PreferenceManager;
import genius.tv.db.models.DeviceInfo;
import genius.tv.ui.events.ScreenOrientationEvent;
import genius.tv.ui.viewmodels.ScreenOrientation;
import genius.tv.util.Utils;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static genius.tv.util.Utils.createPortraitFile;
import static genius.tv.util.Utils.getPortraitFile;

/**
 * Created by Joel on 2/13/2017.
 */

public class GetDeviceInfoJob extends Job {
    public static final int PRIORITY = 3;

    @Inject
    DigifyApiService digifyApiService;
    @Inject
    Bus eventBus;
    @Inject
    Provider<Realm> database;
    @Inject
    PreferenceManager preferenceManager;

    public GetDeviceInfoJob() {
        super(new Params(PRIORITY).setDelayMs(5000).requireNetwork().persist());
    }


    @Override
    public void onRun() throws Throwable {
        DigifyApp.get(getApplicationContext()).getComponent().inject(this);


        digifyApiService.getDevice(Utils.getUniqueDeviceID(getApplicationContext())).enqueue(new Callback<DeviceInfo>() {
            @Override
            public void onResponse(Call<DeviceInfo> call, final Response<DeviceInfo> response) {
                if (response.isSuccessful()) {

                    if (response.body().getMode().equals(ScreenOrientation.Portrait.toString())) {
                        preferenceManager.setPortrait(true);
                        eventBus.post(new ScreenOrientationEvent(ScreenOrientation.Portrait));
                    } else {
                        preferenceManager.setPortrait(false);
                        eventBus.post(new ScreenOrientationEvent(ScreenOrientation.Landscape));
                    }


                    FileDownloadListener fileDownloadListener = new FileDownloadListener() {
                        @Override
                        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                        }

                        @Override
                        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                        }

                        @Override
                        protected void completed(BaseDownloadTask task) {

                        }

                        @Override
                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                        }

                        @Override
                        protected void error(BaseDownloadTask task, Throwable e) {

                        }

                        @Override
                        protected void warn(BaseDownloadTask task) {

                        }
                    };

                    final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(fileDownloadListener);
                    final List<BaseDownloadTask> tasks = new ArrayList<>();
                    tasks.add(FileDownloader.
                            getImpl()
                            .create(response.body().getPortraitLogo())
                            .setPath(createPortraitFile(response.body(), getApplicationContext()).getAbsolutePath()));

                    queueSet.setAutoRetryTimes(5);
                    queueSet.downloadSequentially(tasks);

                    DeviceInfo deviceInfo = database.get().where(DeviceInfo.class).findFirst();

                    if (deviceInfo != null) {
                        if (new DateTime(response.body().getUpdatedAt()).isAfter(new DateTime(deviceInfo.getUpdatedAt())) || getPortraitFile(deviceInfo,getApplicationContext())==null)
                            queueSet.start();

                    } else
                        queueSet.start();

                    database.get().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(response.body());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DeviceInfo> call, Throwable t) {
                Timber.e(t);
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

    @Override
    public void onAdded() {


    }
}
