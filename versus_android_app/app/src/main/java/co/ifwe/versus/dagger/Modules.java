package co.ifwe.versus.dagger;

import co.ifwe.versus.VersusApplication;
import co.ifwe.versus.modules.VariantModule;
import co.ifwe.versus.modules.VersusModule;

public final class Modules {
    public static Object[] list(VersusApplication application) {
        return new Object[]{
                new VariantModule(),
                new VersusModule(application),
        };
    }

    private Modules() {
    }
}