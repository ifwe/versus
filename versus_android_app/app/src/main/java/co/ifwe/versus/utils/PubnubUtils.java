package co.ifwe.versus.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.tagged.caspr.callback.Callback;

import java.util.ArrayList;

import co.ifwe.versus.models.ChatMessage;
import co.ifwe.versus.models.MessageHistory;
import co.ifwe.versus.provider.VersusContract;
import co.ifwe.versus.services.VersusService;
import co.ifwe.versus.services.callbacks.PubnubCallback;

/**
 * Created by schoi on 4/23/16.
 */
public class PubnubUtils {

    private static final String TAG = PubnubUtils.class.getCanonicalName();

    public static void publish(Pubnub pubnub, String channel, ChatMessage chatMessage,
                               Context context, Callback<ChatMessage> callback){
        pubnub.publish(channel, chatMessage.toJson(), new PubnubCallback() {
            @Override
            public void successCallback(String channel, Object message) {
                super.successCallback(channel, message);
                ContentOperationsBuilder builder = new ContentOperationsBuilder(context.getContentResolver());
                builder.insert(VersusContract.Messages.CONTENT_URI, chatMessage);
                builder.apply();
                callback.onSuccess(chatMessage);
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                super.errorCallback(channel, error);
                callback.onError(VersusService.ServiceResult.ERROR_UNKNOWN);
            }
        });
    }

    public static void history(Pubnub pubnub, String channel, long start, boolean ordered, int limit,
                               Context context, Callback<MessageHistory> callback) {
        PubnubCallback pubnubCallback = new PubnubCallback() {
            @Override
            public void successCallback(String channel, Object message) {
                super.successCallback(channel, message);
                MessageHistory history = parseHistory(message);
                ContentOperationsBuilder builder = new ContentOperationsBuilder(context.getContentResolver());
                builder.bulkInsert(VersusContract.Messages.CONTENT_URI, history.getChatMessages());
                builder.apply();
                callback.onSuccess(history);
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                super.errorCallback(channel, error);
                callback.onError(VersusService.ServiceResult.ERROR_UNKNOWN);
            }
        };

        if (start > 0) {
            pubnub.history(channel, start, limit, ordered, pubnubCallback);
        } else {
            pubnub.history(channel, limit, ordered, pubnubCallback);
        }
    }

    private static MessageHistory parseHistory(Object message) {
        JsonParser parser = new JsonParser();
        JsonArray root = parser.parse(message.toString()).getAsJsonArray();

        JsonArray array = root.get(0).getAsJsonArray();

        final ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < array.size(); i++) {
            try {
                JsonElement element = parser.parse(array.get(i).getAsString());
                chatMessages.add(gson.fromJson(element, ChatMessage.class));
                Log.i(TAG, "Message parsed: " + array.get(i));
            } catch (JsonSyntaxException e) {
                Log.e(TAG, "Invalid message: " + array.get(i));
            }
        }
        long startTime = root.get(1).getAsLong();
        long endTime = root.get(2).getAsLong();
        return new MessageHistory(startTime, endTime, chatMessages);
    }
}
