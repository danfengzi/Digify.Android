package digify.tv.core;

import android.app.IntentService;
import android.content.Intent;

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

import digify.tv.DigifyApp;
import digify.tv.api.DigifyApiService;
import digify.tv.db.models.DeviceInfo;
import digify.tv.ui.events.ScreenOrientationEvent;
import digify.tv.ui.viewmodels.ScreenOrientation;
import digify.tv.util.Utils;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static digify.tv.util.Utils.createPortraitFile;
import static digify.tv.util.Utils.getPortraitFile;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DeviceInfoService extends IntentService {
    @Inject
    DigifyApiService digifyApiService;
    @Inject
    Bus eventBus;
    @Inject
    Provider<Realm> database;
    @Inject
    PreferenceManager preferenceManager;

    public DeviceInfoService() {
        super(DeviceInfoService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DigifyApp.get(getApplicationContext()).getComponent().inject(this);


        eventBus.register(this);

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

            }
        });

    }
}
