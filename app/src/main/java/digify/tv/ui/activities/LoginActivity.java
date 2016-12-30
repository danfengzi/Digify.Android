package digify.tv.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import digify.tv.DigifyApp;
import digify.tv.R;
import digify.tv.api.DigifyApiService;
import digify.tv.api.models.LoginResponseModel;
import digify.tv.injection.component.ApplicationComponent;
import digify.tv.util.Utils;
import eu.inloop.easygcm.EasyGcm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends LoginBaseActivity {

    @Inject
    DigifyApiService digifyApiService;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        applicationComponent().inject(this);
        EasyGcm.init(this);
        login();

    }

    public void login() {
        loadingView.smoothToShow();
        instruction.setText("Syncing device for first use");
        code.setText("Please Wait");


        if (!Utils.isGooglePlayServicesAvailable(this))
            return;

        Call<LoginResponseModel> loginCall = digifyApiService.assignmentRequest(EasyGcm.getRegistrationId(this), Utils.getUniquePsuedoID());

        loginCall.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                if (response.body() != null) {
                    instruction.setText("Enter this code into your dashboard");
                    loadingView.smoothToHide();
                    code.setText(response.body().getCode());
                    syncInfo.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
            }
        });
    }

    protected ApplicationComponent applicationComponent() {
        return DigifyApp.get(this).getComponent();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
