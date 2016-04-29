package co.ifwe.versus.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

public class LockableLinearLayoutManager extends LinearLayoutManager {

    private boolean mCanScrollVertically = true;

    public LockableLinearLayoutManager(Context context) {
        super(context);
    }

    public LockableLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LockableLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCanScrollVertically(boolean canScrollVertically) {
        mCanScrollVertically = canScrollVertically;
    }

    @Override
    public boolean canScrollVertically() {
        return mCanScrollVertically;
    }
}
