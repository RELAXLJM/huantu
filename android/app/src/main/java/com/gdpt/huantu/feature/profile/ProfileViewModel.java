package com.gdpt.huantu.feature.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gdpt.huantu.core.model.User;
import com.gdpt.huantu.core.network.ApiResponse;
import com.gdpt.huantu.core.network.ApiService;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final ApiService apiService;

    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    @Inject
    public ProfileViewModel(ApiService apiService) {
        this.apiService = apiService;
    }

    public void loadProfile() {
        apiService.getProfile().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call,
                                   Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    user.postValue(response.body().getData());
                } else if (response.body() != null) {
                    errorMessage.postValue(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                errorMessage.postValue("网络错误: " + t.getMessage());
            }
        });
    }

    public LiveData<User> getUser() { return user; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}
