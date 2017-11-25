package com.amoeba99.novelreader.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amoeba99.novelreader.R;
import com.amoeba99.novelreader.model.Novel;

import com.bumptech.glide.Glide;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Amoeba on 11/22/2017.
 */
public class MainPageAdapter extends RecyclerView.Adapter<MainPageAdapter.SearchResultHolder> {

    private List<Novel> data;
    private Context context;
    private OnItemClicked onClick;

    public MainPageAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Novel> data) {
        this.data = data;
    }

    @Override
    public SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_main_page, null, false);
        return new SearchResultHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchResultHolder holder, final int position) {
        holder.textName.setText(this.data.get(position).getName());
        Glide.with(context).load(this.data.get(position).getImg()).into(holder.imgUrl);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        return 0;
    }

    public static class SearchResultHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img)
        public ImageView imgUrl;

        @BindView(R.id.name)
        public TextView textName;

        @BindView(R.id.layout)
        public ConstraintLayout layout;

        public SearchResultHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClicked{
        void onItemClick(int position);
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }
}
