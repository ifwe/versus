package co.ifwe.versus.services;

import com.tagged.caspr.ICasprService;
import com.tagged.caspr.callback.Callback;

import co.ifwe.versus.models.VersionResponse;

public interface AppService extends ICasprService {
    void checkConnection(Callback<Void> callback);

    void checkVersion(Callback<VersionResponse> callback);
}
