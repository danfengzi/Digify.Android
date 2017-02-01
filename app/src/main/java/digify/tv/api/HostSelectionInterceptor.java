package digify.tv.api;

/**
 * Created by Joel on 1/29/2017.
 */

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;

import javax.inject.Inject;

import digify.tv.DigifyApp;
import digify.tv.core.PreferenceManager;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * An interceptor that allows runtime changes to the URL hostname.
 */
public final class HostSelectionInterceptor implements Interceptor {
    private volatile String host;

    @Inject
    PreferenceManager preferenceManager;


    public HostSelectionInterceptor(Context context) {

        DigifyApp.get(context).getComponent().inject(this);


    }

    @Override public okhttp3.Response intercept(Chain chain) throws IOException {

        this.host = preferenceManager.getBaseUrl();

        Request request = chain.request();

        String host = this.host;

        if (!TextUtils.isEmpty(host)) {
            host=host+"/api/";

            HttpUrl newUrl = HttpUrl.parse(host);

            request = request.newBuilder()
                    .url(newUrl)
                    .build();
        }
        return chain.proceed(request);
    }

}