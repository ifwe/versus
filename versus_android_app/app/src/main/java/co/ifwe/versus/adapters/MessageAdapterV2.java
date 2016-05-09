package co.ifwe.versus.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import co.ifwe.versus.R;
import co.ifwe.versus.models.ChatMessage;
import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.viewholders.MessageViewHolder;
import co.ifwe.versus.viewholders.MessageViewType;

public class MessageAdapterV2 extends RecyclerView.Adapter<MessageViewHolder> {

    private List<ChatMessage> mChatMessageList;
    private Conversation mConversation;
    private String mUserId;
    private Context mContext;

    public MessageAdapterV2(Context context, Conversation conversation, String userId) {
        super();
        mContext = context;
        mConversation = conversation;
        mUserId = userId;
        mChatMessageList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mChatMessageList.size();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(viewType > 0 ?
                        R.layout.item_outgoing_message : R.layout.item_incoming_message,
                parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.bind(getItem(position), getItemViewType(position));
    }

    public void add(int position, List<ChatMessage> chatMessages) {
        mChatMessageList.addAll(position, chatMessages);
        notifyItemRangeInserted(position, chatMessages.size());
    }

    public void add(ChatMessage chatMessage) {
        mChatMessageList.add(chatMessage);
        notifyItemInserted(getItemCount());
    }

    public ChatMessage getItem(int position) {
        return mChatMessageList.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChatMessageList.get(position).getUserId(), mUserId)) {
            //check if previous message was also from the same person
            if (position > 0 && TextUtils.equals(mChatMessageList.get(position - 1).getUserId(), mUserId)) {
                return MessageViewType.SELF.getCode();
            }
            return MessageViewType.SELF_START.getCode();
        } else {
            //check if previous message was also from the same person
            if (position > 0 && !TextUtils.equals(mChatMessageList.get(position - 1).getUserId(), mUserId)) {
                return MessageViewType.OPPONENT.getCode();
            }
            return MessageViewType.OPPONENT_START.getCode();
        }
    }
}
