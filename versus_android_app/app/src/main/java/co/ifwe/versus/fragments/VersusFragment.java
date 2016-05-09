package co.ifwe.versus.fragments;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.View;

import com.facebook.Profile;
import com.tagged.caspr.CasprAdapter;
import com.tagged.caspr.CasprUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.Collection;

import javax.inject.Inject;

import co.ifwe.versus.Constants;
import co.ifwe.versus.R;
import co.ifwe.versus.activities.ConversationActivity;
import co.ifwe.versus.activities.MainActivity;
import co.ifwe.versus.dagger.Injector;
import co.ifwe.versus.events.Bus;
import co.ifwe.versus.events.MatchEvent;
import co.ifwe.versus.events.MessageEvent;
import co.ifwe.versus.models.ChatMessage;

public abstract class VersusFragment extends Fragment {

    private Collection<Integer> mCasprProxies;

    @Inject
    protected CasprAdapter mCasprAdapter;

    @Inject
    protected NotificationManagerCompat mNotificationManager;

    protected String mUserId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Profile profile = Profile.getCurrentProfile();
        mUserId = profile.getId();
        Injector.get().inject(this);
        mCasprProxies = CasprUtils.collectProxies(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bus.MESSAGE_BUS.register(this);
        Bus.USER_BUS.register(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CasprUtils.register(mCasprAdapter, mCasprProxies);
    }

    @Override
    public void onDestroyView() {
        CasprUtils.unregister(mCasprAdapter, mCasprProxies);
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        Bus.MESSAGE_BUS.unregister(this);
        Bus.USER_BUS.unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onMessageEvent(MessageEvent messageEvent) {
        notifyChat(messageEvent.getTopic(), messageEvent.getChatMessage());
    }

    @Subscribe
    public void onMatchedEvent(MatchEvent matchEvent) {}

    protected void notifyChat(String topic, ChatMessage message) {
        Profile profile = Profile.getCurrentProfile();
        if (TextUtils.equals(message.getUserId(), profile.getId())) {
            return;
        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.ic_stat_icon)
                        .setContentTitle(topic)
                        .setContentText(message.getMessage())
                        .setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null))
                        .setAutoCancel(true);

        Intent mainIntent = MainActivity.createIntent(getActivity());
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent convoIntent = ConversationActivity.createIntent(getActivity(), message.getRoomName());
        final PendingIntent pendingIntent = PendingIntent.getActivities(getActivity(), 0,
                new Intent[] {mainIntent, convoIntent}, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);

        mNotificationManager.notify(Constants.Notification.MATCH_ID, builder.build());
    }
}
