package co.ifwe.versus.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import co.ifwe.versus.R;
import co.ifwe.versus.models.ArbitrationMessage;
import co.ifwe.versus.models.ChatMessage;
import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.viewholders.ArbitrationViewHolder;

public class ArbitrationAdapter extends RecyclerView.Adapter<ArbitrationViewHolder> {

    private List<ArbitrationMessage> mChatMessageList;
    private Conversation mConversation;
    private Context mContext;

    @NonNull
    private ArbitrationViewHolder.LikeClickListener mListener;

    public ArbitrationAdapter(Context context, Conversation conversation,
                              ArbitrationViewHolder.LikeClickListener listener) {
        super();
        mContext = context;
        mConversation = conversation;
        mChatMessageList = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mChatMessageList.size();
    }

    @Override
    public ArbitrationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_judge, parent, false);
        return new ArbitrationViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ArbitrationViewHolder holder, int position) {
        holder.bind(getItem(position), getItemViewType(position));
    }

    public void addHistory(List<ChatMessage> chatMessages) {
        int start = mChatMessageList.size();
        mChatMessageList.addAll(ArbitrationMessage.convert(chatMessages));
        notifyItemRangeInserted(start, chatMessages.size());
    }

    public void add(ChatMessage chatMessage) {
        mChatMessageList.add(new ArbitrationMessage(chatMessage));
        notifyItemInserted(getItemCount());
    }

    public ArbitrationMessage getItem(int position) {
        return mChatMessageList.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChatMessageList.get(position).getUserId(), mConversation.getUserAId())) {
            return ArbitrationViewHolder.ViewType.USER_A.getCode();
        } else {
            return ArbitrationViewHolder.ViewType.USER_B.getCode();
        }
    }
}
