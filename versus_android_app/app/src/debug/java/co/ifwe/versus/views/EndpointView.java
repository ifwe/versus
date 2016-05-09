package co.ifwe.versus.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import co.ifwe.versus.activities.LoginActivity;
import co.ifwe.versus.dagger.Injector;
import co.ifwe.versus.fragments.ServerAddressDialogFragment;
import co.ifwe.versus.utils.VersusPrefUtils;

public class EndpointView extends TextView {

    @Inject
    SharedPreferences mSharedPreferences;

    private OnClickListener mOnClickListener = v -> showEndpointDialogChooser();
    String mServer;

    public EndpointView(Context context) {
        this(context, null);
    }

    public EndpointView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EndpointView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
        ButterKnife.bind(this);

        Injector.get().inject(this);
        mServer = VersusPrefUtils.getServer(mSharedPreferences);
        setOnClickListener(mOnClickListener);

        setText(mServer);
    }

    private void showEndpointDialogChooser() {
        Context context = getContext();
        FragmentActivity activity = (FragmentActivity) context;
        FragmentManager fm = activity.getSupportFragmentManager();
        ServerAddressDialogFragment dialog = new ServerAddressDialogFragment();
        dialog.setServer(mServer);
        dialog.setServerAddressSelectListener((sslEnabled, serverAddress, port) -> {
            setNewEndpoint(serverAddress);
        });

        dialog.show(fm, "endpoint_dialog_fragment");
    }

    private void setNewEndpoint(String serverAddress) {
        if (!TextUtils.equals(mServer, serverAddress)) {
            // Save new host value to preference
            mServer = serverAddress;
            VersusPrefUtils.saveServer(mSharedPreferences, serverAddress);

            // Rebuild object graph
            Injector.get().buildObjectGraph();

            // Re inject all services for LaunchActivity
            LoginActivity.start(getContext());
            EndpointView.this.setText(mServer);
        }
    }
}
