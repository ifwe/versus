package co.ifwe.versus.api;

import java.util.List;

import co.ifwe.versus.models.Category;
import co.ifwe.versus.models.Topic;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CategoriesApi {
    @GET("categories")
    Call<List<Category>> getCategories();

    @GET("topics/getForNewConversation")
    Call<Topic> getTopicForNewConversation(
            @Query("fbid") String facebookId,
            @Query("token") String token,
            @Query("category")int categoryId);

    @GET("topics/all")
    Call<List<Topic>> getAllTopics();
}
