package co.ifwe.versus.modules;

import co.ifwe.versus.views.EndpointView;
import dagger.Module;

@Module(
    addsTo = VersusModule.class,
    complete = false,
    library = true,
    injects = {
            EndpointView.class
    })
public final class VariantModule {
}
