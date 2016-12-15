package digify.tv.ui.activities;

import android.os.Bundle;

import javax.inject.Inject;

import digify.tv.R;
import digify.tv.api.DigifyApiService;
import digify.tv.util.*;
import eu.inloop.easygcm.EasyGcm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

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
        Call<Void> loginCall = digifyApiService.assignmentRequest(EasyGcm.getRegistrationId(this),Utils.getUniquePsuedoID());

        loginCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

}
