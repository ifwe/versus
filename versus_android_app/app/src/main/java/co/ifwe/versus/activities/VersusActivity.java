package co.ifwe.versus.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.tagged.caspr.CasprAdapter;
import com.tagged.caspr.CasprUtils;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import co.ifwe.versus.VersusApplication;
import co.ifwe.versus.dagger.Injector;
import dagger.ObjectGraph;

public abstract class VersusActivity extends AppCompatActivity {

    private Collection<Integer> mCasprProxies;

    @Inject
    protected CasprAdapter mCasprAdapter;

    private ObjectGraph mObjectGraph;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initInjects();
    }

    private boolean initInjects() {
        try {
            getObjectGraph().inject(this);
            mCasprProxies = CasprUtils.collectProxies(this);
            return true;
        } catch (IllegalArgumentException e) {
            // inject WILL fail for all authenticated injectables. caller will handle this by checking userId
            Crashlytics.logException(e);
            return false;
        }
    }

    public ObjectGraph getObjectGraph(Object... plusModules) {
        if (mObjectGraph == null) {
            // Build activity object graph
            Object[] modules = getModules();
            mObjectGraph = Injector.get().getObjectGraph(modules);
        }

        if (plusModules == null) {
            return mObjectGraph;
        } else {
            return mObjectGraph.plus(plusModules);
        }
    }

    public Object[] getModules() {
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCasprProxies != null) {
            CasprUtils.register(mCasprAdapter, mCasprProxies);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        VersusApplication.onActivityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        VersusApplication.onActivityPaused();
    }

    @Override
    protected void onStop() {
        if (mCasprProxies != null) {
            CasprUtils.unregister(mCasprAdapter, mCasprProxies);
        }
        super.onStop();
    }
}
