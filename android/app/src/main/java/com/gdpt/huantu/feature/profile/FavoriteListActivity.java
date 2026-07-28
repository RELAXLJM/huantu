package com.gdpt.huantu.feature.profile;
import com.gdpt.huantu.R;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gdpt.huantu.core.network.ApiService;
import com.gdpt.huantu.databinding.ActivityFavoriteListBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class FavoriteListActivity extends AppCompatActivity {

    private ActivityFavoriteListBinding binding;

    @Inject
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setTitle("我的收藏");
        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> finish());

        loadFavorites();
    }

    private void loadFavorites() {
        apiService.getFavoriteList(null).enqueue(
                new Callback<com.gdpt.huantu.core.network.ApiResponse<List<Map<String, Object>>>>() {
                    @Override
                    public void onResponse(Call<com.gdpt.huantu.core.network.ApiResponse<List<Map<String, Object>>>> call,
                                           Response<com.gdpt.huantu.core.network.ApiResponse<List<Map<String, Object>>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            List<Map<String, Object>> data = response.body().getData();
                            if (data != null && !data.isEmpty()) {
                                binding.tvContent.setText("共 " + data.size() + " 条收藏");
                            } else {
                                binding.tvContent.setText("暂无收藏");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<com.gdpt.huantu.core.network.ApiResponse<List<Map<String, Object>>>> call,
                                          Throwable t) {
                        Toast.makeText(FavoriteListActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
