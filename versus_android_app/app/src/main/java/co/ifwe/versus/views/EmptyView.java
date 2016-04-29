package co.ifwe.versus.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.ifwe.versus.R;

/**
 * Created by schoi on 4/25/16.
 */
public class EmptyView extends RelativeLayout {

    @Bind(R.id.empty_image_view)
    ImageView mImageView;

    @Bind(R.id.empty_text_view)
    TextView mTextView;

    @Bind(R.id.empty_button)
    Button mButton;

    @Bind(R.id.empty_fab_text_view)
    TextView mFabTextView;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = inflate(context, R.layout.view_empty, this);
        ButterKnife.bind(view);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EmptyView, 0, 0);

        try {
            int messageTextResId = a.getResourceId(R.styleable.EmptyView_messageText, 0);
            int iconResId = a.getResourceId(R.styleable.EmptyView_iconSrc, 0);
            int buttonTextResId = a.getResourceId(R.styleable.EmptyView_buttonText, 0);
            int fabTextResId = a.getResourceId(R.styleable.EmptyView_fabText, 0);

            int iconVisibility = a.getInt(R.styleable.EmptyView_iconVisiblity, View.VISIBLE);
            int buttonVisibility = a.getInt(R.styleable.EmptyView_buttonVisiblity, View.VISIBLE);
            int fabTextVisibility = a.getInt(R.styleable.EmptyView_fabVisiblity, View.GONE);

            if (messageTextResId != 0) {
                setMessage(messageTextResId);
            }

            if (iconResId != 0) {
                setIcon(iconResId);
            }

            if (buttonTextResId != 0) {
                setButton(buttonTextResId);
            }

            if (fabTextResId != 0) {
                setFabPromptText(fabTextResId);
            }

            setIconVisiblity(iconVisibility);
            setButtonVisibility(buttonVisibility);
            setFabTextVisibility(fabTextVisibility);
        } finally {
            a.recycle();
        }
    }

    public void setMessage(@StringRes int textResId) {
        mTextView.setText(textResId);
    }

    public void setMessage(String text) {
        mTextView.setText(text);
    }

    public void setIcon(@DrawableRes int imageResId) {
        mImageView.setImageResource(imageResId);
    }

    public void setIcon(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
    }

    public void setButton(@StringRes int textResId) {
        mButton.setText(textResId);
    }

    public void setButton(String text) {
        mButton.setText(text);
    }

    public void setButtonClickListener(OnClickListener listener) {
        mButton.setOnClickListener(listener);
    }

    public void setFabPromptText(@StringRes int textResId) {
        mFabTextView.setText(textResId);
    }

    public void setFabPromptText(String text) {
        mFabTextView.setText(text);
    }

    public void setIconVisiblity(int visibility) {
        mImageView.setVisibility(visibility);
    }

    public void setButtonVisibility(int visibility) {
        mButton.setVisibility(visibility);
    }

    public void setFabTextVisibility(int visibility) {
        mFabTextView.setVisibility(visibility);
    }
}
