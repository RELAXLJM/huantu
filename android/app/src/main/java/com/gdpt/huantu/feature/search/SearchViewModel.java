package com.gdpt.huantu.feature.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gdpt.huantu.core.model.Scenic;
import com.gdpt.huantu.core.network.ApiResponse;
import com.gdpt.huantu.core.network.ApiService;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class SearchViewModel extends ViewModel {

    private final ApiService apiService;
    private final MutableLiveData<List<Scenic>> results = new MutableLiveData<>();

    @Inject
    public SearchViewModel(ApiService apiService) {
        this.apiService = apiService;
    }

    public void search(String cityCode, String keyword, String poiType) {
        apiService.search(cityCode, keyword, poiType).enqueue(
                new Callback<ApiResponse<List<Scenic>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Scenic>>> call,
                                           Response<ApiResponse<List<Scenic>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            results.postValue(response.body().getData());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Scenic>>> call, Throwable t) {
                    }
                });
    }

    public LiveData<List<Scenic>> getResults() { return results; }
}
