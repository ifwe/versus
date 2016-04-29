package co.ifwe.versus.modules;

import com.pubnub.api.Pubnub;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public final class PubnubModule {
    private Pubnub mPubnub;

    public PubnubModule() {
        mPubnub = new Pubnub("pub-c-8028947a-380c-420e-be14-cc9216642edd", "sub-c-78d1252e-e658-11e5-a4f2-0619f8945a4f");
    }

    @Provides
    @Singleton
    Pubnub providePubnub() {
        return mPubnub;
    }
}
