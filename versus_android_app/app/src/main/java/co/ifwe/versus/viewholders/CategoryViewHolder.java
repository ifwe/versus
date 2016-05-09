package co.ifwe.versus.viewholders;

import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.ifwe.versus.R;
import co.ifwe.versus.models.Category;
import co.ifwe.versus.recycler.adapter.RecyclerCursorAdapter;
import co.ifwe.versus.recycler.viewholder.CursorViewHolder;

public class CategoryViewHolder extends CursorViewHolder {
    @Bind(R.id.item_text_view)
    protected TextView mTextView;

    public CategoryViewHolder(RecyclerCursorAdapter adapter, View itemView) {
        super(adapter, itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Category item) {
        mTextView.setText(item.getName());
    }
}
