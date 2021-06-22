package io.thoughtbox.hamdan.injections;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import io.thoughtbox.hamdan.utls.Constants;
import io.thoughtbox.hamdan.services.DataService;
import io.thoughtbox.hamdan.services.ServiceRequest;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Module
public class ApiModule {
    private Retrofit retrofit = null;


    @Provides
    public DataService getDataService() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(Constants.BaseURL)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        return retrofit.create(DataService.class);
    }

    @Provides
    public ServiceRequest getServiceRequest() {
        return ServiceRequest.getInstance();
    }
}
