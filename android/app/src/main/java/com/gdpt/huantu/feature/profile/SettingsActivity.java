package com.gdpt.huantu.feature.profile;
import com.gdpt.huantu.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gdpt.huantu.core.network.ApiService;
import com.gdpt.huantu.core.network.TokenManager;
import com.gdpt.huantu.databinding.ActivitySettingsBinding;
import com.gdpt.huantu.feature.auth.LoginActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Inject
    TokenManager tokenManager;

    @Inject
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setTitle("设置");
        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> finish());

        binding.itemClearCache.setOnClickListener(v -> {
            Toast.makeText(this, "缓存已清除", Toast.LENGTH_SHORT).show();
        });

        binding.itemPrivacy.setOnClickListener(v -> {
            Toast.makeText(this, "隐私政策页面", Toast.LENGTH_SHORT).show();
        });

        binding.itemAbout.setOnClickListener(v -> {
            Toast.makeText(this, "寰途 v1.0.0", Toast.LENGTH_SHORT).show();
        });

        binding.btnLogout.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("退出登录")
                    .setMessage("确定要退出登录吗？")
                    .setPositiveButton("退出", (dialog, which) -> {
                        logout();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    private void logout() {
        apiService.logout().enqueue(new Callback<com.gdpt.huantu.core.network.ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<com.gdpt.huantu.core.network.ApiResponse<Void>> call,
                                   Response<com.gdpt.huantu.core.network.ApiResponse<Void>> response) {
                // 无论成功与否，都清除本地session
                tokenManager.clearSession();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finishAffinity();
            }

            @Override
            public void onFailure(Call<com.gdpt.huantu.core.network.ApiResponse<Void>> call,
                                  Throwable t) {
                tokenManager.clearSession();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finishAffinity();
            }
        });
    }
}
