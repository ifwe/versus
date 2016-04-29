package co.ifwe.versus.viewholders;

import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.ifwe.versus.R;
import co.ifwe.versus.adapters.MessageAdapterV2;
import co.ifwe.versus.models.ChatMessage;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.message_text)
    protected TextView mTextView;

    private ChatMessage mChatMessage;

    public MessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(ChatMessage item, int viewType) {
        mChatMessage = item;
        @DrawableRes int drawableRes;
        @ColorRes int textColorRes;

        if (viewType < 0) {
            textColorRes = R.color.black;
            if (viewType == MessageViewType.OPPONENT_START.getCode()) {
                drawableRes = R.drawable.item_message_incoming;
            } else {
                drawableRes = R.drawable.item_message_incoming_follow;
            }
        } else if (viewType > 0){
            textColorRes = R.color.white;
            if (viewType == MessageViewType.SELF_START.getCode()) {
                drawableRes = R.drawable.item_message_outgoing;
            } else {
                drawableRes = R.drawable.item_message_outgoing_follow;
            }
        } else {
            return;
        }
        mTextView.setTextColor(ResourcesCompat.getColor(itemView.getResources(), textColorRes, null));
        setDrawableWithPadding(mTextView, drawableRes);
        mTextView.setText(mChatMessage.getMessage());
    }

    private void setDrawableWithPadding(View view, @DrawableRes int drawableResId) {
        final int paddingTop = view.getPaddingTop();
        final int paddingBottom = view.getPaddingBottom();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            final int paddingStart = view.getPaddingStart();
            final int paddingEnd = view.getPaddingEnd();
            view.setBackgroundResource(drawableResId);
            view.setPaddingRelative(paddingStart, paddingTop, paddingEnd, paddingBottom);
        } else {
            final int paddingLeft = view.getPaddingLeft();
            final int paddingRight = view.getPaddingRight();
            view.setBackgroundResource(drawableResId);
            view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
    }
}
