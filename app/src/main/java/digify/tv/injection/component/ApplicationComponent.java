package digify.tv.injection.component;

import dagger.Component;
import digify.tv.injection.module.ApplicationModule;
import digify.tv.ui.activities.LoginActivity;

/**
 * Created by Joel on 12/8/2016.
 */

@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(LoginActivity loginActivity);
}