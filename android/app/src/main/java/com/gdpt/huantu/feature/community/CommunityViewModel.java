package com.gdpt.huantu.feature.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gdpt.huantu.core.model.Post;
import com.gdpt.huantu.core.network.ApiResponse;
import com.gdpt.huantu.core.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class CommunityViewModel extends ViewModel {

    private final ApiService apiService;

    private final MutableLiveData<List<Post>> posts = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    @Inject
    public CommunityViewModel(ApiService apiService) {
        this.apiService = apiService;
    }

    public void loadPosts(String cityCode, int page, int pageSize) {
        isLoading.postValue(true);
        apiService.getPostList(cityCode, page, pageSize).enqueue(new Callback<ApiResponse<List<Post>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Post>>> call,
                                   Response<ApiResponse<List<Post>>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    posts.postValue(response.body().getData());
                } else if (response.body() != null) {
                    errorMessage.postValue(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Post>>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("网络错误: " + t.getMessage());
            }
        });
    }

    public void loadMorePosts(String cityCode, int page, int pageSize) {
        apiService.getPostList(cityCode, page, pageSize).enqueue(new Callback<ApiResponse<List<Post>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Post>>> call,
                                   Response<ApiResponse<List<Post>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    List<Post> current = posts.getValue();
                    if (current == null) current = new ArrayList<>();
                    List<Post> newPosts = new ArrayList<>(current);
                    newPosts.addAll(response.body().getData());
                    posts.postValue(newPosts);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Post>>> call, Throwable t) {
                // 加载更多失败静默处理
            }
        });
    }

    public LiveData<List<Post>> getPosts() { return posts; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}
