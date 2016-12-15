package digify.tv.ui.activities;

import android.support.v7.app.AppCompatActivity;

import digify.tv.DigifyApp;
import digify.tv.injection.component.ApplicationComponent;

/**
 * Created by Joel on 12/15/2016.
 */

public class BaseActivity extends AppCompatActivity {

    protected ApplicationComponent applicationComponent() {
        return DigifyApp.get(this).getComponent();
    }

}
