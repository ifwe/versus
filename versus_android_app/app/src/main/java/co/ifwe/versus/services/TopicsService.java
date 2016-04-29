package co.ifwe.versus.services;

import com.tagged.caspr.ICasprService;
import com.tagged.caspr.callback.Callback;

import java.util.List;

import co.ifwe.versus.models.Category;
import co.ifwe.versus.models.Topic;

public interface TopicsService extends ICasprService {
    void getCategories(Callback<List<Category>> callback);

    void getTopicForNewConversation(Category category, Callback<Topic> callback);

    void getAllTopics(Callback<List<Topic>> callback);
}
