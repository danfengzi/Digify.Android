package genius.tv.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.view.IconicsImageView;
import com.squareup.otto.Subscribe;
import com.wang.avi.AVLoadingIndicatorView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import genius.tv.DigifyApp;
import genius.tv.R;
import genius.tv.api.DigifyApiService;
import genius.tv.api.models.LoginResponseModel;
import genius.tv.api.models.UserDeviceModel;
import genius.tv.core.PreferenceManager;
import genius.tv.injection.component.ApplicationComponent;
import genius.tv.ui.events.DeviceAssignedEvent;
import genius.tv.util.Utils;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends BaseActivity {

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
    IconicsImageView syncButton;
    @BindView(R.id.sync_info)
    LinearLayout syncInfo;
    @BindView(R.id.login_button)
    IconicsImageView loginButton;
    @BindView(R.id.login_layout)
    LinearLayout loginLayout;

    AnimationDrawable anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setupBackgroundAnim();
        applicationComponent().inject(this);
        login();


    }

    public void setupBackgroundAnim() {
        anim = (AnimationDrawable) activityLogin.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(2000);

    }


    // Stopping animation:- stop the animation on onPause.
    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }


    @OnClick(R.id.login_button)
    public void login() {

        if (!isAppOnline())
            return;

        loginLayout.setVisibility(View.GONE);
        loadingView.smoothToShow();
        instruction.setText("Syncing device for first use");
        code.setText("Please Wait");

        syncButton.requestFocus();

        Call<LoginResponseModel> loginCall = digifyApiService.assignmentRequest(Utils.getUniqueDeviceID(this));

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
                            preferenceManager.setCode(response.body().getCode());
                            syncInfo.setVisibility(View.VISIBLE);

                            checkAssignment(false);
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
        checkAssignment(true);
    }

    @OnClick(R.id.sync_button)
    public void checkAssignment() {
        checkAssignment(true);
    }

    public void checkAssignment(final boolean showToast) {

        Toasty.info(this, "Performing Sync").show();

        scaleSyncButton();

        Call<UserDeviceModel> request = digifyApiService.checkAssignment(Utils.getUniqueDeviceID(this));

        request.enqueue(new Callback<UserDeviceModel>() {
            @Override
            public void onResponse(Call<UserDeviceModel> call, Response<UserDeviceModel> response) {

                if (response.isSuccessful()) {
                    Toasty.success(LoginActivity.this, "Device was assigned!", Toast.LENGTH_SHORT, true).show();

                    preferenceManager.setLoggedInStatus(true);
                    preferenceManager.setName(response.body().getName());
                    preferenceManager.setBaseUrl(response.body().getTenantUrl());
                    preferenceManager.setTenant(response.body().getTenant());

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                } else if (showToast)
                    Toasty.error(LoginActivity.this, "Box not assigned as yet!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<UserDeviceModel> call, Throwable t) {
                if (showToast)
                    Toasty.error(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (anim != null && !anim.isRunning())
            anim.start();

        login();
    }

    public void scaleSyncButton() {

        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 2f, 1f, 2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);     // animation duration in milliseconds
        scaleAnimation.setFillBefore(true);
        syncButton.startAnimation(scaleAnimation);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ScaleAnimation scaleAnimation = new ScaleAnimation(2f, 1f, 2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(500);     // animation duration in milliseconds
                scaleAnimation.setFillBefore(true);
                syncButton.startAnimation(scaleAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public boolean isAutoLogOutEnabled() {
        return false;
    }
}
