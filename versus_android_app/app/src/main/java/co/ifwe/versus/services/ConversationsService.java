package co.ifwe.versus.services;

import com.tagged.caspr.ICasprService;
import com.tagged.caspr.callback.Callback;

import java.util.List;

import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.models.QueueResult;
import co.ifwe.versus.models.Status;

public interface ConversationsService extends ICasprService {
    void getConversationList(List<Status> statuses, boolean pending, Callback<List<Conversation>> callback);

    void addToQueue(int topicId, String side, Callback<QueueResult> callback);

    void acknowledgeMatch(QueueResult result);

    void getConversationForArbitration(Callback<Conversation> callback);

    void submitScore(Conversation conversation, int scoreA, int scoreB, Callback<Void> callback);
}
