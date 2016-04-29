package co.ifwe.versus.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationManagerCompat;

import javax.inject.Singleton;

import co.ifwe.versus.AppConfig;
import co.ifwe.versus.VersusApplication;
import co.ifwe.versus.activities.ArbitrationActivity;
import co.ifwe.versus.activities.CategoryActivity;
import co.ifwe.versus.activities.ConversationActivity;
import co.ifwe.versus.activities.LoginActivity;
import co.ifwe.versus.activities.MainActivity;
import co.ifwe.versus.activities.VersusActivity;
import co.ifwe.versus.fragments.ArbitrationFragment;
import co.ifwe.versus.fragments.ArbitrationStartFragment;
import co.ifwe.versus.fragments.CategoryListFragment;
import co.ifwe.versus.fragments.ConversationFragmentV2;
import co.ifwe.versus.fragments.ConversationFragmentV3;
import co.ifwe.versus.fragments.ConversationHistoryFragment;
import co.ifwe.versus.fragments.ConversationListFragment;
import co.ifwe.versus.fragments.TopicFragment;
import co.ifwe.versus.fragments.VersusFragment;
import co.ifwe.versus.services.SubscribeService;
import co.ifwe.versus.services.internal.AppServiceImpl;
import co.ifwe.versus.services.internal.AuthServiceImpl;
import co.ifwe.versus.services.internal.ConversationsServiceImpl;
import co.ifwe.versus.services.internal.PubnubServiceImpl;
import co.ifwe.versus.services.internal.StartupServiceImpl;
import co.ifwe.versus.services.internal.TopicsServiceImpl;
import dagger.Module;
import dagger.Provides;

@Module(
    injects = {
            ArbitrationStartFragment.class,
            CategoryListFragment.class,
            ConversationFragmentV2.class,
            ConversationFragmentV3.class,
            ArbitrationFragment.class,
            ConversationHistoryFragment.class,
            ConversationListFragment.class,
            TopicFragment.class,
            VersusFragment.class,
            LoginActivity.class,
            MainActivity.class,
            AppServiceImpl.class,
            AuthServiceImpl.class,
            ConversationsServiceImpl.class,
            PubnubServiceImpl.class,
            StartupServiceImpl.class,
            TopicsServiceImpl.class,
            SubscribeService.class,
            ArbitrationActivity.class,
            CategoryActivity.class,
            ConversationActivity.class,
            VersusActivity.class,
            VersusApplication.class,
    },
    includes = {
            ApiModule.class,
            PubnubModule.class,
            ServicesModule.class,
    },
    library = true
)
public final class VersusModule {
    private final Application mApplication;

    public VersusModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    Context provideContext() {
        return mApplication.getApplicationContext();
    }

    @Provides
    SharedPreferences providesSharedPrefs(Application application) {
        return application.getSharedPreferences(AppConfig.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    @Provides
    NotificationManagerCompat provideNotificationManager(Context context) {
        return NotificationManagerCompat.from(context);
    }
}
