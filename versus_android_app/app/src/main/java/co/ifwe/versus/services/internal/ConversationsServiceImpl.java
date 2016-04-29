package co.ifwe.versus.services.internal;

import android.content.ContentValues;
import android.text.TextUtils;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.tagged.caspr.callback.Callback;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import co.ifwe.versus.api.ConversationsApi;
import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.models.QueueBody;
import co.ifwe.versus.models.QueueResult;
import co.ifwe.versus.models.Status;
import co.ifwe.versus.models.UpdateScoreRequest;
import co.ifwe.versus.provider.VersusContract;
import co.ifwe.versus.services.SubscribeService;
import co.ifwe.versus.services.ConversationsService;
import co.ifwe.versus.services.VersusService;
import co.ifwe.versus.utils.ContentOperationsBuilder;
import retrofit2.Call;
import retrofit2.Response;

public class ConversationsServiceImpl extends VersusService implements ConversationsService {

    private static final String TAG = ConversationsServiceImpl.class.getCanonicalName();

    @Inject
    ConversationsApi mConversationsApi;

    @Inject
    SubscribeService mSubscribeService;

    public ConversationsServiceImpl() {
        super(ConversationsServiceImpl.class.getCanonicalName());
    }

    @Override
    public void getConversationList(Status[] statuses, boolean pending, Callback<List<Conversation>> callback) {
        Profile profile = Profile.getCurrentProfile();
        AccessToken token = AccessToken.getCurrentAccessToken();
        List<Status> states = Arrays.asList(statuses);
        Call<List<Conversation>> request = mConversationsApi.getConversations(profile.getId(),
                token.getToken(), states, pending);
        try {
            Response<List<Conversation>> response = request.execute();
            if (response.code() == 200) {
                Status[] statusFilter = statuses;
                if (pending) {
                    statusFilter = Arrays.copyOf(statuses, statuses.length + 1);
                    statusFilter[statusFilter.length - 1] = Status.PENDING;
                }

                ContentOperationsBuilder builder = new ContentOperationsBuilder(getContentResolver());
                builder.bulkInsert(VersusContract.Conversations.CONTENT_URI, response.body());
                builder.delete(VersusContract.Conversations.CONTENT_URI,
                        VersusContract.Conversations.buildNotInSelection(response.body(), statusFilter), null);
                builder.apply();
                mSubscribeService.subscribeChatChannels(response.body());
                callback.onSuccess(response.body());
            } else {
                callback.onError(response.code());
            }
        } catch (IOException e) {
            callback.onError(ServiceResult.ERROR_UNKNOWN);
        }
    }

    @Override
    public void addToQueue(int topicId, String side, Callback<QueueResult> callback) {
        Profile profile = Profile.getCurrentProfile();
        AccessToken token = AccessToken.getCurrentAccessToken();
        QueueBody body = new QueueBody(profile.getId(), topicId, side, token.getToken());
        Call<QueueResult> request = mConversationsApi.addToQueue(body);

        try {
            Response<QueueResult> response = request.execute();
            if (response.code() == 200) {
                QueueResult result = response.body();
                ContentOperationsBuilder builder = new ContentOperationsBuilder(getContentResolver());
                builder.insert(VersusContract.Conversations.CONTENT_URI, result);
                getContentResolver().notifyChange(VersusContract.Conversations.CONTENT_URI, null);
                builder.apply();

                if (result.isMatched()) {
                    mSubscribeService.subscribeChatChannel(result.getRoomName());
                }
                callback.onSuccess(result);
            } else {
                callback.onError(response.code());
            }
        } catch (IOException e) {
            callback.onError(ServiceResult.ERROR_UNKNOWN);
        }
    }

    @Override
    public void acknowledgeMatch(QueueResult result) {
        Profile profile = Profile.getCurrentProfile();

        boolean isUserA = TextUtils.equals(profile.getId(), result.getUserAId());
        if (!isUserA && !TextUtils.equals(profile.getId(), result.getUserBId())) {
            throw new IllegalArgumentException("User id does not match user_a or user_b");
        }

        ContentOperationsBuilder builder = new ContentOperationsBuilder(getContentResolver());
        ContentValues contentValues = new ContentValues();
        contentValues.put(VersusContract.Conversations.USER_A, result.getUserAId());
        contentValues.put(VersusContract.Conversations.USER_B, result.getUserBId());
        contentValues.put(VersusContract.Conversations.STATUS, Status.ACTIVE.getCode());
        contentValues.put(VersusContract.Conversations.END_TIME, result.getEndTime());
        builder.update(VersusContract.Conversations.buildConversationUri(result.getRoomName()),
                contentValues, null, null);
        builder.apply();
        getContentResolver().notifyChange(VersusContract.Conversations.CONTENT_URI, null);
    }

    @Override
    public void getConversationForArbitration(Callback<Conversation> callback) {
        Profile profile = Profile.getCurrentProfile();
        AccessToken token = AccessToken.getCurrentAccessToken();

        Call<Conversation> request = mConversationsApi.getForJudge(profile.getId(), token.getToken());
        try {
            Response<Conversation> response = request.execute();
            if (response.code() == 200) {
                callback.onSuccess(response.body());
            } else {
                callback.onError(response.code());
            }
        } catch (IOException e) {
            callback.onError(ServiceResult.ERROR_UNKNOWN);
        }
    }

    @Override
    public void submitScore(Conversation conversation, int scoreA, int scoreB, Callback<Void> callback) {
        Profile profile = Profile.getCurrentProfile();
        AccessToken token = AccessToken.getCurrentAccessToken();

        UpdateScoreRequest body = new UpdateScoreRequest(profile.getId(), token.getToken(),
                conversation.getRoomName(), scoreA, scoreB);
        Call<Void> request = mConversationsApi.submitScore(body);
        try {
            Response<Void> response = request.execute();
            if (response.code() == 200) {
                callback.onSuccess(null);
            } else {
                callback.onError(response.code());
            }
        } catch (IOException e) {
            callback.onError(ServiceResult.ERROR_UNKNOWN);
        }
    }
}
