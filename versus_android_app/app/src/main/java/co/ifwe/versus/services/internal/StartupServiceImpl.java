package co.ifwe.versus.services.internal;

import javax.inject.Inject;

import co.ifwe.versus.models.Status;
import co.ifwe.versus.services.ConversationsService;
import co.ifwe.versus.services.StartupService;
import co.ifwe.versus.services.TopicsService;
import co.ifwe.versus.services.VersusService;
import co.ifwe.versus.services.callbacks.StubCallback;

public class StartupServiceImpl extends VersusService implements StartupService{

    @Inject
    TopicsService mTopicsService;

    public StartupServiceImpl() {
        super(StartupServiceImpl.class.getCanonicalName());
    }

    @Override
    public void refreshData() {
        mTopicsService.getAllTopics(new StubCallback<>());
        mTopicsService.getCategories(new StubCallback<>());
    }
}
