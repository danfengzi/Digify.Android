package digify.tv.api;

import android.app.Application;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Joel on 12/10/2016.
 */

public class UnAuthorizedAccessInterceptor implements Interceptor {

    private Application app;



    public UnAuthorizedAccessInterceptor(Application app) {
        this.app = app;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Response response = chain.proceed(chain.request());

        if (response.code() == 401) {
        }

        return response;
    }
}
