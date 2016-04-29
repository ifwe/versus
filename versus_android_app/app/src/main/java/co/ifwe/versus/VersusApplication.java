package co.ifwe.versus;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.stetho.Stetho;

import java.util.Locale;

import co.ifwe.versus.dagger.Injector;
import co.ifwe.versus.dagger.Modules;
import io.fabric.sdk.android.Fabric;

public final class VersusApplication extends Application {

    private static boolean sIsVisible;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Stetho.initializeWithDefaults(this);

        Injector.init(Modules.list(this));

        FacebookSdk.setApplicationId(AppConfig.FACEBOOK_APP_ID);
        FacebookSdk.sdkInitialize(this);
    }

    public static boolean isVisible() {
        return sIsVisible;
    }

    public static void onActivityResumed() {
        sIsVisible = true;
    }

    public static void onActivityPaused() {
        sIsVisible = false;
    }
}
