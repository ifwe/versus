package co.ifwe.versus.api;

import co.ifwe.versus.models.LoginRequest;
import co.ifwe.versus.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserApi {
    @POST("users/login")
    Call<User> loginUser(@Body LoginRequest request);

    @GET("users/getUser")
    Call<User> getUser(@Query("fbid") String fbid,
                       @Query("token") String token);
}
