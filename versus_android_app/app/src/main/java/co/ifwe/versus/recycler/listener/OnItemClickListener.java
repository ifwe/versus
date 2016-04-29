package co.ifwe.versus.recycler.listener;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnItemClickListener implements RecyclerView.OnItemTouchListener {

    private GestureDetectorCompat mGestureDetector;
    private RecyclerView mRecyclerView;

    public abstract void onItemClickListener(RecyclerView recyclerView, int position, RecyclerView.ViewHolder holder);

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        ensureGestureDetector(rv);
        return mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        ensureGestureDetector(rv);
    }

    private void ensureGestureDetector(RecyclerView recyclerView) {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new OnItemClickListenerInternal());
        }
        mRecyclerView = recyclerView;
    }

    private class OnItemClickListenerInternal extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            int position = mRecyclerView.getChildAdapterPosition(childView);
            if (position != RecyclerView.NO_POSITION) {
                RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(position);
                onItemClickListener(mRecyclerView, position, holder);
                return true;
            } else {
                return false;
            }
        }
    }

}
