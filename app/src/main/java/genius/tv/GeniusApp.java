package genius.tv;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

import com.birbit.android.jobqueue.JobManager;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.liulishuo.filedownloader.FileDownloader;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.Iconics;

import net.danlew.android.joda.JodaTimeAndroid;
import net.gotev.speech.Speech;

import javax.inject.Inject;

import genius.tv.core.KioskService;
import genius.tv.core.OnScreenOffReceiver;
import genius.tv.core.PreferenceManager;
import genius.tv.injection.component.ApplicationComponent;
import genius.tv.injection.component.DaggerApplicationComponent;
import genius.tv.injection.module.ApplicationModule;
import genius.tv.jobs.FetchPlaylistJob;
import genius.tv.jobs.FetchSettingsJob;
import genius.tv.jobs.FetchUserDeviceJob;
import genius.tv.jobs.GetDeviceInfoJob;
import genius.tv.util.Utils;
import io.fabric.sdk.android.Fabric;
import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static genius.tv.util.Utils.isEmulator;

/**
 * Created by Joel on 12/7/2016.
 */

public class GeniusApp extends Application {

    ApplicationComponent applicationComponent;
    private static final int JOB_ID = 42334;
    private static final String JOB_PERIODIC_TASK_TAG = "digify.tv.JobPeriodicTask";

    private PowerManager.WakeLock wakeLock;
    private OnScreenOffReceiver onScreenOffReceiver;

    @Inject
    JobManager jobManager;

    @Inject
    PreferenceManager preferenceManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Speech.init(this);

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        JodaTimeAndroid.init(this);

        initializeCustomFontAndIconProvider();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        FileDownloader.init(getApplicationContext());

        applicationComponent.inject(this);

        if (!isEmulator()) {
            Fabric.with(this, new Crashlytics());
            Crashlytics.setUserName(preferenceManager.getTenant());
        }

        scheduleJob();
        setupInAppKioskService();
        registerKioskModeScreenOffReceiver();
        logDeviceDetailsToServices();
    }

    public void setupInAppKioskService() {
        if (preferenceManager.isKioskModeEnabled()) {
            startKioskService();
        }
    }

    public static GeniusApp get(Context context) {
        return (GeniusApp) context.getApplicationContext();
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
                jobManager.addJobInBackground(new FetchUserDeviceJob());
            }
        };

        return new Job.Builder(JOB_ID, callback, Job.Type.JOB_TYPE_ALARM, JOB_PERIODIC_TASK_TAG)
                .setIntervalMillis(5 * 60 * 1000)
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

    private void registerKioskModeScreenOffReceiver() {
        // register screen off receiver
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        onScreenOffReceiver = new OnScreenOffReceiver();
        registerReceiver(onScreenOffReceiver, filter);
    }

    public PowerManager.WakeLock getWakeLock() {
        if (wakeLock == null) {
            // lazy loading: first call, create wakeLock via PowerManager.
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "wakeup");
        }
        return wakeLock;
    }

    private void startKioskService() {
        startService(new Intent(this, KioskService.class));
    }


    private void logDeviceDetailsToServices()
    {
        FirebaseAnalytics.getInstance(this).setUserId(preferenceManager.getName());
        FirebaseAnalytics.getInstance(this).setUserProperty("Tenant",preferenceManager.getTenant());
        FirebaseAnalytics.getInstance(this).setUserProperty("Code",preferenceManager.getCode());
        FirebaseAnalytics.getInstance(this).setUserProperty("Device Id", Utils.getUniqueDeviceID(this));

        if(!isEmulator()) {
            Crashlytics.setUserIdentifier(preferenceManager.getName());
            Crashlytics.setUserName(preferenceManager.getTenant());
            Crashlytics.setUserEmail(preferenceManager.getCode());
            Crashlytics.setString("Device Id",Utils.getUniqueDeviceID(this));
        }
    }
}
