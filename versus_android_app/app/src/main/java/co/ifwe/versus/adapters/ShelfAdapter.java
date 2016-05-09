package co.ifwe.versus.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import co.ifwe.versus.R;
import co.ifwe.versus.utils.ViewUtils;
import co.ifwe.versus.views.ShelfItem;

public class ShelfAdapter extends BaseAdapter {
    private Context mContext;
    private List<ShelfItem> mShelfItems;

    public ShelfAdapter(Context context, List<ShelfItem> items) {
        super();
        mContext = context;
        mShelfItems = items;
    }

    @Override
    public int getCount() {
        return mShelfItems.size();
    }

    @Override
    public ShelfItem getItem(int position) {
        return mShelfItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_drawer_shelf, parent, false);
        ShelfItem item = getItem(position);
        TextView menuTextView = ViewUtils.findView(rowView, R.id.menuTextView);
        menuTextView.setText(item.getStringResId());
        Drawable drawable = ResourcesCompat.getDrawable(mContext.getResources(), item.getDrawableResId(), null);
        drawable.setColorFilter(ResourcesCompat.getColor(mContext.getResources(), R.color.menuIconColor, null), PorterDuff.Mode.SRC_IN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            menuTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
        } else {
            menuTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
        return rowView;
    }
}
