package co.ifwe.versus.viewholders;

import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.ifwe.versus.R;
import co.ifwe.versus.models.ArbitrationMessage;

public class ArbitrationViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.message_text_view)
    protected TextView mTextView;

    @Bind(R.id.like_button)
    protected ImageView mLikeButton;

    @NonNull
    private LikeClickListener mLikeClickListener;

    private ArbitrationMessage mChatMessage;

    public ArbitrationViewHolder(View itemView, LikeClickListener likeClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mLikeClickListener = likeClickListener;
    }

    public void bind(ArbitrationMessage item, int viewType) {
        mChatMessage = item;
        if (viewType == ViewType.USER_A.getCode()) {
            setDrawableWithPadding(mTextView, R.drawable.item_judge_a);
            mTextView.setTextColor(ResourcesCompat.getColor(itemView.getResources(), R.color.white, null));
        } else if (viewType == ViewType.USER_B.getCode()){
            setDrawableWithPadding(mTextView, R.drawable.item_judge_b);
            mTextView.setTextColor(ResourcesCompat.getColor(itemView.getResources(), R.color.black, null));
        }
        mTextView.setText(mChatMessage.getMessage());
        boolean liked = mChatMessage.isLiked();
        mLikeButton.setImageResource(liked ? R.drawable.ic_liked : R.drawable.ic_like);
        int color = ResourcesCompat.getColor(itemView.getResources(), liked ? R.color.sea_blue : R.color.gunmetal, null);
        mLikeButton.setColorFilter(color, PorterDuff.Mode.SRC_IN);
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

    @OnClick(R.id.like_button)
    void onLikeClicked() {
        boolean liked = mChatMessage.toggleLiked();
        mLikeButton.setImageResource(liked ? R.drawable.ic_liked : R.drawable.ic_like);
        int color = ResourcesCompat.getColor(itemView.getResources(), liked ? R.color.sea_blue : R.color.gunmetal, null);
        mLikeButton.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        mLikeClickListener.onLikeClicked(mChatMessage.getUserId(), liked);
    }

    public interface LikeClickListener {
        void onLikeClicked(String userId, boolean liked);
    }

    public enum ViewType {
        USER_A (1),
        USER_B (-1),
        FOOTER (0);

        private int mCode;

        ViewType(int code) {
            mCode = code;
        }

        public int getCode() {
            return mCode;
        }
    }
}
