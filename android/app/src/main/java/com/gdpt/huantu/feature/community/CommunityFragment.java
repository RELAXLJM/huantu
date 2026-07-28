package com.gdpt.huantu.feature.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.gdpt.huantu.core.model.Post;
import com.gdpt.huantu.core.network.TokenManager;
import com.gdpt.huantu.databinding.FragmentCommunityBinding;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CommunityFragment extends Fragment {

    private FragmentCommunityBinding binding;

    @Inject
    TokenManager tokenManager;

    private CommunityViewModel viewModel;
    private PostGridAdapter adapter;
    private int currentPage = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CommunityViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCommunityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        observeData();
        loadData();
    }

    private void initViews() {
        // 城市筛选 - 默认显示当前城市
        // 简化为显示文本，后续可扩展城市选择器

        // 瀑布流帖子列表
        adapter = new PostGridAdapter(requireContext(), new ArrayList<>(), post -> {
            Intent intent = new Intent(requireContext(), PostDetailActivity.class);
            intent.putExtra("postId", post.getId());
            startActivity(intent);
        });
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL);
        binding.rvPosts.setLayoutManager(layoutManager);
        binding.rvPosts.setAdapter(adapter);

        // 上拉加载更多
        binding.rvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] lastPositions = layoutManager.findLastVisibleItemPositions(null);
                int lastVisibleItem = Math.max(lastPositions[0], lastPositions[1]);
                int totalItemCount = layoutManager.getItemCount();
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    loadMore();
                }
            }
        });

        // FAB - 发布帖子
        binding.fabPublish.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), PublishPostActivity.class));
        });

        // 下拉刷新
        binding.swipeRefresh.setOnRefreshListener(() -> {
            currentPage = 1;
            loadData();
        });
    }

    private void observeData() {
        viewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            adapter.updateData(posts);
            if (posts.isEmpty()) {
                binding.layoutEmpty.setVisibility(View.VISIBLE);
                binding.rvPosts.setVisibility(View.GONE);
            } else {
                binding.layoutEmpty.setVisibility(View.GONE);
                binding.rvPosts.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.swipeRefresh.setRefreshing(isLoading != null && isLoading);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        // 不限定城市，展示全国帖子
        viewModel.loadPosts(null, currentPage, 10);
    }

    private void loadMore() {
        currentPage++;
        viewModel.loadMorePosts(null, currentPage, 10);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 从帖子详情/发布页返回时刷新
        currentPage = 1;
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
