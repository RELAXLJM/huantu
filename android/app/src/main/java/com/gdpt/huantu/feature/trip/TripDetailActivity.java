package com.gdpt.huantu.feature.trip;
import com.gdpt.huantu.R;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gdpt.huantu.core.model.Route;
import com.gdpt.huantu.core.network.ApiResponse;
import com.gdpt.huantu.core.network.ApiService;
import com.gdpt.huantu.databinding.ActivityTripDetailBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class TripDetailActivity extends AppCompatActivity {

    private ActivityTripDetailBinding binding;

    @Inject
    ApiService apiService;

    private long routeId;
    private Route currentRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTripDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        routeId = getIntent().getLongExtra("routeId", 0);

        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> finish());

        binding.btnSave.setOnClickListener(v -> saveRoute());
        binding.btnDelete.setOnClickListener(v -> deleteRoute());

        loadRouteDetail();
    }

    private void loadRouteDetail() {
        apiService.getRouteDetail(routeId).enqueue(new Callback<ApiResponse<Route>>() {
            @Override
            public void onResponse(Call<ApiResponse<Route>> call,
                                   Response<ApiResponse<Route>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    currentRoute = response.body().getData();
                    displayRoute(currentRoute);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Route>> call, Throwable t) {
                Toast.makeText(TripDetailActivity.this,
                        "加载失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayRoute(Route route) {
        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setTitle(route.getDestination());
        binding.tvRouteTitle.setText(route.getTitle() != null ? route.getTitle() : route.getDestination());
        binding.tvRouteInfo.setText(route.getDays() + "天 | " +
                route.getScenicCount() + "个景点 | " + route.getDurationText());
        binding.tvRouteStatus.setText(route.getStatusText());

        // 时间轴
        if (route.getDayPlans() != null && !route.getDayPlans().isEmpty()) {
            DayTimelineAdapter adapter = new DayTimelineAdapter(route.getDayPlans());
            binding.rvTimeline.setLayoutManager(new LinearLayoutManager(this));
            binding.rvTimeline.setAdapter(adapter);
        }
    }

    private void saveRoute() {
        if (currentRoute == null) return;
        apiService.updateRoute(routeId, 1, null).enqueue(new Callback<ApiResponse<Route>>() {
            @Override
            public void onResponse(Call<ApiResponse<Route>> call,
                                   Response<ApiResponse<Route>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    Toast.makeText(TripDetailActivity.this, "已保存", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Route>> call, Throwable t) {
            }
        });
    }

    private void deleteRoute() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除这条路线吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    apiService.deleteRoute(routeId).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call,
                                               Response<ApiResponse<Void>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(TripDetailActivity.this,
                                        "已删除", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        }
                    });
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
