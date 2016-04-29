package co.ifwe.versus.recycler.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Decoration that adds padding to each item from top, left, right and bottom
 */
public class ItemPaddingDecoration extends RecyclerView.ItemDecoration {

    private final int mLeftPadding;
    private final int mTopPadding;
    private final int mRightPadding;
    private final int mBottomPadding;

    public ItemPaddingDecoration(int left, int top, int right, int bottom) {
        mLeftPadding = left;
        mTopPadding = top;
        mRightPadding = right;
        mBottomPadding = bottom;
    }

    public ItemPaddingDecoration(int itemPadding) {
        this(itemPadding, itemPadding, itemPadding, itemPadding);
    }

    public ItemPaddingDecoration(Context context, @DimenRes int itemPadding) {
        this(context.getResources().getDimensionPixelSize(itemPadding));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(mLeftPadding, mTopPadding, mRightPadding, mBottomPadding);
    }
}
