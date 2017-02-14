package digify.tv;

import android.app.Application;
import android.content.Context;

import com.birbit.android.jobqueue.JobManager;
import com.crashlytics.android.Crashlytics;
import com.liulishuo.filedownloader.FileDownloader;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.Iconics;

import net.danlew.android.joda.JodaTimeAndroid;

import javax.inject.Inject;

import digify.tv.injection.component.ApplicationComponent;
import digify.tv.injection.component.DaggerApplicationComponent;
import digify.tv.injection.module.ApplicationModule;
import digify.tv.jobs.FetchPlaylistJob;
import digify.tv.jobs.FetchSettingsJob;
import digify.tv.jobs.GetDeviceInfoJob;
import io.fabric.sdk.android.Fabric;
import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static digify.tv.util.Utils.isEmulator;

/**
 * Created by Joel on 12/7/2016.
 */

public class DigifyApp extends Application {

    ApplicationComponent applicationComponent;
    private static final int JOB_ID = 42334;
    private static final String JOB_PERIODIC_TASK_TAG = "digify.tv.JobPeriodicTask";

    @Inject
    JobManager jobManager;

    @Override
    public void onCreate() {
        super.onCreate();

        if (!isEmulator())
            Fabric.with(this, new Crashlytics());

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        JodaTimeAndroid.init(this);

        initializeCustomFontAndIconProvider();

        FileDownloader.init(getApplicationContext());

        applicationComponent.inject(this);

        scheduleJob();
    }

    public static DigifyApp get(Context context) {
        return (DigifyApp) context.getApplicationContext();
    }

    public void initializeCustomFontAndIconProvider() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("Roboto-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Iconics.registerFont(new CommunityMaterial());
    }

    public ApplicationComponent getComponent() {
        return applicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        this.applicationComponent = applicationComponent;
    }

    public Job createJob() {
        SmartScheduler.JobScheduledCallback callback = new SmartScheduler.JobScheduledCallback() {
            @Override
            public void onJobScheduled(Context context, Job job) {
                jobManager.addJobInBackground(new FetchPlaylistJob());
                jobManager.addJobInBackground(new GetDeviceInfoJob());
                jobManager.addJobInBackground(new FetchSettingsJob());
            }
        };

        return new Job.Builder(JOB_ID, callback, Job.Type.JOB_TYPE_ALARM, JOB_PERIODIC_TASK_TAG)
                .setPeriodic(10 * 60 * 1000)
                .build();


    }

    public void scheduleJob() {
        SmartScheduler jobScheduler = SmartScheduler.getInstance(this);

        // Check if any periodic job is currently scheduled
        if (jobScheduler.contains(JOB_ID)) {
            removePeriodicJob();
            return;
        }

        // Create a new job with specified params
        Job job = createJob();
        if (job == null) {
            return;
        }

        // Schedule current created job
        jobScheduler.addJob(job);
    }

    public void removePeriodicJob() {
        SmartScheduler jobScheduler = SmartScheduler.getInstance(this);
        if (!jobScheduler.contains(JOB_ID)) {
            return;
        }

        jobScheduler.removeJob(JOB_ID);

    }


}
