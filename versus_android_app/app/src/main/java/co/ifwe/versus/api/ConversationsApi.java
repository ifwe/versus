package co.ifwe.versus.api;

import java.util.List;

import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.models.QueueBody;
import co.ifwe.versus.models.QueueResult;
import co.ifwe.versus.models.Status;
import co.ifwe.versus.models.UpdateScoreRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ConversationsApi {
    @GET("conversations/getForUser")
    Call<List<Conversation>> getConversations(@Query("fbid") String facebookId,
                                              @Query("token") String token,
                                              @Query("states[]") List<Status> statuses,
                                              @Query("includePending") boolean includePending);

    @POST("queue/addOrMatch")
    Call<QueueResult> addToQueue(@Body QueueBody body);

    @GET("conversations/getForJudge")
    Call<Conversation> getForJudge(@Query("fbid") String facebookId,
                                   @Query("token") String token);

    @POST("conversations/updateScore")
    Call<Void> submitScore(@Body UpdateScoreRequest body);
}
