package co.ifwe.versus.recycler.adapter;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import co.ifwe.versus.recycler.RecyclerUtils;
import co.ifwe.versus.recycler.layout.GridSpanSizeLookup;

public class RecyclerViewAdapter extends RecyclerView.Adapter implements GridSpanSizeLookup {

    private final View mView;
    private final int mViewType;
    private int mSpanSize = 1;

    public RecyclerViewAdapter(@NonNull View view, @IdRes int viewType) {
        mView = view;
        mViewType = viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) { }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return mViewType;
    }

    public boolean has(View view) {
        return mView == view;
    }

    @Override
    public int getSpanSize(int position) {
        return mSpanSize;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mSpanSize = RecyclerUtils.getSpanSize(recyclerView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static boolean hasView(RecyclerView.Adapter adapter, View view) {
        return (adapter instanceof RecyclerViewAdapter) && ((RecyclerViewAdapter) adapter).has(view);
    }

}
