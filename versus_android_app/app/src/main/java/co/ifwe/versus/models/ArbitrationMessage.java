package co.ifwe.versus.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by schoi on 4/23/16.
 */
public class ArbitrationMessage {
    private ChatMessage mChatMessage;
    private boolean mLiked;

    public ArbitrationMessage(ChatMessage chatMessage) {
        mChatMessage = chatMessage;
    }

    public ChatMessage getChatMessage() {
        return mChatMessage;
    }

    public String getUserId() {
        return mChatMessage.getUserId();
    }

    public String getMessage() {
        return mChatMessage.getMessage();
    }

    public boolean isLiked() {
        return mLiked;
    }

    public void setLiked(boolean liked) {
        mLiked = liked;
    }

    public boolean toggleLiked() {
        mLiked = !mLiked;
        return mLiked;
    }

    public static List<ArbitrationMessage> convert(List<ChatMessage> messages) {
        List<ArbitrationMessage> arbitrationMessages = new ArrayList<>();
        for (int i = 0; i < messages.size(); i ++) {
            arbitrationMessages.add(new ArbitrationMessage(messages.get(i)));
        }
        return arbitrationMessages;
    }
}
