package digify.tv.api;

import com.github.simonpercic.oklog3.OkLogInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import digify.tv.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Joel on 12/9/2016.
 */

public class RetrofitHelper {

    public DigifyApiService newDigifyApiService() {

        final GsonBuilder builder = new GsonBuilder();
        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();


        if (BuildConfig.DEBUG) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpBuilder.addInterceptor(logging);

            OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().build();
            okHttpBuilder.addInterceptor(okLogInterceptor);

        }

        OkHttpClient okHttpClient = okHttpBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(builder.create()))
                .baseUrl("http://digify.tv/api/")
                .build();

        return retrofit.create(DigifyApiService.class);
    }
}
