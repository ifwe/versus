package co.ifwe.versus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.ifwe.versus.R;
import co.ifwe.versus.models.User;
import co.ifwe.versus.services.AppService;
import co.ifwe.versus.services.AuthService;
import co.ifwe.versus.services.SubscribeService;
import co.ifwe.versus.services.callbacks.StubCallback;
import co.ifwe.versus.utils.VersusPrefUtils;

public class LoginActivity extends VersusActivity {
    private static final String TAG = LoginActivity.class.getCanonicalName();

    CallbackManager mCallbackManager;
    LoginManager mLoginManager;

    @Inject
    SharedPreferences mSharedPreferences;

    @Inject
    AuthService mAuthService;

    @Inject
    AppService mAppService;

    @Bind(R.id.login_button)
    Button mLoginButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mCallbackManager = CallbackManager.Factory.create();
        mLoginManager = LoginManager.getInstance();
        mLoginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                onFacebookLoginSuccess(loginResult);
            }

            @Override
            public void onCancel() {
                mLoginButton.setVisibility(View.VISIBLE);
                Log.e(TAG, "User cancelled login");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, exception.getMessage());
                mLoginButton.setVisibility(View.VISIBLE);
                new MaterialDialog.Builder(LoginActivity.this)
                        .content(R.string.login_error_facebook)
                        .positiveText(R.string.login_error_ok)
                        .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AccessToken token = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();
        if (token == null || profile == null) {
            mLoginButton.setVisibility(View.VISIBLE);
        } else {
            loginUser();
        }
//        mAppService.checkConnection(new StubCallback<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                super.onSuccess(aVoid);
//            }
//
//            @Override
//            public void onError(int errorCode) {
//                super.onError(errorCode);
//                Toast.makeText(LoginActivity.this, "Cannot connect to server", Toast.LENGTH_LONG).show();
//            }
//        });
    }

    void showUpdateDialog() {
        mLoginButton.setVisibility(View.VISIBLE);
        new MaterialDialog.Builder(this)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .positiveText("Go to Play Store")
                .content("Update required")
                .onPositive((dialog, which) -> goToPlayStore())
                .show();
    }

    void goToPlayStore() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @OnClick(R.id.login_button)
    void onLoginClick() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();
        if (token == null || profile == null) {
            List<String> permissions = new ArrayList<>();
            permissions.add("public_profile");
            permissions.add("email");
            mLoginManager.logInWithReadPermissions(LoginActivity.this, permissions);
        } else {
            loginUser();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onFacebookLoginSuccess(LoginResult loginResult) {
        if (loginResult.getRecentlyDeniedPermissions().contains("public_profile")) {
            new MaterialDialog.Builder(LoginActivity.this)
                    .content(R.string.login_error_permissions)
                    .positiveText(R.string.login_error_ok)
                    .show();
            return;
        }
        mLoginButton.setVisibility(View.GONE);
        Profile profile = Profile.getCurrentProfile();
        if (profile == null) {
            ProfileTracker profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                    stopTracking();
                    loginUser();
                }
            };
            profileTracker.startTracking();
        } else {
            loginUser();
        }
    }

    private void loginUser() {
        mLoginButton.setVisibility(View.GONE);
        mAuthService.login(new StubCallback<User>() {
            @Override
            public void onSuccess(User data) {
                VersusPrefUtils.saveUser(mSharedPreferences, data);
                Intent intent = new Intent(LoginActivity.this, SubscribeService.class);
                startService(intent);
                MainActivity.start(LoginActivity.this);
                finish();
            }

            @Override
            public void onError(int errorCode) {
                super.onError(errorCode);
                mLoginButton.setVisibility(View.VISIBLE);
                new MaterialDialog.Builder(LoginActivity.this)
                        .content(R.string.login_error_versus)
                        .positiveText(R.string.login_error_ok)
                        .show();
            }
        });
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    public static void start(Context context) {
        context.startActivity(createIntent(context));
    }
}
