package co.ifwe.versus.provider;

import android.net.Uri;

import java.util.List;

import co.ifwe.versus.BuildConfig;
import co.ifwe.versus.models.Category;
import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.models.Result;
import co.ifwe.versus.models.Status;
import co.ifwe.versus.models.Topic;

public final class VersusContract {
    private VersusContract() {
    }

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String QUERY_LIMIT = "query_limit";

    /**
     * Projection parameter for operations that should not call notifyChange() on the uri
     */
    public static final String QUERY_SILENT = "query_silent";

    public static class Categories implements Table.Categories.Columns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.versus.categories";
        public static final String CONTENT_PATH = "categories";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.versus.categories";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH).build();

        public static Uri buildCategoryUri(Category category) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(category.getId())).build();
        }

        public static Uri buildCategoryUri(int categoryId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(categoryId)).build();
        }
    }

    public static class Topics implements Table.Topics.Columns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.versus.topics";
        public static final String CONTENT_PATH = "topics";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.versus.topics";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH).build();

        public static Uri buildTopicUri(Topic topic) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(topic.getTopicId())).build();
        }

        public static Uri buildTopicUri(int topicId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(topicId)).build();
        }

        public static String buildTopicForCategoryUri(int categoryId) {
            StringBuilder builder = new StringBuilder(Table.Categories.Columns.ID + " = ");
            builder.append(categoryId);
            return builder.toString();
        }
    }

    public static class Conversations implements Table.Conversations.Columns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.versus.conversations";
        public static final String CONTENT_PATH = "conversations";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.versus.conversations";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH).build();

        public static Uri buildConversationUri(Conversation conversation) {
            return CONTENT_URI.buildUpon().appendPath(conversation.getRoomName()).build();
        }

        public static Uri buildResultsUri() {
            return CONTENT_URI.buildUpon().appendPath(RESULT).build();
        }

        public static Uri buildConversationUri(String roomName) {
            return CONTENT_URI.buildUpon().appendPath(roomName).build();
        }

        public static String buildConversationSelection() {
            StringBuilder selection = new StringBuilder();
            selection.append(Qualified.CONVERSATION_ROOM_NAME).append("=?");
            return selection.toString();
        }

        public static String buildStatusSelection(Status... statuses) {
            StringBuilder selection = new StringBuilder();
            selection.append(STATUS).append(" IN (");
            for (int i = 0; i < statuses.length; i ++) {
                selection.append("\"");
                selection.append(statuses[i].getCode());
                selection.append("\"");
                if (i < statuses.length - 1) {
                    selection.append(", ");
                }
            }
            selection.append(")");
            return selection.toString();
        }

        public static String buildResultsSelection(Result... results) {
            StringBuilder selection = new StringBuilder();
            selection.append(RESULT).append(" IN (");
            for (int i = 0; i < results.length; i ++) {
                selection.append("\"");
                selection.append(results[i].getCode());
                selection.append("\"");
                if (i < results.length - 1) {
                    selection.append(", ");
                }
            }
            selection.append(")");
            return selection.toString();
        }

        public static String buildTopicSelection(boolean isUserA) {
            StringBuilder selection = new StringBuilder();
            selection.append(STATUS).append("=").append("\"").append(Status.PENDING.getCode()).append("\"");

            selection.append(" AND ");
            selection.append(Qualified.CONVERSATION_TOPIC_ID).append("=?");

            selection.append(" AND ");
            if (isUserA) {
                selection.append(USER_A);
            } else {
                selection.append(USER_B);
            }
            selection.append("=?");
            return selection.toString();
        }

        public static String buildNotInSelection(List<Conversation> conversations, List<Status> statuses) {
            StringBuilder selection = new StringBuilder();
            selection.append(STATUS).append(" IN (");
            for (int i = 0; i < statuses.size(); i ++) {
                selection.append("\"").append(statuses.get(i).getCode()).append("\"");
                if (i < statuses.size() - 1) {
                    selection.append(", ");
                }
            }
            selection.append(")");
            selection.append(" AND ");
            selection.append(ROOM_NAME).append(" NOT IN (");
            for (int i = 0; i < conversations.size(); i ++) {
                selection.append("\"").append(conversations.get(i).getRoomName()).append("\"");
                if (i < conversations.size() - 1) {
                    selection.append(", ");
                }
            }
            selection.append(")");
            return selection.toString();
        }
    }

    public static class Messages implements Table.Messages.Columns {
        public static final String CONTENT_PATH = "messages";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.versus.messages";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH).build();

        public static Uri buildConversationUri(String roomName) {
            return CONTENT_URI.buildUpon().appendPath(roomName).build();
        }

        public static String buildConversationSelection() {
            StringBuilder selection = new StringBuilder();
            selection.append(ROOM_NAME).append("=?");
            return selection.toString();
        }

        public static String getDefaultSortOrder() {
            StringBuilder sort = new StringBuilder();
            sort.append(TIMESTAMP).append(" ASC");
            return sort.toString();
        }
    }
}
