package digify.tv;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.liulishuo.filedownloader.FileDownloader;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.Iconics;

import net.danlew.android.joda.JodaTimeAndroid;

import javax.inject.Inject;

import digify.tv.api.DigifyApiService;
import digify.tv.injection.component.ApplicationComponent;
import digify.tv.injection.component.DaggerApplicationComponent;
import digify.tv.injection.module.ApplicationModule;
import digify.tv.util.Utils;
import eu.inloop.easygcm.EasyGcm;
import eu.inloop.easygcm.GcmListener;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Joel on 12/7/2016.
 */

public class DigifyApp extends Application implements GcmListener {

    ApplicationComponent applicationComponent;

    @Inject
    DigifyApiService digifyApiService;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        JodaTimeAndroid.init(this);

        initializeCustomFontAndIconProvider();

        getComponent().inject(this);

        FileDownloader.init(getApplicationContext());

        EasyGcm.init(this);
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

    @Override
    public void onMessage(String s, Bundle bundle) {
        Log.v("gcm_message", bundle.getString("message"));
        Log.v("gcm_id", s);
    }

    @Override
    public void sendRegistrationIdToBackend(String pushId) {
        Call<Void> request = digifyApiService.updatePushId(Utils.getUniqueDeviceID(this), pushId);

        request.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
