package co.ifwe.versus.provider;

public interface
Projection extends Qualified {

    String[] CATEGORY = {
            VersusContract.Categories.ID,
            VersusContract.Categories.NAME,
    };

    String[] TOPIC = {
            VersusContract.Topics.ID,
            VersusContract.Topics.SIDE_A,
            VersusContract.Topics.SIDE_B,
            VersusContract.Topics.SIDE_A_URL,
            VersusContract.Topics.SIDE_B_URL,
            VersusContract.Topics.CATEGORY_ID,
    };

    String[] CONVERSATION = {
            VersusContract.Conversations.ROOM_NAME,
            CONVERSATION_TOPIC_ID,
            VersusContract.Topics.SIDE_A,
            VersusContract.Topics.SIDE_B,
            VersusContract.Topics.SIDE_A_FR,
            VersusContract.Topics.SIDE_B_FR,
            VersusContract.Topics.SIDE_A_URL,
            VersusContract.Topics.SIDE_B_URL,
            VersusContract.Topics.CATEGORY_ID,
            VersusContract.Conversations.USER_A,
            VersusContract.Conversations.USER_B,
            VersusContract.Conversations.SCORE_A,
            VersusContract.Conversations.SCORE_B,
            VersusContract.Conversations.STATUS,
            VersusContract.Conversations.RESULT,
            VersusContract.Conversations.END_TIME,
    };

    String[] RESULTS = {
            VersusContract.Conversations.RESULT,
            "COUNT(*)",
    };

    String[] TOPIC_FOR_CONVERSATION = {
        VersusContract.Topics.SIDE_A,
        VersusContract.Topics.SIDE_B,
    };

    String[] MESSAGE = {
            VersusContract.Messages.ROOM_NAME,
            VersusContract.Messages.USER_ID,
            VersusContract.Messages.TIMESTAMP,
            VersusContract.Messages.MESSAGE,
    };
}
