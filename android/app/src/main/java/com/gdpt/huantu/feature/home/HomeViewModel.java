package com.gdpt.huantu.feature.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gdpt.huantu.core.model.Scenic;
import com.gdpt.huantu.core.model.WeatherInfo;
import com.gdpt.huantu.core.network.ApiResponse;
import com.gdpt.huantu.core.network.ApiService;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private final ApiService apiService;

    private final MutableLiveData<WeatherInfo> weather = new MutableLiveData<>();
    private final MutableLiveData<List<Scenic>> nearbyScenics = new MutableLiveData<>();
    private final MutableLiveData<List<Scenic>> rankings = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    @Inject
    public HomeViewModel(ApiService apiService) {
        this.apiService = apiService;
    }

    public void loadNearbyByGps(double lng, double lat, int limit) {
        isLoading.postValue(true);
        apiService.getNearbyByGps(lng, lat, limit).enqueue(new Callback<ApiResponse<List<Scenic>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Scenic>>> call,
                                   Response<ApiResponse<List<Scenic>>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    nearbyScenics.postValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Scenic>>> call, Throwable t) {
                isLoading.postValue(false);
            }
        });
    }

    public void loadWeather(String cityCode) {
        apiService.getWeather(cityCode).enqueue(new Callback<ApiResponse<WeatherInfo>>() {
            @Override
            public void onResponse(Call<ApiResponse<WeatherInfo>> call,
                                   Response<ApiResponse<WeatherInfo>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    weather.postValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<WeatherInfo>> call, Throwable t) {
                // 天气加载失败不影响主流程
            }
        });
    }

    public void loadNearby(String city, int limit) {
        isLoading.postValue(true);
        apiService.getNearby(city, limit).enqueue(new Callback<ApiResponse<List<Scenic>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Scenic>>> call,
                                   Response<ApiResponse<List<Scenic>>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    nearbyScenics.postValue(response.body().getData());
                } else if (response.body() != null) {
                    errorMessage.postValue(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Scenic>>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("网络错误: " + t.getMessage());
            }
        });
    }

    public void loadRankings(String city, String tag, int limit) {
        apiService.getRankings(city, tag, limit).enqueue(new Callback<ApiResponse<List<Scenic>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Scenic>>> call,
                                   Response<ApiResponse<List<Scenic>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    rankings.postValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Scenic>>> call, Throwable t) {
                // 榜单加载失败不影响主流程
            }
        });
    }

    public LiveData<WeatherInfo> getWeather() { return weather; }
    public LiveData<List<Scenic>> getNearbyScenics() { return nearbyScenics; }
    public LiveData<List<Scenic>> getRankings() { return rankings; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}
