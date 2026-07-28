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

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.ViewHolder> {

    private final Context context;
    private List<Scenic> data;
    private final OnScenicClickListener listener;

    public interface OnScenicClickListener {
        void onScenicClick(Scenic scenic);
    }

    public NearbyAdapter(Context context, List<Scenic> data, OnScenicClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    public void updateData(List<Scenic> newData) {
        this.data = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_scenic_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Scenic scenic = data.get(position);
        holder.tvName.setText(scenic.getName());
        holder.tvRating.setText(String.format("%.1f", scenic.getRating()));
        holder.tvAddress.setText(scenic.getAddress());
        holder.tvPrice.setText(scenic.getPriceInfo() != null ? scenic.getPriceInfo() : "");

        String imageUrl = scenic.getFirstImage();
        if (imageUrl != null) {
            Glide.with(context).load(imageUrl).centerCrop().into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.bg_footprint_map_placeholder);
        }

        holder.itemView.setOnClickListener(v -> listener.onScenicClick(scenic));
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvName, tvRating, tvAddress, tvPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvName = itemView.findViewById(R.id.tv_name);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
