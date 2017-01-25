package digify.tv.api;

import java.util.List;

import digify.tv.api.models.LoginResponseModel;
import digify.tv.api.models.UserDeviceModel;
import digify.tv.db.models.Media;
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
    Call<LoginResponseModel> assignmentRequest(@Query("push_id") String pushId, @Query("device_id") String deviceId);

    @GET("device/check_assignment/{device_id}")
    Call<UserDeviceModel> checkAssignment(@Path("device_id") String deviceId);

    @POST("device/push_id/{device_id}")
    Call<Void> updatePushId(@Path("device_id") String deviceId, @Query("push_id") String pushId);

    @GET("device/playlist/{device_id}")
    Call<List<Media>> getDevicePlaylist(@Path("device_id") String deviceId);
}
