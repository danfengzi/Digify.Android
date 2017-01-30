package digify.tv.api;

/**
 * Created by Joel on 1/29/2017.
 */

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * An interceptor that allows runtime changes to the URL hostname.
 */
public final class HostSelectionInterceptor implements Interceptor {
    private volatile String host;


    public HostSelectionInterceptor(String host) {
        this.host = host;
    }

    @Override public okhttp3.Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        String host = this.host;

        if (!TextUtils.isEmpty(host)) {
            HttpUrl newUrl = request.url().newBuilder()
                    .host(host)
                    .build();
            request = request.newBuilder()
                    .url(newUrl)
                    .build();
        }
        return chain.proceed(request);
    }

}