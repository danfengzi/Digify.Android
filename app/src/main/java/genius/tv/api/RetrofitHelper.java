package genius.tv.api;

import android.content.Context;

import com.github.simonpercic.oklog3.OkLogInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.Date;

import javax.inject.Inject;

import genius.tv.BuildConfig;
import genius.tv.GeniusApp;
import genius.tv.core.GsonDateDeserializer;
import genius.tv.core.MediaGsonConverter;
import genius.tv.core.PreferenceManager;
import genius.tv.db.models.Media;
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

    @Inject
    PreferenceManager preferenceManager;


    public RetrofitHelper(Context context) {
        this.context = context;

        GeniusApp.get(this.context).getComponent().inject(this);
    }

    public DigifyApiService newDigifyApiService() {

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

        OkHttpClient okHttpClient = okHttpBuilder.build();

        Retrofit retrofit;

        if(preferenceManager.isLoggedIn())
        {
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(builder.create()))
                    .baseUrl(preferenceManager.getBaseUrl()+"/api/")
                    .build();
        }
        else
        {
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(builder.create()))
                    .baseUrl("http://api.digify.tv/")
                    .build();
        }



        return retrofit.create(DigifyApiService.class);
    }

}
