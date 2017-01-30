package digify.tv.api;

import android.content.Context;

import com.github.simonpercic.oklog3.OkLogInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.Date;

import javax.inject.Inject;

import digify.tv.BuildConfig;
import digify.tv.DigifyApp;
import digify.tv.core.GsonDateDeserializer;
import digify.tv.core.MediaGsonConverter;
import digify.tv.core.PreferenceManager;
import digify.tv.db.models.Media;
import io.realm.RealmList;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Joel on 12/9/2016.
 */

public class RetrofitHelper {
    private Context context;

    public RetrofitHelper(Context context) {
        this.context = context;

        DigifyApp.get(context).getComponent().inject(this);
    }

    @Inject
    PreferenceManager preferenceManager;

    public DigifyApiService newDigifyApiService() {


        String host = preferenceManager.getBaseUrl();

        final GsonBuilder builder = new GsonBuilder();
        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        builder.registerTypeAdapter(new TypeToken<RealmList<Media>>() {
        }.getType(), new MediaGsonConverter());
        builder.registerTypeAdapter(Date.class, new GsonDateDeserializer());

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();


        if (BuildConfig.DEBUG) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpBuilder.addInterceptor(logging);

            OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().build();
            okHttpBuilder.addInterceptor(okLogInterceptor);

        }

        okHttpBuilder.addInterceptor(new HostSelectionInterceptor(host));

        OkHttpClient okHttpClient = okHttpBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(builder.create()))
                .baseUrl("http://digify.tv/api/")
                .build();

        return retrofit.create(DigifyApiService.class);
    }

}
