package co.ifwe.versus.services;

import com.tagged.caspr.ICasprService;
import com.tagged.caspr.callback.Callback;

import co.ifwe.versus.models.User;

public interface AuthService extends ICasprService {
    void login(Callback<User> callback);

    void getUser(Callback<User> callback);
}
