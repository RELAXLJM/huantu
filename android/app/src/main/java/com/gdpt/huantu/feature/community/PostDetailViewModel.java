package com.gdpt.huantu.feature.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gdpt.huantu.core.model.Post;
import com.gdpt.huantu.core.network.ApiResponse;
import com.gdpt.huantu.core.network.ApiService;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class PostDetailViewModel extends ViewModel {

    private final ApiService apiService;
    private final MutableLiveData<Post> post = new MutableLiveData<>();

    @Inject
    public PostDetailViewModel(ApiService apiService) {
        this.apiService = apiService;
    }

    public void loadPost(long postId) {
        apiService.getPostDetail(postId).enqueue(new Callback<ApiResponse<Post>>() {
            @Override
            public void onResponse(Call<ApiResponse<Post>> call,
                                   Response<ApiResponse<Post>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    post.postValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Post>> call, Throwable t) {
            }
        });
    }

    public LiveData<Post> getPost() { return post; }
}
