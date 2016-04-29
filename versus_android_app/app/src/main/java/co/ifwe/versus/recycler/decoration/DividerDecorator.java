package co.ifwe.versus.recycler.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DividerDecorator extends ItemDecoration {

    private final Drawable mDivider;

    public DividerDecorator(Context context, @DrawableRes int drawableResId) {
        mDivider = ContextCompat.getDrawable(context, drawableResId);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int dividerHeight = mDivider.getIntrinsicHeight();
        final int itemCount = parent.getAdapter().getItemCount();

        int top;
        int bottom;
        int itemPosition;
        RecyclerView.LayoutParams params;
        View child;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            child = parent.getChildAt(i);
            itemPosition = parent.getChildAdapterPosition(child);

            if (itemPosition == RecyclerView.NO_POSITION) {
                continue;
            }

            // Do not draw divider after last item
            if (itemPosition < itemCount - 1) {
                params = (RecyclerView.LayoutParams) child.getLayoutParams();
                bottom = child.getBottom() + params.bottomMargin;
                top = bottom - dividerHeight;

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

}
