package com.gdpt.huantu.feature.trip;
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
import com.gdpt.huantu.core.model.Route;

import java.util.ArrayList;
import java.util.List;

public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.ViewHolder> {

    private final Context context;
    private List<Route> data;
    private final OnRouteClickListener listener;

    public interface OnRouteClickListener {
        void onRouteClick(Route route);
    }

    public TripListAdapter(Context context, List<Route> data, OnRouteClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    public void updateData(List<Route> newData) {
        this.data = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_route_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Route route = data.get(position);
        holder.tvTitle.setText(route.getTitle() != null ? route.getTitle() : route.getDestination());
        holder.tvDestination.setText(route.getDestination());
        holder.tvInfo.setText(route.getDays() + "天 | " +
                route.getScenicCount() + "个景点 | " +
                route.getDurationText());
        holder.tvStatus.setText(route.getStatusText());

        if (route.getCoverUrl() != null && !route.getCoverUrl().isEmpty()) {
            Glide.with(context).load(route.getCoverUrl()).centerCrop().into(holder.ivCover);
        }

        holder.itemView.setOnClickListener(v -> listener.onRouteClick(route));
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvDestination, tvInfo, tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDestination = itemView.findViewById(R.id.tv_destination);
            tvInfo = itemView.findViewById(R.id.tv_info);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}
