package com.gdpt.huantu.feature.profile;
import com.gdpt.huantu.R;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gdpt.huantu.core.network.ApiService;
import com.gdpt.huantu.databinding.ActivityFootprintMapBinding;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class FootprintMapActivity extends AppCompatActivity {

    private ActivityFootprintMapBinding binding;

    @Inject
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFootprintMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setTitle("我的足迹地图");
        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> finish());

        loadFootprintMap();
    }

    private void loadFootprintMap() {
        apiService.getFootprintMap().enqueue(
                new Callback<com.gdpt.huantu.core.network.ApiResponse<List<Map<String, Object>>>>() {
                    @Override
                    public void onResponse(Call<com.gdpt.huantu.core.network.ApiResponse<List<Map<String, Object>>>> call,
                                           Response<com.gdpt.huantu.core.network.ApiResponse<List<Map<String, Object>>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            List<Map<String, Object>> cities = response.body().getData();
                            if (cities != null && !cities.isEmpty()) {
                                StringBuilder sb = new StringBuilder("已点亮 " + cities.size() + " 座城市：\n");
                                for (Map<String, Object> city : cities) {
                                    sb.append("• ").append(city.get("city")).append("\n");
                                }
                                binding.tvContent.setText(sb.toString());
                            } else {
                                binding.tvContent.setText("还没有去过任何城市");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<com.gdpt.huantu.core.network.ApiResponse<List<Map<String, Object>>>> call,
                                          Throwable t) {
                        Toast.makeText(FootprintMapActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
