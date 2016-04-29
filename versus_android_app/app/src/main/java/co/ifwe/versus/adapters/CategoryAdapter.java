package co.ifwe.versus.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import co.ifwe.versus.models.Category;
import co.ifwe.versus.recycler.adapter.RecyclerCursorAdapter;
import co.ifwe.versus.utils.ViewUtils;
import co.ifwe.versus.viewholders.ModelViewHolder;

public class CategoryAdapter extends RecyclerCursorAdapter<ModelViewHolder<Category>> {

    protected Context mContext;
    protected OnItemClickListener mOnItemClickListener;

    @LayoutRes
    protected int mItemLayoutRes;

    public CategoryAdapter(Context context, int itemLayoutRes) {
        super();
        mContext = context;
        mItemLayoutRes = itemLayoutRes;
    }

    @Override
    public ModelViewHolder<Category> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = ViewUtils.inflate(parent.getContext(), mItemLayoutRes, parent, false);
        return new ModelViewHolder<>(this, view);
    }

    @Override
    public void onBindViewHolderCursor(ModelViewHolder<Category> holder, Cursor cursor) {
        Category category = Category.fromCursor(cursor);
        holder.bind(category);
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(category);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }
}