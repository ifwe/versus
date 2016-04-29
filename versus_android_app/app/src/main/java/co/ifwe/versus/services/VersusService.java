package co.ifwe.versus.services;

import com.tagged.caspr.CasprAdapter;
import com.tagged.caspr.CasprService;
import com.tagged.caspr.CasprUtils;
import com.tagged.caspr.ICasprService;
import com.tagged.caspr.callback.Callback;

import java.io.IOException;
import java.util.Collection;

import javax.inject.Inject;

import co.ifwe.versus.BuildConfig;
import co.ifwe.versus.dagger.Injector;

public abstract class VersusService extends CasprService implements ICasprService {

    private static final String TAG = VersusService.class.getSimpleName();

    @Inject
    protected CasprAdapter mCasprAdapter;

    private Collection<Integer> mCasprProxies;

    public VersusService(String name) {
        super(name);
    }

    public VersusService(String name, int threadPoolSize) {
        super(name, threadPoolSize);
    }

    public void onCreate() {
        super.onCreate();
        Injector.get().inject(this);

        mCasprProxies = CasprUtils.collectProxies(this);
    }

    @Override
    public void onUnhandledException(Throwable e, Callback callback) {
        final int errorCode = parseError(e);

        if (BuildConfig.DEBUG && ServiceResult.ERROR_UNKNOWN == errorCode) {
            throw new RuntimeException(e);
        }

        if (callback != null) {
            callback.onError(errorCode);
        }
    }

    /**
     * Parses {@link Throwable} into {@link ServiceResult}
     */
    public static int parseError(Throwable t) {
        if (t instanceof IOException) {
            return ServiceResult.ERROR_NETWORK;
        }

        return ServiceResult.ERROR_UNKNOWN;
    }

    @Override
    public void onDestroy() {
        CasprUtils.unregister(mCasprAdapter, mCasprProxies);
        super.onDestroy();
    }

    public interface ServiceResult {
        int ERROR_UNKNOWN = -1;
        int ERROR_NETWORK = -2;
        int ERROR_ARG_INVALID = -3;

        int ERROR_NO_CONTENT = 204;
        int ERROR_UNAUTHORIZED = 401;
        int ERROR_NOT_FOUND = 404;
        int ERROR_IM_A_TEAPOT = 418;
        int ERROR_TOO_MANY_REQUESTS = 429;
        int ERROR_SERVER_ERROR = 500;
    }
}
