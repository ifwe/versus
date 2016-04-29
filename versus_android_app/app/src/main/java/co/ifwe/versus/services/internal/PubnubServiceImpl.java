package co.ifwe.versus.services.internal;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.tagged.caspr.callback.Callback;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import co.ifwe.versus.api.PubnubApi;
import co.ifwe.versus.models.ChatMessage;
import co.ifwe.versus.models.MessageHistory;
import co.ifwe.versus.services.PubnubService;
import co.ifwe.versus.services.VersusService;
import co.ifwe.versus.services.callbacks.PubnubCallback;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by schoi on 4/23/16.
 */
public class PubnubServiceImpl extends VersusService implements PubnubService {

    private static final String TAG = PubnubServiceImpl.class.getCanonicalName();

    @Inject
    Pubnub mPubnub;

    @Inject
    PubnubApi mPubnubApi;

    public PubnubServiceImpl() {
        super(PubnubServiceImpl.class.getCanonicalName());
    }


    @Override
    public void publish(String channel, ChatMessage chatMessage, Callback<ChatMessage> callback) {
        Call<Void> call = mPubnubApi.publish(channel, chatMessage.toString());
        try {
            Response<Void> response = call.execute();
            if (response.isSuccess()) {
                callback.onSuccess(chatMessage);
            } else {
                callback.onError(response.code());
            }
        } catch (IOException e) {
            callback.onError(ServiceResult.ERROR_UNKNOWN);
        }

//        mPubnub.publish(channel, chatMessage.toJson(), new PubnubCallback() {
//            @Override
//            public void successCallback(String channel, Object message) {
//                super.successCallback(channel, message);
//                callback.onSuccess(chatMessage);
//            }
//
//            @Override
//            public void errorCallback(String channel, PubnubError error) {
//                super.errorCallback(channel, error);
//                callback.onError(ServiceResult.ERROR_UNKNOWN);
//            }
//        });
    }

    @Override
    public void history(String channel, long start, boolean ordered, int limit, Callback<MessageHistory> callback) {
        PubnubCallback pubnubCallback = new PubnubCallback() {
            @Override
            public void successCallback(String channel, Object message) {
                super.successCallback(channel, message);
                MessageHistory history = parseHistory(message);
                callback.onSuccess(history);
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                super.errorCallback(channel, error);
                callback.onError(ServiceResult.ERROR_UNKNOWN);
            }
        };

        if (start > 0) {
            mPubnub.history(channel, start, limit, ordered, pubnubCallback);
        } else {
            mPubnub.history(channel, limit, ordered, pubnubCallback);
        }
    }

    @Override
    public void subscribe(String channel) {

    }

    private MessageHistory parseHistory(Object message) {
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
