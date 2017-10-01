package genius.tv.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import genius.tv.GeniusApp;
import genius.tv.api.HostSelectionInterceptor;
import genius.tv.api.RetrofitHelper;
import genius.tv.core.CustomerProcessor;
import genius.tv.core.DeviceInfoService;
import genius.tv.core.GetUserDeviceService;
import genius.tv.core.KioskService;
import genius.tv.core.OnScreenOffReceiver;
import genius.tv.db.MediaRepository;
import genius.tv.injection.module.ApplicationModule;
import genius.tv.jobs.FetchPlaylistJob;
import genius.tv.jobs.FetchSettingsJob;
import genius.tv.jobs.FetchUserDeviceJob;
import genius.tv.jobs.GetDeviceInfoJob;
import genius.tv.ui.activities.BaseActivity;
import genius.tv.ui.activities.LandscapeMediaActivity;
import genius.tv.ui.activities.LoginActivity;
import genius.tv.ui.activities.MainActivity;
import genius.tv.ui.activities.MainFragment;
import genius.tv.ui.activities.PlaybackOverlayFragment;
import genius.tv.ui.activities.PortraitMediaActivity;
import genius.tv.ui.activities.QueueModeActivity;
import genius.tv.ui.activities.VideoDetailsFragment;
import genius.tv.ui.fragments.QueueFragment;

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
    void inject(GeniusApp geniusApp);
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