package co.ifwe.versus.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.ifwe.versus.R;
import co.ifwe.versus.activities.ArbitrationActivity;
import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.services.ConversationsService;
import co.ifwe.versus.services.callbacks.StubCallback;
import co.ifwe.versus.utils.FragmentState;

public class ArbitrationStartFragment extends VersusFragment {

    private static final String TAG = ArbitrationStartFragment.class.getCanonicalName();

    @Bind(R.id.arbitration_start_button)
    Button mStartButton;

    @Inject
    ConversationsService mConversationsService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.title_arbitration);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arbitration_start, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.arbitration_start_button)
    public void onStartClick() {
        mConversationsService.getConversationForArbitration(new StubCallback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                super.onSuccess(conversation);
                ArbitrationActivity.start(getActivity(), conversation);
            }

            @Override
            public void onError(int errorCode) {
                super.onError(errorCode);
                new MaterialDialog.Builder(getActivity())
                        .content(R.string.arbitration_empty_message)
                        .positiveText(R.string.arbitration_empty_button)
                        .show();
                Log.e(TAG, "Error getting conversation for judgement");
            }

            @Override
            public void onComplete() {
                super.onComplete();
                //todo: add and remove loading thingy
            }
        });
    }

    public static Bundle createState() {
        return FragmentState.create(ArbitrationStartFragment.class, new Bundle());
    }
}
