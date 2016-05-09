package co.ifwe.versus.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.ifwe.versus.R;
import co.ifwe.versus.models.ChatMessage;
import co.ifwe.versus.recycler.adapter.DataSetChangeStrategyMyers;
import co.ifwe.versus.recycler.adapter.RecyclerCursorAdapter;
import co.ifwe.versus.viewholders.MessageViewHolderV2;
import co.ifwe.versus.viewholders.MessageViewType;

public class MessageAdapterV3 extends RecyclerCursorAdapter<MessageViewHolderV2> {

    private String mUserId;
    private Context mContext;

    public MessageAdapterV3(Context context, String userId) {
        super();
        mContext = context;
        mUserId = userId;
        setDataSetChangeStrategy(new DataSetChangeStrategyMyers(this));
    }

    @Override
    public MessageViewHolderV2 onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(viewType > 0 ?
                        R.layout.item_outgoing_message : R.layout.item_incoming_message,
                parent, false);
        return new MessageViewHolderV2(this, view);
    }

    @Override
    public void onBindViewHolderCursor(MessageViewHolderV2 holder, Cursor cursor) {
        ChatMessage message = ChatMessage.fromCursor(cursor);
        holder.bind(message);
    }

//    public void add(int position, List<ChatMessage> chatMessages) {
//        mChatMessageList.addAll(position, chatMessages);
//        notifyItemRangeInserted(position, chatMessages.size());
//    }
//
//    public void add(ChatMessage chatMessage) {
//        mChatMessageList.add(chatMessage);
//        notifyItemInserted(getItemCount());
//    }

    @Override
    public int getItemViewType(int position) {
        if (getCursor() == null) {
            return 0;
        }
        ChatMessage current = ChatMessage.fromCursor(getCursor());
        ChatMessage prev = null;

        if (getCursor().move(position - 1)) {
            prev = ChatMessage.fromCursor(getCursor());
        }

        if (TextUtils.equals(current.getUserId(), mUserId)) {
            //check if previous message was also from the same person
            if (prev != null && TextUtils.equals(prev.getUserId(), mUserId)) {
                return MessageViewType.SELF.getCode();
            }
            return MessageViewType.SELF_START.getCode();
        } else {
            //check if previous message was also from the same person
            if (prev != null && TextUtils.equals(prev.getUserId(), mUserId)) {
                return MessageViewType.OPPONENT.getCode();
            }
            return MessageViewType.OPPONENT_START.getCode();
        }
    }
}
