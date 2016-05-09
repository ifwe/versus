package co.ifwe.versus.viewholders;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.Profile;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.ifwe.versus.R;
import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.models.Status;
import co.ifwe.versus.recycler.adapter.RecyclerCursorAdapter;
import co.ifwe.versus.recycler.viewholder.CursorViewHolder;

public class ConversationViewHolder extends CursorViewHolder {
    @Bind(R.id.topic_a_text_view)
    TextView mTopicATextView;

    @Bind(R.id.topic_b_text_view)
    TextView mTopicBTextView;

    @Bind(R.id.time_text_view)
    TextView mTimeTextView;

    @Bind(R.id.subtitle_text_view)
    TextView mSubtitleTextView;

    public ConversationViewHolder(RecyclerCursorAdapter adapter, View itemView) {
        super(adapter, itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Conversation conversation) {
        Profile profile = Profile.getCurrentProfile();
        mTopicATextView.setText(conversation.getTopic().getSideA());
        mTopicBTextView.setText(conversation.getTopic().getSideB());

        Context context = itemView.getContext();
        Resources resources = itemView.getResources();
        String subtitle = "";
        if (!TextUtils.isEmpty(conversation.getUserAId())
                && TextUtils.equals(profile.getId(), conversation.getUserAId())) {
            mTopicATextView.setTextColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null));
            mTopicBTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.gunmetal, null));
        } else if (!TextUtils.isEmpty(conversation.getUserBId())
                && TextUtils.equals(profile.getId(), conversation.getUserBId())) {
            mTopicATextView.setTextColor(ResourcesCompat.getColor(resources, R.color.gunmetal, null));
            mTopicBTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null));
        }

        if (!conversation.isMatched()) {
            subtitle = context.getString(R.string.inbox_pending);
            mSubtitleTextView.setVisibility(View.VISIBLE);
        } else {
            mSubtitleTextView.setVisibility(View.INVISIBLE);
        }

        if (conversation.getStatus() == Status.REVIEW) {
            itemView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparent, null));
            mTimeTextView.setText("Pending");
            mTimeTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.gunmetal, null));
        } else if (conversation.getStatus() == Status.DONE) {
            int backgroundColor;
            int textColor;
            String resultText;
            switch (conversation.getResult()) {
                case WIN:
                    backgroundColor = ResourcesCompat.getColor(resources, R.color.sea_blue_20, null);
                    textColor = ResourcesCompat.getColor(resources, R.color.sea_blue, null);
                    resultText = context.getString(R.string.conversation_win);
                    break;
                case LOSS:
                    backgroundColor = ResourcesCompat.getColor(resources, R.color.salmon_20, null);
                    textColor = ResourcesCompat.getColor(resources, R.color.salmon, null);
                    resultText = context.getString(R.string.conversation_loss);
                    break;
                case DRAW:
                    backgroundColor = ResourcesCompat.getColor(resources, R.color.slate_20, null);
                    textColor = ResourcesCompat.getColor(resources, R.color.gunmetal, null);
                    resultText = context.getString(R.string.conversation_draw);
                    break;
                case NONE:
                default:
                    backgroundColor = ResourcesCompat.getColor(resources, R.color.transparent, null);
                    textColor = ResourcesCompat.getColor(resources, R.color.gunmetal, null);
                    resultText = context.getString(R.string.conversation_pending);
                    break;
            }
            itemView.setBackgroundColor(backgroundColor);
            mTimeTextView.setText(resultText);
            mTimeTextView.setTextColor(textColor);
        }

        mSubtitleTextView.setText(subtitle);
    }
}
