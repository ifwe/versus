package co.ifwe.versus.dagger;

import android.view.View;

import dagger.ObjectGraph;

public final class Injector {
    private static Injector sInjector;

    private final Object[] mModulesList;
    private ObjectGraph mObjectGraph;

    public static void init(Object[] modulesList) {
        sInjector = new Injector(modulesList);
    }

    public static Injector get() {
        return sInjector;
    }

    public Injector(Object[] modulesList) {
        mModulesList = modulesList;
        buildObjectGraph();
    }

    public void inject(Object object) {
        mObjectGraph.inject(object);
    }

    public void injectView(View view) {
        if (view.isInEditMode()) {
            return;
        }
        inject(view);
    }

    public <T> T getObject(Class<T> type) {
        return mObjectGraph.get(type);
    }

    public void buildObjectGraph() {
        mObjectGraph = ObjectGraph.create(mModulesList);
        mObjectGraph.injectStatics();
    }

    public ObjectGraph getObjectGraph(Object... modules) {
        if (modules == null) {
            return mObjectGraph;
        } else {
            return mObjectGraph.plus(modules);
        }
    }
}
