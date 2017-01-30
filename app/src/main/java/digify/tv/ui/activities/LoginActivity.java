package digify.tv.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.otto.Subscribe;
import com.wang.avi.AVLoadingIndicatorView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import digify.tv.DigifyApp;
import digify.tv.R;
import digify.tv.api.DigifyApiService;
import digify.tv.api.models.LoginResponseModel;
import digify.tv.api.models.UserDeviceModel;
import digify.tv.core.PreferenceManager;
import digify.tv.injection.component.ApplicationComponent;
import digify.tv.ui.events.DeviceAssignedEvent;
import digify.tv.util.Utils;
import es.dmoral.toasty.Toasty;
import eu.inloop.easygcm.EasyGcm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends LoginBaseActivity {

    @Inject
    DigifyApiService digifyApiService;
    @Inject
    PreferenceManager preferenceManager;

    @BindView(R.id.company_name)
    TextView companyName;
    @BindView(R.id.instruction)
    TextView instruction;
    @BindView(R.id.code)
    TextView code;
    @BindView(R.id.loading_view)
    AVLoadingIndicatorView loadingView;
    @BindView(R.id.activity_login)
    RelativeLayout activityLogin;
    @BindView(R.id.sync_button)
    ImageView syncButton;
    @BindView(R.id.sync_info)
    LinearLayout syncInfo;
    @BindView(R.id.login_button)
    ImageView loginButton;
    @BindView(R.id.login_layout)
    LinearLayout loginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        applicationComponent().inject(this);
        EasyGcm.init(this);
        login();
        setRipples();


    }

    public void setRipples() {
        MaterialRippleLayout.on(syncButton)
                .rippleColor(Color.WHITE)
                .create();

        MaterialRippleLayout.on(loginButton)
                .rippleColor(Color.WHITE)
                .create();


    }

    @OnClick(R.id.login_button)
    public void login() {

        if (!isAppOnline())
            return;

        if (!Utils.isGooglePlayServicesAvailable(this))
            return;

        loginLayout.setVisibility(View.GONE);
        loadingView.smoothToShow();
        instruction.setText("Syncing device for first use");
        code.setText("Please Wait");




        Call<LoginResponseModel> loginCall = digifyApiService.assignmentRequest(EasyGcm.getRegistrationId(this), Utils.getUniqueDeviceID(this));

        loginCall.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, final Response<LoginResponseModel> response) {
                if (response.body() != null) {
                    Handler handler = new Handler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            instruction.setText("Enter this code into your dashboard");
                            loadingView.hide();
                            code.setText(response.body().getCode());
                            syncInfo.setVisibility(View.VISIBLE);
                        }
                    }, 5000);

                }

            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                Toasty.error(LoginActivity.this, "Something went wrong. Press enter to retry!", Toast.LENGTH_LONG).show();
                loginLayout.setVisibility(View.VISIBLE);
                loadingView.smoothToHide();
                code.setText("Uh oh!");

                syncInfo.setVisibility(View.INVISIBLE);
            }
        });
    }

    public boolean isAppOnline() {

        if (Utils.isOnline(this))
            return true;

        else {
            Toasty.error(LoginActivity.this, "Internet is required!", Toast.LENGTH_LONG).show();
            loginLayout.setVisibility(View.VISIBLE);
            syncInfo.setVisibility(View.INVISIBLE);
            code.setText("Uh oh!");
            loadingView.smoothToHide();

            return false;
        }
    }

    protected ApplicationComponent applicationComponent() {
        return DigifyApp.get(this).getComponent();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Subscribe
    public void deviceAssigned(DeviceAssignedEvent event) {
        checkAssignment();
    }

    @OnClick(R.id.sync_button)
    public void checkAssignment() {
        Call<UserDeviceModel> request = digifyApiService.checkAssignment(Utils.getUniqueDeviceID(this));

        request.enqueue(new Callback<UserDeviceModel>() {
            @Override
            public void onResponse(Call<UserDeviceModel> call, Response<UserDeviceModel> response) {

                if (response.isSuccessful()) {
                    Toasty.success(LoginActivity.this, "Device was assigned!", Toast.LENGTH_SHORT, true).show();

                    preferenceManager.setLoggedInStatus(true);
                    preferenceManager.setName(response.body().getName());

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                } else
                    Toasty.error(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<UserDeviceModel> call, Throwable t) {
                Toasty.error(LoginActivity.this, "Not yet registered!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
