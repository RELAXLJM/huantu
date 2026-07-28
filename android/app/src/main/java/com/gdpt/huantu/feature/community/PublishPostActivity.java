package com.gdpt.huantu.feature.community;
import com.gdpt.huantu.R;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gdpt.huantu.core.model.Post;
import com.gdpt.huantu.core.model.request.PostPublishRequest;
import com.gdpt.huantu.core.network.ApiResponse;
import com.gdpt.huantu.core.network.ApiService;
import com.gdpt.huantu.core.network.TokenManager;
import com.gdpt.huantu.databinding.ActivityPublishPostBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class PublishPostActivity extends AppCompatActivity {

    private ActivityPublishPostBinding binding;

    @Inject
    ApiService apiService;

    @Inject
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPublishPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setTitle("发布帖子");
        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> finish());

        binding.btnPublish.setOnClickListener(v -> publish());
    }

    private void publish() {
        String content = binding.etContent.getText().toString().trim();
        String title = binding.etTitle.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }

        PostPublishRequest request = new PostPublishRequest();
        request.setContent(content);
        request.setTitle(title);
        request.setCity(tokenManager.getCurrentCity());
        request.setCityCode(tokenManager.getCurrentCityCode());
        request.setLocationTag(binding.etLocation.getText().toString().trim());
        request.setIsLocalAuth(binding.switchLocalAuth.isChecked() ? 1 : 0);

        binding.btnPublish.setEnabled(false);

        apiService.publishPost(request).enqueue(new Callback<ApiResponse<Post>>() {
            @Override
            public void onResponse(Call<ApiResponse<Post>> call,
                                   Response<ApiResponse<Post>> response) {
                binding.btnPublish.setEnabled(true);
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    Toast.makeText(PublishPostActivity.this,
                            "发布成功！", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response.body() != null) {
                    Toast.makeText(PublishPostActivity.this,
                            response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Post>> call, Throwable t) {
                binding.btnPublish.setEnabled(true);
                Toast.makeText(PublishPostActivity.this,
                        "发布失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
