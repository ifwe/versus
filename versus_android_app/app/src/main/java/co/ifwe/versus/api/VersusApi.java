package co.ifwe.versus.api;

import co.ifwe.versus.models.VersionResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VersusApi {
    @GET("versus/")
    Call<Void> checkConnection();

    @GET("versus/version")
    Call<VersionResponse> checkVersion(@Query("version") int versionCode);
}
