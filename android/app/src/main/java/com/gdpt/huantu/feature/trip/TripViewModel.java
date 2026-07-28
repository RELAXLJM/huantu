package com.gdpt.huantu.feature.trip;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gdpt.huantu.core.model.Route;
import com.gdpt.huantu.core.network.ApiResponse;
import com.gdpt.huantu.core.network.ApiService;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class TripViewModel extends ViewModel {

    private final ApiService apiService;

    private final MutableLiveData<List<Route>> routes = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    @Inject
    public TripViewModel(ApiService apiService) {
        this.apiService = apiService;
    }

    public void loadRoutes(Integer status) {
        isLoading.postValue(true);
        apiService.getRouteList(status).enqueue(new Callback<ApiResponse<List<Route>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Route>>> call,
                                   Response<ApiResponse<List<Route>>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    routes.postValue(response.body().getData());
                } else if (response.body() != null) {
                    errorMessage.postValue(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Route>>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("网络错误: " + t.getMessage());
            }
        });
    }

    public LiveData<List<Route>> getRoutes() { return routes; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}
