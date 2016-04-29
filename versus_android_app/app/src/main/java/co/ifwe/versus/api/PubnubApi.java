package co.ifwe.versus.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by schoi on 4/23/16.
 */
public interface PubnubApi {

    @Headers({
            "V: 3.1",
            "User-Agent: Java-Android",
            "Accept: */*"
    })
    @GET("publish/pub-c-8028947a-380c-420e-be14-cc9216642edd/sub-c-78d1252e-e658-11e5-a4f2-0619f8945a4f/0/{channel}/0/{message}")
    Call<Void> publish(@Path("channel")String channel,
                       @Path("message")String message);

    @Headers({
            "V: 3.1",
            "User-Agent: Java-Android",
            "Accept: */*"
    })
    @GET("history/sub-c-78d1252e-e658-11e5-a4f2-0619f8945a4f/{channel}/0/{limit}")
    Call<Object> history(@Path("user")String channel,
                       @Path("limit")int limit);
    /**
     * http://pubsub.pubnub.com
     /history
     /sub-key
     /channel
     /callback
     /limit
     */
}
