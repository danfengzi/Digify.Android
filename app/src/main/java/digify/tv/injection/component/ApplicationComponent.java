package digify.tv.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import digify.tv.injection.module.ApplicationModule;
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
}