package digify.tv.core;

import android.content.Context;

import digify.tv.DigifyApp;
import digify.tv.injection.component.ApplicationComponent;

/**
 * Created by Joel on 12/10/2016.
 */

public abstract class BaseComponent {
    private Context context;

    public BaseComponent(Context context) {
        this.context = context;
    }

    protected ApplicationComponent applicationComponent() {
        return DigifyApp.get(context).getComponent();
    }

    public Context getContext() {
        return context;
    }
}