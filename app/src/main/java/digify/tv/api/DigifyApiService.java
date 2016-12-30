package digify.tv.api;

import digify.tv.api.models.LoginResponseModel;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Joel on 12/9/2016.
 */
public interface DigifyApiService {

    @POST("device/assign")
    Call<LoginResponseModel> assignmentRequest(@Query("push_id") String pushId, @Query("device_id") String deviceId);
}
