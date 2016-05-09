package co.ifwe.versus.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import co.ifwe.versus.models.Category;
import co.ifwe.versus.recycler.adapter.RecyclerCursorAdapter;
import co.ifwe.versus.utils.ViewUtils;
import co.ifwe.versus.viewholders.CategoryViewHolder;

public class CategoryAdapter extends RecyclerCursorAdapter<CategoryViewHolder> {

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
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = ViewUtils.inflate(parent.getContext(), mItemLayoutRes, parent, false);
        return new CategoryViewHolder(this, view);
    }

    @Override
    public void onBindViewHolderCursor(CategoryViewHolder holder, Cursor cursor) {
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