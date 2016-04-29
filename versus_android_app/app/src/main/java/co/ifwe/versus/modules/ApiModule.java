package co.ifwe.versus.modules;

import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import co.ifwe.versus.api.CategoriesApi;
import co.ifwe.versus.api.ConversationsApi;
import co.ifwe.versus.api.PubnubApi;
import co.ifwe.versus.api.UserApi;
import co.ifwe.versus.api.VersusApi;
import co.ifwe.versus.utils.VersusPrefUtils;
import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(
        complete = false,
        library = true
)
public final class ApiModule {

    private static final String GETTY_URL = "GETTY_URL";
    private static final String VERSUS_URL = "VERSUS_URL";
    private static final String PUBNUB = "PUBNUB";
    private static final String PUBNUB_URL = "PUBNUB_URL";
    private static final String VERSUS_SERVER = "VERSUS";

    @Provides
    @Singleton
    HttpUrl provideBaseUrl(SharedPreferences preferences) {
        String server = VersusPrefUtils.getServer(preferences);
        return HttpUrl.parse("http://" + server + ":8080");
    }

    @Provides
    @Singleton
    @Named(GETTY_URL)
    HttpUrl provideGettyUrl() {
        return HttpUrl.parse("https://api.gettyimages.com/v3/");
    }

    @Provides
    @Singleton
    @Named(PUBNUB_URL)
    HttpUrl providePubnubUrl() {
        return HttpUrl.parse("http://pubsub.pubnub.com");
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    Retrofit provideVersusRetrofit(HttpUrl baseUrl, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    @Named(PUBNUB)
    Retrofit providePubnubRetrofit(@Named(PUBNUB_URL) HttpUrl url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    CategoriesApi provideCategoriesApi(Retrofit retrofit) {
        return retrofit.create(CategoriesApi.class);
    }

    @Provides
    ConversationsApi provideConversationsApi(Retrofit retrofit) {
        return retrofit.create(ConversationsApi.class);
    }

    @Provides
    UserApi provideUserApi(Retrofit retrofit) {
        return retrofit.create(UserApi.class);
    }

    @Provides
    VersusApi provideVersusApi(Retrofit retrofit) {
        return retrofit.create(VersusApi.class);
    }

    @Provides
    PubnubApi providePubnubApi(@Named(PUBNUB) Retrofit retrofit) {
        return retrofit.create(PubnubApi.class);
    }
}