package digify.tv.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import digify.tv.DigifyApp;
import digify.tv.injection.module.ApplicationModule;
import digify.tv.jobs.FetchPlaylistJob;
import digify.tv.ui.activities.BaseActivity;
import digify.tv.ui.activities.LoginActivity;

/**
 * Created by Joel on 12/8/2016.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(LoginActivity loginActivity);
    void inject(BaseActivity baseActivity);
    void inject(DigifyApp digifyApp);
    void inject(FetchPlaylistJob fetchPlaylistJob);
}