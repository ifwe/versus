package co.ifwe.versus.modules;

import android.content.Context;

import com.tagged.caspr.CasprAdapter;

import javax.inject.Singleton;

import co.ifwe.versus.services.AppService;
import co.ifwe.versus.services.AuthService;
import co.ifwe.versus.services.ConversationsService;
import co.ifwe.versus.services.PubnubService;
import co.ifwe.versus.services.StartupService;
import co.ifwe.versus.services.TopicsService;
import co.ifwe.versus.services.internal.AppServiceImpl;
import co.ifwe.versus.services.internal.AuthServiceImpl;
import co.ifwe.versus.services.internal.ConversationsServiceImpl;
import co.ifwe.versus.services.internal.PubnubServiceImpl;
import co.ifwe.versus.services.internal.StartupServiceImpl;
import co.ifwe.versus.services.internal.TopicsServiceImpl;
import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class ServicesModule {

    @Provides
    @Singleton
    CasprAdapter provideCasprAdapter(Context context) {
        return new CasprAdapter(context);
    }

    @Provides
    AuthService provideAuthService(CasprAdapter adapter) {
        return adapter.create(AuthService.class, AuthServiceImpl.class);
    }

    @Provides
    ConversationsService provideConversationsService(CasprAdapter adapter) {
        return adapter.create(ConversationsService.class, ConversationsServiceImpl.class);
    }

    @Provides
    TopicsService provideTopicsService(CasprAdapter adapter) {
        return adapter.create(TopicsService.class, TopicsServiceImpl.class);
    }

    @Provides
    StartupService provideStartupService(CasprAdapter adapter) {
        return adapter.create(StartupService.class, StartupServiceImpl.class);
    }

    @Provides
    PubnubService providePubnubService(CasprAdapter adapter) {
        return adapter.create(PubnubService.class, PubnubServiceImpl.class);
    }

    @Provides
    AppService provideAppService(CasprAdapter adapter) {
        return adapter.create(AppService.class, AppServiceImpl.class);
    }
}
