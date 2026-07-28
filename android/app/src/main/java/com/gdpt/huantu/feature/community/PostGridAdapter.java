package com.gdpt.huantu.feature.community;
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
import com.gdpt.huantu.core.model.Post;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostGridAdapter extends RecyclerView.Adapter<PostGridAdapter.ViewHolder> {

    private final Context context;
    private List<Post> data;
    private final OnPostClickListener listener;

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    public PostGridAdapter(Context context, List<Post> data, OnPostClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    public void updateData(List<Post> newData) {
        this.data = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_post_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = data.get(position);

        // 标题/内容
        holder.tvTitle.setText(post.getTitle() != null && !post.getTitle().isEmpty()
                ? post.getTitle() : post.getContentPreview());

        // 作者昵称
        holder.tvAuthor.setText(post.getAuthorNickname() != null
                ? post.getAuthorNickname() : "用户");

        // 点赞数
        holder.tvLikeCount.setText(String.valueOf(post.getLikeCount()));

        // 地主认证徽章
        holder.tvLocalBadge.setVisibility(post.isLocalAuth() ? View.VISIBLE : View.GONE);

        // 城市标签
        if (post.getCity() != null && !post.getCity().isEmpty()) {
            holder.tvCityTag.setText(post.getCity());
            holder.tvCityTag.setVisibility(View.VISIBLE);
        } else {
            holder.tvCityTag.setVisibility(View.GONE);
        }

        // 封面图 - 小红书风格必须有大图
        String imageUrl = post.getFirstImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context).load(imageUrl).centerCrop().into(holder.ivCover);
            holder.ivCover.setVisibility(View.VISIBLE);
        } else {
            holder.ivCover.setImageResource(R.drawable.bg_footprint_map_placeholder);
            holder.ivCover.setVisibility(View.VISIBLE);
        }

        // 用户头像
        String avatarUrl = post.getAuthorAvatar();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(context).load(avatarUrl).centerCrop().into(holder.ivAvatar);
        }

        holder.itemView.setOnClickListener(v -> listener.onPostClick(post));
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        CircleImageView ivAvatar;
        TextView tvTitle, tvAuthor, tvLikeCount, tvLocalBadge, tvCityTag;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            tvLocalBadge = itemView.findViewById(R.id.tv_local_badge);
            tvCityTag = itemView.findViewById(R.id.tv_city_tag);
        }
    }
}
