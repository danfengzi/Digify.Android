package digify.tv.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import javax.inject.Inject;

import digify.tv.DigifyApp;
import digify.tv.R;
import digify.tv.api.DigifyApiService;
import digify.tv.api.models.LoginResponseModel;
import digify.tv.injection.component.ApplicationComponent;
import digify.tv.util.*;
import eu.inloop.easygcm.EasyGcm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity {

    @Inject
    DigifyApiService digifyApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        applicationComponent().inject(this);
        EasyGcm.init(this);
        login();

    }

    public void login()
    {
        if(!Utils.isGooglePlayServicesAvailable(this))
            return;

        Call<LoginResponseModel> loginCall = digifyApiService.assignmentRequest(EasyGcm.getRegistrationId(this),Utils.getUniquePsuedoID());

        loginCall.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                Log.v("test",response.body().getCode());

            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
            }
        });
    }

    protected ApplicationComponent applicationComponent() {
        return DigifyApp.get(this).getComponent();
    }


}
