package co.ifwe.versus.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import co.ifwe.versus.R;
import co.ifwe.versus.models.Server;

public class ServerAddressDialogFragment extends DialogFragment {

    private Server mServer;
    private String mAddress;
    private String mPort;
    private boolean mSslEnabled;

    @Bind(R.id.preview)
    TextView mPreviewView;

    @Bind(R.id.server_address)
    EditText mServerAddressEditText;

    @Bind(R.id.clear_address_button)
    ImageButton mClearAddressButton;

    @Bind(R.id.port)
    EditText mPortEditText;

    @Bind(R.id.clear_port_button)
    ImageButton mClearPortButton;

    @Bind(R.id.sslEnabledSelector)
    RadioGroup mSslEnbaledSelector;

    @Bind(R.id.ok)
    Button mOkButton;

    @Bind(R.id.server_list_view)
    ListView mListView;

    private ArrayAdapter<String> mAdapter;

    private ServerAddressSelectListener mServerAddressSelectListener;

    public interface ServerAddressSelectListener {
        void onEndpointSelected(boolean sslEnabled, String address, String port);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_single_choice, Server.getNames());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server_address_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mServerAddressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAddress = s.toString();
                mOkButton.setEnabled(mServer != Server.OTHER || !TextUtils.isEmpty(mAddress));
                updatePreview();
            }
        });

        int tierPosition = Server.findHostPosition(mAddress, Server.OTHER.ordinal());
        mServer = Server.values()[tierPosition];

        mListView.setAdapter(mAdapter);
        mListView.setItemChecked(tierPosition, true);
        if (mServer != Server.OTHER) {
            mAddress = "";
        }

        mSslEnbaledSelector.setOnCheckedChangeListener((group, checkedId) -> {
            switch(checkedId) {
                case R.id.https_button:
                    mSslEnabled = true;
                    break;
                case R.id.http_button:
                    mSslEnabled = false;
                    break;
            }
        });

        updatePreview();
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mServerAddressEditText.setSelection(mServerAddressEditText.getText().length());
    }

    public void setServerAddressSelectListener(ServerAddressSelectListener serverAddressSelectListener) {
        mServerAddressSelectListener = serverAddressSelectListener;
    }

    @OnClick(R.id.clear_address_button)
    void onClearServer() {
        mServerAddressEditText.setText(null);
        mAddress = "";
    }

    @OnClick(R.id.clear_port_button)
    void onClearPort() {
        mPortEditText.setText(null);
        mPort = "8080";
    }

    @OnItemClick(R.id.server_list_view)
    void onListedServerClick(int position) {
        mListView.setItemChecked(position, true);
        int tierPosition = mListView.getCheckedItemPosition();
        mServer = Server.values()[tierPosition];
        setAddressEnabled(mServer == Server.OTHER);
        mOkButton.setEnabled(mServer != Server.OTHER || !TextUtils.isEmpty(mAddress));

        updatePreview();
    }

    @OnClick(R.id.ok)
    void onSetEndpoint() {
        if (mServerAddressSelectListener != null) {
            mServerAddressSelectListener.onEndpointSelected(mSslEnabled, getServerAddress(), mPort);
        }
        dismiss();
    }

    @OnClick(R.id.cancel)
    void onCancel() {
        dismiss();
    }

    private void setAddressEnabled(boolean enabled) {
        mServerAddressEditText.setEnabled(enabled);
        mClearAddressButton.setEnabled(enabled);
    }

    private void updatePreview() {
        mPreviewView.setText(getPreviewAddress());
    }

    private String getPreviewAddress() {
        StringBuilder builder = new StringBuilder();
        builder.append(mSslEnabled ? "https" : "http");
        builder.append("://");
        builder.append(getServerAddress());
        builder.append(":");
        builder.append(TextUtils.isEmpty(mPort) ? "8080" : mPort);
        return builder.toString();
    }

    private String getServerAddress() {
        if (mServer == Server.OTHER) {
            return mAddress;
        } else {
            return mServer.getHost();
        }
    }

    public void setServer(String server) {
        mAddress = server;
    }

    public void setPort(String port) {
        mPort = port;
    }

    public void setSslEnabled(boolean sslEnabled) {
        mSslEnabled = sslEnabled;
    }

}
