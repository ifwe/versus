package co.ifwe.versus.models;

import java.util.List;

/**
 * Created by schoi on 4/23/16.
 */
public class MessageHistory {
    private long mStartTime;
    private long mEndTime;
    private List<ChatMessage> mChatMessages;

    public MessageHistory(long startTime, long endTime, List<ChatMessage> chatMessages) {
        mStartTime = startTime;
        mEndTime = endTime;
        mChatMessages = chatMessages;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public List<ChatMessage> getChatMessages() {
        return mChatMessages;
    }
}
