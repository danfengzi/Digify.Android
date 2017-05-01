package digify.tv.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import digify.tv.DigifyApp;
import digify.tv.api.HostSelectionInterceptor;
import digify.tv.api.RetrofitHelper;
import digify.tv.core.DeviceInfoService;
import digify.tv.core.KioskService;
import digify.tv.core.OnScreenOffReceiver;
import digify.tv.core.CustomerProcessor;
import digify.tv.core.SocketService;
import digify.tv.db.MediaRepository;
import digify.tv.injection.module.ApplicationModule;
import digify.tv.jobs.FetchPlaylistJob;
import digify.tv.jobs.FetchSettingsJob;
import digify.tv.jobs.GetDeviceInfoJob;
import digify.tv.ui.activities.BaseActivity;
import digify.tv.ui.activities.LandscapeMediaActivity;
import digify.tv.ui.activities.LoginActivity;
import digify.tv.ui.activities.MainActivity;
import digify.tv.ui.activities.MainFragment;
import digify.tv.ui.activities.PlaybackOverlayFragment;
import digify.tv.ui.activities.PortraitMediaActivity;
import digify.tv.ui.activities.QueueModeActivity;
import digify.tv.ui.activities.VideoDetailsFragment;
import digify.tv.ui.fragments.QueueFragment;

/**
 * Created by Joel on 12/8/2016.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(LoginActivity loginActivity);
    void inject(BaseActivity baseActivity);
    void inject(QueueModeActivity queueModeActivity);
    void inject(PortraitMediaActivity portraitActivity);
    void inject(MainActivity mainActivity);
    void inject(LandscapeMediaActivity landscapeMediaActivity);
    void inject(DigifyApp digifyApp);
    void inject(FetchPlaylistJob fetchPlaylistJob);
    void inject(MainFragment mainFragment);
    void inject(VideoDetailsFragment videoDetailsFragment);
    void inject(MediaRepository mediaRepository);
    void inject(PlaybackOverlayFragment playbackOverlayFragment);
    void inject(RetrofitHelper retrofitHelper);
    void inject(HostSelectionInterceptor hostSelectionInterceptor);
    void inject(FetchSettingsJob fetchSettingsJob);
    void inject(GetDeviceInfoJob getDeviceInfoJob);
    void inject(DeviceInfoService deviceInfoService);
    void inject(SocketService socketService);
    void inject(KioskService kioskService);
    void inject(OnScreenOffReceiver onScreenOffReceiver);
    void inject(QueueFragment queueFragment);
    void inject(CustomerProcessor customerProcessor);
}