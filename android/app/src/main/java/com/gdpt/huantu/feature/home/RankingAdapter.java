package com.gdpt.huantu.feature.home;
import com.gdpt.huantu.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gdpt.huantu.core.model.Scenic;

import java.util.ArrayList;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private final Context context;
    private List<Scenic> data;

    public RankingAdapter(Context context, List<Scenic> data) {
        this.context = context;
        this.data = data;
    }

    public void updateData(List<Scenic> newData) {
        this.data = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_ranking_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Scenic scenic = data.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvName.setText(scenic.getName());
        holder.tvRating.setText(String.format("★ %.1f", scenic.getRating()));

        String imageUrl = scenic.getFirstImage();
        if (imageUrl != null) {
            Glide.with(context).load(imageUrl).centerCrop().into(holder.ivCover);
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvRank, tvName, tvRating;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvName = itemView.findViewById(R.id.tv_name);
            tvRating = itemView.findViewById(R.id.tv_rating);
        }
    }
}
