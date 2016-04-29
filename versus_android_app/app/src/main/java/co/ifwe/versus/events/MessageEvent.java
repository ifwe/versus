package co.ifwe.versus.events;

import co.ifwe.versus.models.ChatMessage;

public class MessageEvent {
    private String mTopic;
    private ChatMessage mChatMessage;

    public MessageEvent(String topic, ChatMessage chatMessage) {
        mTopic = topic;
        mChatMessage = chatMessage;
    }

    public String getTopic() {
        return mTopic;
    }

    public String getRoomName() {
        return mChatMessage.getRoomName();
    }

    public String getUserId() {
        return mChatMessage.getUserId();
    }

    public ChatMessage getChatMessage() {
        return mChatMessage;
    }
}
