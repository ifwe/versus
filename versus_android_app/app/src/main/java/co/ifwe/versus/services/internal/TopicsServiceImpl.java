package co.ifwe.versus.services.internal;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.tagged.caspr.callback.Callback;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import co.ifwe.versus.api.CategoriesApi;
import co.ifwe.versus.models.Category;
import co.ifwe.versus.models.Topic;
import co.ifwe.versus.provider.VersusContract;
import co.ifwe.versus.services.TopicsService;
import co.ifwe.versus.services.VersusService;
import co.ifwe.versus.utils.ContentOperationsBuilder;
import retrofit2.Call;
import retrofit2.Response;

public class TopicsServiceImpl extends VersusService implements TopicsService {
    @Inject
    CategoriesApi mCategoriesApi;

    public TopicsServiceImpl() {
        super(TopicsServiceImpl.class.getCanonicalName());
    }

    @Override
    public void getCategories(Callback<List<Category>> callback) {
        Call<List<Category>> call = mCategoriesApi.getCategories();
        try {
            Response<List<Category>> response = call.execute();
            if (response.code() == 200) {
                ContentOperationsBuilder builder = new ContentOperationsBuilder(getContentResolver());
                builder.bulkInsert(VersusContract.Categories.CONTENT_URI, response.body());
                builder.apply();
                callback.onSuccess(response.body());
            } else {
                callback.onError(response.code());
            }
        } catch (IOException e) {
            callback.onError(ServiceResult.ERROR_UNKNOWN);
        }
    }

    @Override
    public void getTopicForNewConversation(Category category, Callback<Topic> callback) {
        Profile profile = Profile.getCurrentProfile();
        AccessToken token = AccessToken.getCurrentAccessToken();
        Call<Topic> call = mCategoriesApi.getTopicForNewConversation(profile.getId(), token.getToken(), category.getId());
        try {
            Response<Topic> response = call.execute();
            if (response.code() == 200) {
                ContentOperationsBuilder builder = new ContentOperationsBuilder(getContentResolver());
                builder.insert(VersusContract.Topics.CONTENT_URI, response.body());
                builder.apply();
                callback.onSuccess(response.body());
            } else {
                callback.onError(response.code());
            }
        } catch (IOException e) {
            callback.onError(ServiceResult.ERROR_UNKNOWN);
        }
    }

    @Override
    public void getAllTopics(Callback<List<Topic>> callback) {
        Call<List<Topic>> call = mCategoriesApi.getAllTopics();
        try {
            Response<List<Topic>> response = call.execute();
            if (response.code() == 200) {
                ContentOperationsBuilder builder = new ContentOperationsBuilder(getContentResolver());
                builder.bulkInsert(VersusContract.Topics.CONTENT_URI, response.body());
                builder.apply();
                callback.onSuccess(response.body());
            } else {
                callback.onError(response.code());
            }
        } catch (IOException e) {
            callback.onError(ServiceResult.ERROR_UNKNOWN);
        }
    }
}
