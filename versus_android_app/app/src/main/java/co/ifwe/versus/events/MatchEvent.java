package co.ifwe.versus.events;

import co.ifwe.versus.models.QueueResult;

public class MatchEvent {
    private QueueResult mQueueResult;

    public MatchEvent(QueueResult queueResult) {
        mQueueResult = queueResult;
    }

    public QueueResult getQueueResult() {
        return mQueueResult;
    }
}
