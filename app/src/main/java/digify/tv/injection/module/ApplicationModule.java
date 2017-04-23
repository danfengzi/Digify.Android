package digify.tv.injection.module;

import android.app.Application;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pusher.client.Pusher;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import digify.tv.BuildConfig;
import digify.tv.R;
import digify.tv.api.DigifyApiService;
import digify.tv.api.RetrofitHelper;
import digify.tv.core.PreferenceManager;
import digify.tv.db.MediaRepository;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Joel on 12/8/2016.
 */

@Module
public class ApplicationModule {
    final Application app;

    public ApplicationModule(Application app) {
        this.app = app;
    }

    @Singleton
    @Provides
    PreferenceManager providePreferenceManager() {
        return new PreferenceManager(app);
    }

    @Provides
    DigifyApiService provideDigifyService() {
        return new RetrofitHelper(app).newDigifyApiService();
    }

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new Bus(ThreadEnforcer.ANY);
    }

    @Singleton
    @Provides
    JobManager provideJobManager() {
        return new JobManager(provideJobManagerConfiguration());
    }

    Configuration provideJobManagerConfiguration() {
        Configuration.Builder builder = new Configuration.Builder(app)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";

                    @Override
                    public boolean isDebugEnabled() {
                        return false;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);

                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));

                    }

                    @Override
                    public void v(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));

                    }
                })
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120);//wait 2 minute

        return builder.build();
    }

    RealmConfiguration provideRealmConfiguration() {

        Realm.init(app);
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder();
        if (BuildConfig.DEBUG) {
            builder = builder.deleteRealmIfMigrationNeeded();
        }

        return builder.build();

    }

    @Provides
    Pusher providePusher()
    {
        return new Pusher(app.getApplicationContext().getString(R.string.pusher_key));
    }

    @Provides
    Realm provideRealm() {
        return Realm.getInstance(provideRealmConfiguration());
    }

    @Provides
    DatabaseReference provideDatabaseReference()
    {
       return FirebaseDatabase.getInstance().getReference("customers");
    }

    @Provides
    MediaRepository provideMediaRepository() {
        return new MediaRepository(app);
    }
}