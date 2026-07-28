package com.gdpt.huantu.feature.trip;
import com.gdpt.huantu.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gdpt.huantu.core.model.DayPlan;
import com.gdpt.huantu.core.model.ScenicItem;

import java.util.List;

/**
 * 路线时间轴适配器（每天一个Section + 景点列表）
 */
public class DayTimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_DAY_HEADER = 0;
    private static final int TYPE_SCENIC = 1;

    private final List<DayTimelineItem> items;

    public DayTimelineAdapter(List<DayPlan> dayPlans) {
        this.items = new java.util.ArrayList<>();
        for (DayPlan dp : dayPlans) {
            items.add(new DayTimelineItem(true, dp.getDayNumber(), null));
            if (dp.getScenics() != null) {
                for (ScenicItem si : dp.getScenics()) {
                    items.add(new DayTimelineItem(false, dp.getDayNumber(), si));
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isHeader ? TYPE_DAY_HEADER : TYPE_SCENIC;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_DAY_HEADER) {
            View view = inflater.inflate(R.layout.item_day_header, parent, false);
            return new DayHeaderHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_timeline_scenic, parent, false);
            return new ScenicHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DayTimelineItem item = items.get(position);
        if (holder instanceof DayHeaderHolder) {
            ((DayHeaderHolder) holder).tvDayTitle.setText("第" + item.dayNumber + "天");
        } else if (holder instanceof ScenicHolder && item.scenic != null) {
            ScenicItem s = item.scenic;
            ScenicHolder sh = (ScenicHolder) holder;
            sh.tvName.setText(s.getName());
            sh.tvDuration.setText(s.getStayDurationText());
            sh.tvTransport.setText(s.getTransportTip() != null ? "🚗 " + s.getTransportTip() : "");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class DayTimelineItem {
        boolean isHeader;
        int dayNumber;
        ScenicItem scenic;

        DayTimelineItem(boolean isHeader, int dayNumber, ScenicItem scenic) {
            this.isHeader = isHeader;
            this.dayNumber = dayNumber;
            this.scenic = scenic;
        }
    }

    static class DayHeaderHolder extends RecyclerView.ViewHolder {
        TextView tvDayTitle;

        DayHeaderHolder(@NonNull View itemView) {
            super(itemView);
            tvDayTitle = itemView.findViewById(R.id.tv_day_title);
        }
    }

    static class ScenicHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDuration, tvTransport;

        ScenicHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_scenic_name);
            tvDuration = itemView.findViewById(R.id.tv_stay_duration);
            tvTransport = itemView.findViewById(R.id.tv_transport);
        }
    }
}
