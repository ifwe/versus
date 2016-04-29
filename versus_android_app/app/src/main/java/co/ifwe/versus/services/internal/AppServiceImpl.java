package co.ifwe.versus.services.internal;

import com.tagged.caspr.callback.Callback;

import java.io.IOException;

import javax.inject.Inject;

import co.ifwe.versus.BuildConfig;
import co.ifwe.versus.api.VersusApi;
import co.ifwe.versus.models.VersionResponse;
import co.ifwe.versus.services.AppService;
import co.ifwe.versus.services.VersusService;
import retrofit2.Call;
import retrofit2.Response;

public class AppServiceImpl extends VersusService implements AppService {

    @Inject
    VersusApi mVersusApi;

    public AppServiceImpl() {
        super(AppServiceImpl.class.getCanonicalName());
    }

    @Override
    public void checkConnection(Callback<Void> callback) {
        Call<Void> call = mVersusApi.checkConnection();
        try {
            Response<Void> response = call.execute();
            if (response.code() == 200) {
                callback.onSuccess(null);
            } else {
                callback.onError(response.code());
            }
        } catch (IOException e) {
            callback.onError(ServiceResult.ERROR_UNKNOWN);
        }
    }

    @Override
    public void checkVersion(Callback<VersionResponse> callback) {
        Call<VersionResponse> call = mVersusApi.checkVersion(BuildConfig.VERSION_CODE);
        try {
            Response<VersionResponse> response = call.execute();
            if (response.code() == 200) {
                callback.onSuccess(response.body());
            } else {
                callback.onError(response.code());
            }
        } catch (IOException e) {
            callback.onError(ServiceResult.ERROR_UNKNOWN);
        }
    }
}
