package co.ifwe.versus.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import co.ifwe.versus.R;
import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.recycler.adapter.DataSetChangeStrategyMyers;
import co.ifwe.versus.recycler.adapter.RecyclerCursorAdapter;
import co.ifwe.versus.viewholders.ConversationViewHolder;

public class ConversationAdapter extends RecyclerCursorAdapter<ConversationViewHolder> {

    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public ConversationAdapter(Context context) {
        super();
        mContext = context;
        setDataSetChangeStrategy(new DataSetChangeStrategyMyers(this));
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_conversation, parent, false);

        return new ConversationViewHolder(this, view);
    }

    @Override
    public void onBindViewHolderCursor(ConversationViewHolder holder, Cursor cursor) {
        Conversation conversation = Conversation.fromCursor(cursor);
        holder.bind(conversation);
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(conversation);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Conversation conversation);
    }
}