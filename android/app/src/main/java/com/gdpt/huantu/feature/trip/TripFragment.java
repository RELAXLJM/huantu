package com.gdpt.huantu.feature.trip;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gdpt.huantu.core.model.Route;
import com.gdpt.huantu.core.util.Constants;
import com.gdpt.huantu.databinding.FragmentTripBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TripFragment extends Fragment {

    private FragmentTripBinding binding;
    private TripViewModel viewModel;
    private TripListAdapter adapter;
    private int currentStatus = Constants.ROUTE_STATUS_SAVED; // 默认查看已保存

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TripViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTripBinding.inflate(inflater, container, false);
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
        // 状态切换器
        // 进行中 = status=1(已保存)，已保存 = status=1，已结束 = status=2
        binding.chipActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentStatus = Constants.ROUTE_STATUS_SAVED;
                filterAndShow();
            }
        });
        binding.chipSaved.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentStatus = Constants.ROUTE_STATUS_SAVED;
                filterAndShow();
            }
        });
        binding.chipFinished.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentStatus = Constants.ROUTE_STATUS_FINISHED;
                filterAndShow();
            }
        });

        // 路线列表
        adapter = new TripListAdapter(requireContext(), new ArrayList<>(), route -> {
            Intent intent = new Intent(requireContext(), TripDetailActivity.class);
            intent.putExtra("routeId", route.getId());
            startActivity(intent);
        });
        binding.rvRoutes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRoutes.setAdapter(adapter);

        // FAB - 生成新路线
        binding.fabGenerate.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), RouteGenerateActivity.class));
        });

        // 下拉刷新
        binding.swipeRefresh.setOnRefreshListener(this::loadData);
    }

    private void observeData() {
        viewModel.getRoutes().observe(getViewLifecycleOwner(), routes -> {
            filterAndShow();
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
        viewModel.loadRoutes(null); // 加载所有路线
    }

    private void filterAndShow() {
        List<Route> allRoutes = viewModel.getRoutes().getValue();
        if (allRoutes == null) allRoutes = new ArrayList<>();

        // 当前后台只有 0=草稿, 1=已保存, 2=已结束
        // "进行中"和"已保存"都显示 status=1
        List<Route> filtered;
        if (currentStatus == Constants.ROUTE_STATUS_FINISHED) {
            filtered = allRoutes.stream()
                    .filter(r -> r.getStatus() == Constants.ROUTE_STATUS_FINISHED)
                    .collect(Collectors.toList());
        } else {
            filtered = allRoutes.stream()
                    .filter(r -> r.getStatus() != Constants.ROUTE_STATUS_FINISHED
                            && r.getStatus() != Constants.ROUTE_STATUS_DRAFT)
                    .collect(Collectors.toList());
        }

        adapter.updateData(filtered);

        if (filtered.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.rvRoutes.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.rvRoutes.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
