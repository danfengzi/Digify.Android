package digify.tv;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.Iconics;

import net.danlew.android.joda.JodaTimeAndroid;

import digify.tv.injection.component.ApplicationComponent;
import digify.tv.injection.component.DaggerApplicationComponent;
import digify.tv.injection.module.ApplicationModule;
import eu.inloop.easygcm.GcmListener;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Joel on 12/7/2016.
 */

public class DigifyApp extends Application implements GcmListener{

    ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        JodaTimeAndroid.init(this);

        initializeCustomFontAndIconProvider();
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
        Log.v("gcm_message",bundle.getString("message"));
        Log.v("gcm_id",s);
    }

    @Override
    public void sendRegistrationIdToBackend(String s) {

    }
}
