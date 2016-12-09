package digify.tv.injection.module;

import android.app.Application;

import dagger.Module;

/**
 * Created by Joel on 12/8/2016.
 */

@Module
public class ApplicationModule {
    final Application app;

    public ApplicationModule(Application app) {
        this.app = app;
    }
}