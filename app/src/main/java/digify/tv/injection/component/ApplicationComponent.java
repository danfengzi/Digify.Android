package digify.tv.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import digify.tv.DigifyApp;
import digify.tv.api.HostSelectionInterceptor;
import digify.tv.api.RetrofitHelper;
import digify.tv.db.MediaRepository;
import digify.tv.injection.module.ApplicationModule;
import digify.tv.jobs.FetchPlaylistJob;
import digify.tv.ui.activities.BaseActivity;
import digify.tv.ui.activities.LoginActivity;
import digify.tv.ui.activities.MainActivity;
import digify.tv.ui.activities.MainFragment;
import digify.tv.ui.activities.PlaybackOverlayFragment;
import digify.tv.ui.activities.VideoDetailsFragment;

/**
 * Created by Joel on 12/8/2016.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(LoginActivity loginActivity);
    void inject(BaseActivity baseActivity);
    void inject(MainActivity mainActivity);
    void inject(DigifyApp digifyApp);
    void inject(FetchPlaylistJob fetchPlaylistJob);
    void inject(MainFragment mainFragment);
    void inject(VideoDetailsFragment videoDetailsFragment);
    void inject(MediaRepository mediaRepository);
    void inject(PlaybackOverlayFragment playbackOverlayFragment);
    void inject(RetrofitHelper retrofitHelper);
    void inject(HostSelectionInterceptor hostSelectionInterceptor);
}