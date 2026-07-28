package com.gdpt.huantu.feature.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.gdpt.huantu.core.model.Scenic;
import com.gdpt.huantu.core.model.WeatherInfo;
import com.gdpt.huantu.core.network.TokenManager;
import com.gdpt.huantu.databinding.FragmentHomeBinding;
import com.gdpt.huantu.feature.search.SearchActivity;
import com.gdpt.huantu.feature.trip.RouteGenerateActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Inject
    TokenManager tokenManager;

    private HomeViewModel viewModel;
    private NearbyAdapter nearbyAdapter;
    private RankingAdapter rankingAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean gpsLoaded = false;

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    requestCurrentLocation();
                } else {
                    loadCityBasedData();
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        observeData();
        requestLocationAndLoad();
    }

    private void initViews() {
        String city = tokenManager.getCurrentCity();
        binding.tvCityName.setText(city);
        binding.tvWeatherBrief.setText("");
        binding.tvTripAdvice.setText("");

        binding.layoutSearch.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SearchActivity.class));
        });

        binding.btnAiWand.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), RouteGenerateActivity.class));
        });

        binding.chipScenic.setOnClickListener(v -> searchByType("景点"));
        binding.chipFood.setOnClickListener(v -> searchByType("美食"));
        binding.chipHotel.setOnClickListener(v -> searchByType("住宿"));
        binding.chipTravel.setOnClickListener(v -> searchByType("周边游"));
        binding.chipPhoto.setOnClickListener(v -> searchByType("拍照圣地"));

        nearbyAdapter = new NearbyAdapter(requireContext(), new ArrayList<>(), scenic -> {
            Toast.makeText(requireContext(), scenic.getName(), Toast.LENGTH_SHORT).show();
        });
        binding.rvNearby.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.rvNearby.setAdapter(nearbyAdapter);
        binding.rvNearby.setNestedScrollingEnabled(false);

        rankingAdapter = new RankingAdapter(requireContext(), new ArrayList<>());
        binding.rvRankings.setLayoutManager(
                new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        binding.rvRankings.setAdapter(rankingAdapter);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            gpsLoaded = false;
            requestLocationAndLoad();
        });
    }

    private void observeData() {
        viewModel.getWeather().observe(getViewLifecycleOwner(), weatherInfo -> {
            stopSkeletonShimmer(binding.layoutWeatherSkeleton);
            if (weatherInfo != null) {
                binding.layoutWeatherSkeleton.setVisibility(View.GONE);
                binding.layoutWeatherContent.setVisibility(View.VISIBLE);

                binding.tvWeatherBrief.setText(
                        weatherInfo.getDayWeather() + " " + weatherInfo.getDayTemp() + "°"
                );
                binding.tvTemp.setText(weatherInfo.getDayTemp() + "°");
                binding.tvWeatherDetail.setText(
                        weatherInfo.getDayWeather() + "  |  " +
                                weatherInfo.getDayWind() + " " +
                                weatherInfo.getDayPower()
                );
                binding.tvTripAdvice.setText(generateTripAdvice(weatherInfo));
            }
        });

        viewModel.getNearbyScenics().observe(getViewLifecycleOwner(), scenics -> {
            binding.layoutNearbySkeleton.setVisibility(View.GONE);
            if (scenics != null && !scenics.isEmpty()) {
                binding.rvNearby.setVisibility(View.VISIBLE);
                binding.layoutNearbyEmpty.setVisibility(View.GONE);
                nearbyAdapter.updateData(scenics);
            } else if (scenics != null) {
                binding.rvNearby.setVisibility(View.GONE);
                binding.layoutNearbyEmpty.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getRankings().observe(getViewLifecycleOwner(), scenics -> {
            binding.layoutRankingSkeleton.setVisibility(View.GONE);
            if (scenics != null && !scenics.isEmpty()) {
                binding.rvRankings.setVisibility(View.VISIBLE);
                binding.layoutRankingEmpty.setVisibility(View.GONE);
                rankingAdapter.updateData(scenics);
            } else if (scenics != null) {
                binding.rvRankings.setVisibility(View.GONE);
                binding.layoutRankingEmpty.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.swipeRefresh.setRefreshing(isLoading != null && isLoading);
            if (isLoading != null && isLoading) {
                showSkeletons();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                binding.layoutNearbySkeleton.setVisibility(View.GONE);
                binding.layoutRankingSkeleton.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 请求定位权限 → 立即加载基础数据 + 尝试GPS
     */
    private void requestLocationAndLoad() {
        // 1. 立即用缓存城市加载天气和榜单（不等待GPS）
        loadCityBasedData();

        // 2. 尝试GPS定位来更新附近的景点
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            requestCurrentLocation();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * 获取当前GPS位置
     */
    @SuppressWarnings("MissingPermission")
    private void requestCurrentLocation() {
        try {
            LocationRequest locationRequest = new LocationRequest.Builder(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000)
                    .setMaxUpdates(1)
                    .build();

            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                    .addOnSuccessListener(location -> {
                        if (location != null && !gpsLoaded) {
                            gpsLoaded = true;
                            loadGpsBasedData(location);
                            // 逆地理编码获取城市名
                            updateCityFromLocation(location);
                        }
                    })
                    .addOnFailureListener(e -> loadCityBasedData());
        } catch (Exception e) {
            loadCityBasedData();
        }
    }

    /**
     * 基于GPS加载数据
     */
    private void loadGpsBasedData(Location location) {
        showSkeletons();
        viewModel.loadNearbyByGps(location.getLongitude(), location.getLatitude(), 20);

        // 城市榜单仍需城市名，用缓存城市
        String city = tokenManager.getCurrentCity();
        String cityCode = tokenManager.getCurrentCityCode();
        if (!"定位中...".equals(city)) {
            viewModel.loadWeather(cityCode.isEmpty() ? "440100" : cityCode);
            viewModel.loadRankings(city, null, 10);
        }
    }

    /**
     * 从GPS坐标更新城市名和天气
     */
    private void updateCityFromLocation(Location location) {
        String cityCode = tokenManager.getCurrentCityCode();
        viewModel.loadWeather(cityCode.isEmpty() ? "440100" : cityCode);
        // 更新城市名
        binding.tvCityName.setText("当前位置");
    }

    /**
     * 回退：基于缓存城市加载
     */
    private void loadCityBasedData() {
        String city = tokenManager.getCurrentCity();
        String cityCode = tokenManager.getCurrentCityCode();

        if (!"定位中...".equals(city)) {
            showSkeletons();
            viewModel.loadWeather(cityCode.isEmpty() ? "440100" : cityCode);
            viewModel.loadNearby(city, 20);
            viewModel.loadRankings(city, null, 10);
        }
    }

    private void showSkeletons() {
        binding.layoutWeatherSkeleton.setVisibility(View.VISIBLE);
        binding.layoutNearbySkeleton.setVisibility(View.VISIBLE);
        binding.layoutRankingSkeleton.setVisibility(View.VISIBLE);
        startSkeletonShimmer(
                binding.layoutWeatherSkeleton,
                binding.layoutNearbySkeleton,
                binding.layoutRankingSkeleton
        );
    }

    private void startSkeletonShimmer(View... views) {
        android.view.animation.Animation shimmer =
                AnimationUtils.loadAnimation(requireContext(), com.gdpt.huantu.R.anim.shimmer_animation);
        for (View v : views) {
            if (v != null) v.startAnimation(shimmer);
        }
    }

    private void stopSkeletonShimmer(View... views) {
        for (View v : views) {
            if (v != null) v.clearAnimation();
        }
    }

    private String generateTripAdvice(WeatherInfo weather) {
        int temp = weather.getDayTemp();
        String condition = weather.getDayWeather();
        if (condition == null) condition = "";

        if (condition.contains("雨")) {
            return "💡 今天有雨，建议选择室内景点，带好雨具，出行注意安全";
        } else if (condition.contains("雪")) {
            return "💡 下雪天路滑，注意保暖防滑，雪景拍照超出片哦";
        } else if (condition.contains("阴")) {
            return "💡 今天阴天凉爽，适合城市漫步，推荐去老街区和博物馆";
        } else if (condition.contains("多云")) {
            return "💡 多云天气温和舒适，非常适合户外徒步和公园野餐";
        } else if (condition.contains("晴")) {
            if (temp > 35) return "💡 高温晴天！建议避开中午时段出行，多补充水分，做好防晒";
            else if (temp >= 25) return "💡 阳光正好，温度舒适！推荐去户外景点和自然风光区";
            else if (temp >= 15) return "💡 天气宜人，适合全天户外活动，快去探索城市吧";
            else return "💡 晴天但偏凉，出门记得带件外套，享受冬日暖阳";
        }

        if (temp > 35) return "💡 高温预警！建议避开中午时段出行，多补充水分";
        else if (temp >= 25) return "💡 今天适合出行，祝您旅途愉快！";
        else if (temp >= 15) return "💡 温度舒适，是出游的好日子，尽情享受吧";
        else if (temp >= 5) return "💡 天气偏凉，出门记得添衣保暖哦";
        else return "💡 天气较冷，注意保暖，推荐温泉或室内景点";
    }

    private void searchByType(String type) {
        Intent intent = new Intent(requireContext(), SearchActivity.class);
        intent.putExtra("poiType", type);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
