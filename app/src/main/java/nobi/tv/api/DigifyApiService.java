package nobi.tv.api;

import java.util.List;

import nobi.tv.db.models.DeviceInfo;
import nobi.tv.api.models.LoginResponseModel;
import nobi.tv.api.models.SettingsModel;
import nobi.tv.api.models.UserDeviceModel;
import nobi.tv.db.models.Media;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Joel on 12/9/2016.
 */
public interface DigifyApiService {

    @POST("device/assign")
    Call<LoginResponseModel> assignmentRequest(@Query("device_id") String deviceId);

    @GET("device/check_assignment/{device_id}")
    Call<UserDeviceModel> checkAssignment(@Path("device_id") String deviceId);

    @GET("device/playlist/{device_id}")
    Call<List<Media>> getDevicePlaylist(@Path("device_id") String deviceId);

    @GET("settings")
    Call<SettingsModel> getOrganizationSettings();

    @GET("device/{device_id}")
    Call<DeviceInfo> getDevice(@Path("device_id") String deviceId);
}
