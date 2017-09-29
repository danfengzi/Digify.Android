package nobi.tv.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import nobi.tv.DigifyApp;
import nobi.tv.api.HostSelectionInterceptor;
import nobi.tv.api.RetrofitHelper;
import nobi.tv.core.CustomerProcessor;
import nobi.tv.core.DeviceInfoService;
import nobi.tv.core.GetUserDeviceService;
import nobi.tv.core.KioskService;
import nobi.tv.core.OnScreenOffReceiver;
import nobi.tv.db.MediaRepository;
import nobi.tv.injection.module.ApplicationModule;
import nobi.tv.jobs.FetchPlaylistJob;
import nobi.tv.jobs.FetchSettingsJob;
import nobi.tv.jobs.FetchUserDeviceJob;
import nobi.tv.jobs.GetDeviceInfoJob;
import nobi.tv.ui.activities.BaseActivity;
import nobi.tv.ui.activities.LandscapeMediaActivity;
import nobi.tv.ui.activities.LoginActivity;
import nobi.tv.ui.activities.MainActivity;
import nobi.tv.ui.activities.MainFragment;
import nobi.tv.ui.activities.PlaybackOverlayFragment;
import nobi.tv.ui.activities.PortraitMediaActivity;
import nobi.tv.ui.activities.QueueModeActivity;
import nobi.tv.ui.activities.VideoDetailsFragment;
import nobi.tv.ui.fragments.QueueFragment;

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
    void inject(KioskService kioskService);
    void inject(OnScreenOffReceiver onScreenOffReceiver);
    void inject(QueueFragment queueFragment);
    void inject(CustomerProcessor customerProcessor);
    void inject(FetchUserDeviceJob fetchUserDeviceJob);
    void inject(GetUserDeviceService getUserDeviceService);
}