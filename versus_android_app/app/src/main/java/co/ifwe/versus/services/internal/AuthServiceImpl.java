package co.ifwe.versus.services.internal;

import android.content.SharedPreferences;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.tagged.caspr.callback.Callback;

import java.io.IOException;

import javax.inject.Inject;

import co.ifwe.versus.api.UserApi;
import co.ifwe.versus.models.LoginRequest;
import co.ifwe.versus.models.User;
import co.ifwe.versus.services.AuthService;
import co.ifwe.versus.services.VersusService;
import co.ifwe.versus.utils.VersusPrefUtils;
import retrofit2.Call;
import retrofit2.Response;

public class AuthServiceImpl extends VersusService implements AuthService  {

    @Inject
    UserApi mUserApi;

    @Inject
    SharedPreferences mSharedPreferences;

    public AuthServiceImpl() {
        super(AuthServiceImpl.class.getCanonicalName());
    }

    @Override
    public void login(Callback<User> callback) {
        Profile profile = Profile.getCurrentProfile();
        AccessToken token = AccessToken.getCurrentAccessToken();

        LoginRequest request = new LoginRequest(profile.getId(), profile.getName(), token.getToken());
        Call<User> call = mUserApi.loginUser(request);
        try {
            Response<User> response = call.execute();
            User user = response.body();
            if (user != null) {
                VersusPrefUtils.saveUserId(mSharedPreferences, user.getFacebookId());
                callback.onSuccess(response.body());
            } else {
                callback.onError(ServiceResult.ERROR_UNKNOWN);
            }
        } catch (IOException e) {
            callback.onError(ServiceResult.ERROR_UNKNOWN);
        }
    }

    @Override
    public void getUser(Callback<User> callback) {
        Profile profile = Profile.getCurrentProfile();
        AccessToken token = AccessToken.getCurrentAccessToken();

        if (profile == null || token == null) {
            callback.onError(ServiceResult.ERROR_IM_A_TEAPOT);
        }
        Call<User> call = mUserApi.getUser(profile.getId(), token.getToken());
        try {
            Response<User> response = call.execute();
            if (response.code() == 200) {
                User user = response.body();
                VersusPrefUtils.saveUserId(mSharedPreferences, user.getFacebookId());
                callback.onSuccess(response.body());
            } else {
                callback.onError(response.code());
            }
        } catch (IOException e) {
            callback.onError(ServiceResult.ERROR_UNKNOWN);
        }
    }
}
