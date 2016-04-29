package co.ifwe.versus.viewholders;

import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.ifwe.versus.R;
import co.ifwe.versus.adapters.ListItemModel;
import co.ifwe.versus.recycler.adapter.RecyclerCursorAdapter;
import co.ifwe.versus.recycler.viewholder.CursorViewHolder;

public class ModelViewHolder<T extends ListItemModel> extends CursorViewHolder {
    @Bind(R.id.item_text_view)
    protected TextView mTextView;

    public ModelViewHolder(RecyclerCursorAdapter adapter, View itemView) {
        super(adapter, itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(T item) {
        mTextView.setText(item.getListItemText());
    }
}
