package co.ifwe.versus.provider;

public interface Qualified {
    String TOPIC_TOPIC_ID = Table.Topics.Name + "." + VersusContract.Topics.ID;
    String CONVERSATION_TOPIC_ID = Table.Conversations.Name + "." + VersusContract.Conversations.TOPIC_ID;

    String CONVERSATION_ROOM_NAME = Table.Conversations.Name + "." + VersusContract.Conversations.ROOM_NAME;
}
