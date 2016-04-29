package co.ifwe.versus.services;

import com.tagged.caspr.ICasprService;
import com.tagged.caspr.callback.Callback;

import co.ifwe.versus.models.ChatMessage;
import co.ifwe.versus.models.MessageHistory;

/**
 * Created by schoi on 4/23/16.
 */
public interface PubnubService extends ICasprService {
    void publish(String channel, ChatMessage message, Callback<ChatMessage> callback);

    void history(String channel, long start, boolean ordered, int limit, Callback<MessageHistory> callback);

    void subscribe(String channel);
}
