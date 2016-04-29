package co.ifwe.versus.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.Profile;
import com.google.gson.Gson;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import co.ifwe.versus.Constants;
import co.ifwe.versus.R;
import co.ifwe.versus.VersusApplication;
import co.ifwe.versus.activities.ConversationActivity;
import co.ifwe.versus.activities.MainActivity;
import co.ifwe.versus.dagger.Injector;
import co.ifwe.versus.events.Bus;
import co.ifwe.versus.events.MatchEvent;
import co.ifwe.versus.events.MessageEvent;
import co.ifwe.versus.models.ChatMessage;
import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.models.QueueResult;
import co.ifwe.versus.models.Status;
import co.ifwe.versus.provider.Projection;
import co.ifwe.versus.provider.VersusContract;
import co.ifwe.versus.utils.CursorUtils;
import co.ifwe.versus.utils.VersusPrefUtils;

/**
 * Created by schoi on 3/17/16.
 */
public class SubscribeService extends Service {
    private static final String TAG = SubscribeService.class.getCanonicalName();

    PowerManager.WakeLock mWakeLock;

    private String mUserChannel;

    @Inject
    Pubnub mPubnub;

    @Inject
    NotificationManagerCompat mNotificationManager;

    @Inject
    ConversationsService mConversationsService;

    @Inject
    SharedPreferences mSharedPreferences;

    @Inject
    Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.get().inject(this);

