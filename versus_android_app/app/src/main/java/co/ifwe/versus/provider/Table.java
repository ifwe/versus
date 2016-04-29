package co.ifwe.versus.provider;

import co.ifwe.versus.models.Conversation;

public interface Table {

    interface Categories {
        String Name = "categories";
        String CreateSql =
                "CREATE TABLE " + Name + "(" +
                        Columns.ID + " integer unique not null, " +
                        Columns.NAME + " string not null, " +
                        "PRIMARY KEY(" + Columns.ID + ")" +
                        ")";

        interface Columns {
            String ID = "_id";
            String NAME = "name";
        }
    }

    interface Topics {
        String Name = "topics";
        String CreateSql =
                "CREATE TABLE " + Name + "(" +
                        Columns.ID + " integer unique not null, " +
                        Columns.CATEGORY_ID + " integer not null, " +
                        Columns.SIDE_A + " string not null, " +
                        Columns.SIDE_B + " string not null, " +
                        Columns.SIDE_A_FR + " string not null, " +
                        Columns.SIDE_B_FR + " string not null, " +
                        Columns.SIDE_A_URL + " string, " +
                        Columns.SIDE_B_URL + " string, " +
                        "PRIMARY KEY(" + Columns.ID + ") " +
                        "FOREIGN KEY(" + Columns.CATEGORY_ID + ") REFERENCES " +
                        Categories.Name + "(" + Categories.Columns.ID + ")" +
                        ")";

        interface Columns {
            String ID = "topic_id";
            String CATEGORY_ID = "category_id";
            String SIDE_A = "side_a";
            String SIDE_B = "side_b";
            String SIDE_A_FR = "side_a_fr";
            String SIDE_B_FR = "side_b_fr";
            String SIDE_A_URL = "side_a_url";
            String SIDE_B_URL = "side_b_url";
        }
    }

    interface Conversations {
        String Name = "conversations";
        String CreateSql =
                "CREATE TABLE " + Name + "(" +
                        Columns.ROOM_NAME + " string unique not null, " +
                        Columns.TOPIC_ID + " integer not null, " +
                        Columns.STATUS + " string, " +
                        Columns.USER_A + " string, " +
                        Columns.USER_B + " string, " +
                        Columns.RESULT + " string default none, " +
                        Columns.SCORE_A + " integer, " +
                        Columns.SCORE_B + " integer, " +
                        Columns.END_TIME + " datetime, " +
                        "PRIMARY KEY(" + Columns.ROOM_NAME + ") " +
                        "FOREIGN KEY(" + Columns.TOPIC_ID + ") REFERENCES " +
                        Topics.Name + "(" + Topics.Columns.ID + ")" +
                        ")";

        interface Columns {
            String ROOM_NAME = "_id";
            String TOPIC_ID = "topic_id";
            String STATUS = "status";
            String USER_A = "user_a";
            String USER_B = "user_b";
            String RESULT = "result";
            String SCORE_A = "score_a";
            String SCORE_B = "score_b";
            String END_TIME = "end_time";
        }
    }

    interface Messages {
        String Name = "messages";
        String CreateSql =
                "CREATE TABLE " + Name + "(" +
                        Columns.ROOM_NAME + " string not null, " +
                        Columns.USER_ID + " string not null, " +
                        Columns.TIMESTAMP + " datetime not null, " +
                        Columns.MESSAGE + " string, " +
                        "PRIMARY KEY(" + Columns.ROOM_NAME + ", " + Columns.USER_ID + ", " + Columns.TIMESTAMP + ") " +
                        "FOREIGN KEY(" + Columns.ROOM_NAME + ") REFERENCES " +
                        Conversations.Name + "(" + Conversations.Columns.ROOM_NAME + ")" +
                        ")";

        interface Columns {
            String ROOM_NAME = "_id";
            String USER_ID = "user_id";
            String TIMESTAMP = "timestamp";
            String MESSAGE = "message";
        }
    }
}
