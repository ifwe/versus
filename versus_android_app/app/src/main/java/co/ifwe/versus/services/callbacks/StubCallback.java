package co.ifwe.versus.services.callbacks;

import com.tagged.caspr.callback.CompleteCallback;

public class StubCallback<T> implements CompleteCallback<T> {
    public static final StubCallback<Void> EMPTY = new StubCallback<>();

    @Override
    public void onComplete() {
    }

    @Override
    public void onSuccess(T t) {
    }

    @Override
    public void onError(int errorCode) {
    }
}

