package digify.tv.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import digify.tv.DigifyApp;
import digify.tv.core.PreferenceManager;
import digify.tv.injection.component.ApplicationComponent;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Joel on 12/15/2016.
 */

public class BaseActivity extends Activity {

    @Inject
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applicationComponent().inject(this);

        if(!preferenceManager.isLoggedIn())
        {
            Intent intent = new Intent(this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);

            //startActivity(intent);
        }


    }

    protected ApplicationComponent applicationComponent() {
        return DigifyApp.get(this).getComponent();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
