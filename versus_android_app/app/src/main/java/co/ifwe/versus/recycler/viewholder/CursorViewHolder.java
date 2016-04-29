package co.ifwe.versus.recycler.viewholder;

import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import co.ifwe.versus.recycler.adapter.RecyclerCursorAdapter;

public class CursorViewHolder<V extends View> extends RecyclerView.ViewHolder {
    public V itemView;
    protected RecyclerCursorAdapter mCursorAdapter;

    @Nullable
    protected RecyclerView mRecyclerView;

    public CursorViewHolder(RecyclerCursorAdapter adapter, V itemView) {
        super(itemView);
        mCursorAdapter = adapter;
        this.itemView = itemView;
    }

    public void setRecyclerView(@Nullable RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public void bind(Cursor cursor) {

    }
}