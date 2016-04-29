package co.ifwe.versus.recycler.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridItemDecoration extends ItemDecoration {

    private final int mInsidePadding;
    private final int mOutsidePadding;
    private final Rect drawablePaddingRect;

    public GridItemDecoration(Context context, @DimenRes int outsidePadding, @DimenRes int insidePadding) {
        mOutsidePadding = context.getResources().getDimensionPixelSize(outsidePadding);
        mInsidePadding = context.getResources().getDimensionPixelSize(insidePadding);
        drawablePaddingRect = new Rect();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager glm = (GridLayoutManager) parent.getLayoutManager();
        GridLayoutManager.SpanSizeLookup spanSizeLookup = glm.getSpanSizeLookup();

        int position = parent.getChildAdapterPosition(view);
        int spanCount = glm.getSpanCount();
        int itemSpanIndex = spanSizeLookup.getSpanIndex(position, spanCount);
        int itemSpanGroup = spanSizeLookup.getSpanGroupIndex(position, spanCount);
        int itemSpanSize = spanSizeLookup.getSpanSize(position);

        if (itemSpanGroup == 0) {
            // most top row
            outRect.top = mOutsidePadding;
        } else {
            // middle rows
            outRect.top = mInsidePadding;
        }
        outRect.bottom = mInsidePadding;

        if (itemSpanSize == spanCount) {
            // item takes whole row
            outRect.left = mOutsidePadding;
            outRect.right = mOutsidePadding;
        } else {
            if (itemSpanIndex == 0) {
                // most left item
                outRect.left = mOutsidePadding;
                outRect.right = mInsidePadding;
            } else if (itemSpanIndex + itemSpanSize == spanCount) {
                // most right item
                outRect.left = mInsidePadding;
                outRect.right = mOutsidePadding;
            } else {
                // middle item
                outRect.left = mInsidePadding;
                outRect.right = mInsidePadding;
            }
        }

        Drawable drawable = view.getBackground();
        if (drawable != null && drawable.getPadding(drawablePaddingRect)) {
            outRect.left -= drawablePaddingRect.left;
            outRect.top -= drawablePaddingRect.top;
            outRect.right -= drawablePaddingRect.right;
            outRect.bottom -= drawablePaddingRect.bottom;
        }
    }
}
