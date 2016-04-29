package co.ifwe.versus.recycler.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class LinearLayoutItemDecoration extends ItemDecoration {
    private final int mInsidePadding;
    private final int mOutsidePadding;

    public LinearLayoutItemDecoration(Context context, @DimenRes int outsidePadding, @DimenRes int insidePadding) {
        mOutsidePadding = context.getResources().getDimensionPixelSize(outsidePadding);
        mInsidePadding = context.getResources().getDimensionPixelSize(insidePadding);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int position = parent.getChildAdapterPosition(view);

        outRect.left = mOutsidePadding;
        outRect.top = mOutsidePadding;
        outRect.right = mOutsidePadding;
        outRect.bottom = mOutsidePadding;

        if (linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
            outRect.top = position == 0 ? mOutsidePadding : mInsidePadding;
            outRect.bottom = position == state.getItemCount() - 1 ? mOutsidePadding : mInsidePadding;
        } else {
            outRect.left = position == 0 ? mOutsidePadding : mInsidePadding;
            outRect.right = position == state.getItemCount() - 1 ? mOutsidePadding : mInsidePadding;
        }
    }
}