        mPubnub.setNonSubscribeTimeout(500);
        mPubnub.setResumeOnReconnect(true);
        mPubnub.setMaxRetries(100);
        mPubnub.setSubscribeTimeout(20000);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SubscribeAtBoot");
        if (mWakeLock != null) {
            mWakeLock.acquire();
            Log.i(TAG, "Partial Wake Lock : " + mWakeLock.isHeld());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!TextUtils.isEmpty(VersusPrefUtils.getUserId(mSharedPreferences))) {
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(VersusContract.Conversations.CONTENT_URI,
                    Projection.CONVERSATION,
                    VersusContract.Conversations.buildStatusSelection(Status.ACTIVE),
                    null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Conversation conversation = Conversation.fromCursor(cursor);
                    subscribeChatChannel(conversation.getRoomName());
                } while (cursor.moveToNext());
            }
            subscribeUserChannel();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWakeLock != null) {
            mWakeLock.release();
            Log.i(TAG, "Partial Wake Lock : " + mWakeLock.isHeld());
            mWakeLock = null;
        }
        mPubnub.unsubscribeAll();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Pubnub getPubnub() {
        return mPubnub;
    }

    private void notifyUser(String channelName, ChatMessage message) {
        Profile profile = Profile.getCurrentProfile();
        if (profile == null || TextUtils.equals(message.getUserId(), profile.getId())) {
            return;
        }
        Cursor cursor = mAppContext.getContentResolver()
                .query(VersusContract.Conversations.buildConversationUri(message.getRoomName()),
                Projection.TOPIC_FOR_CONVERSATION, null, null, null);

        String topic = "New message";
        if (cursor != null && cursor.moveToFirst()) {
            String sideA = CursorUtils.getString(cursor, VersusContract.Topics.SIDE_A, "");
            String sideB = CursorUtils.getString(cursor, VersusContract.Topics.SIDE_B, "");

            if (!TextUtils.isEmpty(sideA) && !TextUtils.isEmpty(sideB)) {
                topic = mAppContext.getString(R.string.notification_vs, sideA, sideB);
            }
        }

        if (!VersusApplication.isVisible()) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_stat_icon)
                            .setContentTitle(topic)
                            .setContentText(message.getMessage())
                            .setColor(ResourcesCompat.getColor(mAppContext.getResources(), R.color.colorPrimary, null))
                            .setGroup(Constants.Notification.MESSAGE_GROUP)
                            .setAutoCancel(true);

            Intent mainIntent = MainActivity.createIntent(mAppContext);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent convoIntent = ConversationActivity.createIntent(mAppContext, message.getRoomName());
            final PendingIntent pendingIntent = PendingIntent.getActivities(mAppContext, 0,
                    new Intent[] {mainIntent, convoIntent}, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(pendingIntent);

            mNotificationManager.notify(message.getRoomName(), Constants.Notification.MESSAGE_ID, builder.build());
        } else {
            Bus.MESSAGE_BUS.post(new MessageEvent(topic, message));
        }
    }

    private void notifyMatch(QueueResult result) {
        Profile profile = Profile.getCurrentProfile();
        if (profile == null
                || (!TextUtils.equals(result.getUserAId(), profile.getId())
                    && !TextUtils.equals(result.getUserBId(), profile.getId()))) {
            return;
        }
        Cursor cursor = mAppContext.getContentResolver()
                .query(VersusContract.Conversations.buildConversationUri(result.getRoomName()),
                    Projection.TOPIC_FOR_CONVERSATION, null, null, null);

        String topic = "Match Found!";
        if (cursor != null && cursor.moveToFirst()) {
            String sideA = CursorUtils.getString(cursor, VersusContract.Topics.SIDE_A, "");
            String sideB = CursorUtils.getString(cursor, VersusContract.Topics.SIDE_B, "");

            if (!TextUtils.isEmpty(sideA) && !TextUtils.isEmpty(sideB)) {
                topic = mAppContext.getString(R.string.notification_vs, sideA, sideB);
            }
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_icon)
                        .setContentTitle(topic)
                        .setContentText("You've been matched!")
                        .setColor(ResourcesCompat.getColor(mAppContext.getResources(), R.color.colorPrimary, null))
                        .setAutoCancel(true);

        Intent mainIntent = MainActivity.createIntent(mAppContext);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent convoIntent = ConversationActivity.createIntent(mAppContext, result.getRoomName());
        final PendingIntent pendingIntent = PendingIntent.getActivities(mAppContext, 0,
                new Intent[] {mainIntent, convoIntent}, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);

        mNotificationManager.notify(Constants.Notification.MATCH_ID, builder.build());

        Bus.USER_BUS.post(new MatchEvent(result));
    }

    public boolean isSubscribed(String channelName) {
        List<String> channels = Arrays.asList(mPubnub.getSubscribedChannelsArray());
        return channels.contains(channelName);
    }

    public void subscribeUserChannel() {
        Profile profile = Profile.getCurrentProfile();
        if (profile == null) {
            return;
        }
        mUserChannel = "f" + profile.getId();

        if (!isSubscribed(mUserChannel)) {
            try {
                mPubnub.subscribe(mUserChannel, new Callback() {
                    @Override
                    public void connectCallback(String channel, Object message) {
                    }

                    @Override
                    public void disconnectCallback(String channel, Object message) {
                    }

                    public void reconnectCallback(String channel, Object message) {
                    }

                    @Override
                    public void successCallback(String channel, Object message) {
                        final QueueResult queueResult = new Gson().fromJson(message.toString(), QueueResult.class);
                        mConversationsService.acknowledgeMatch(queueResult);
                        subscribeChatChannel(queueResult.getRoomName());
                        notifyMatch(queueResult);
                    }

                    @Override
                    public void errorCallback(String channel, PubnubError error) {
                    }
                });
            } catch (PubnubException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public void subscribeChatChannel(String channelName) {
        if (!isSubscribed(channelName)) {
            try {
                mPubnub.subscribe(channelName, new Callback() {
                    @Override
                    public void connectCallback(String channel, Object message) {
                    }

                    @Override
                    public void disconnectCallback(String channel, Object message) {
                    }

                    public void reconnectCallback(String channel, Object message) {
                    }

                    @Override
                    public void successCallback(String channel, Object message) {
                        final ChatMessage newChatMessage = new Gson().fromJson(message.toString(), ChatMessage.class);
                        notifyUser(channel, newChatMessage);
                    }

                    @Override
                    public void errorCallback(String channel, PubnubError error) {
                    }
                });
            }catch(PubnubException e){
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public void subscribeChatChannels(List<Conversation> conversations) {
        for (Conversation conversation : conversations) {
            subscribeChatChannel(conversation.getRoomName());
        }
    }

    public void unsubscribe(String channelName) {
        mPubnub.unsubscribe(channelName);
    }
}
