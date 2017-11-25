package com.amoeba99.novelreader.adapter;

        import android.content.Context;
        import android.support.constraint.ConstraintLayout;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.amoeba99.novelreader.R;
        import com.amoeba99.novelreader.model.Volume;
        import com.bumptech.glide.Glide;

        import java.util.List;

        import butterknife.BindView;
        import butterknife.ButterKnife;

/**
 * Created by Amoeba on 11/23/2017.
 */

public class NovelPageAdapter extends RecyclerView.Adapter<NovelPageAdapter.SearchResultHolder> {

    private List<Volume> data;
    private Context context;
    private String imgUrl;
    private OnItemClicked onClick;

    public NovelPageAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Volume> data) {
        this.data = data;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_novel_page, null, false);
        return new SearchResultHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchResultHolder holder, final int position) {
        holder.title.setText(this.data.get(position).getTitle());
        Glide.with(context).load(imgUrl).into(holder.img);
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
        public ImageView img;
        @BindView(R.id.title)
        public TextView title;
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

    public void setOnClick(NovelPageAdapter.OnItemClicked onClick) {
        this.onClick = onClick;
    }
}
