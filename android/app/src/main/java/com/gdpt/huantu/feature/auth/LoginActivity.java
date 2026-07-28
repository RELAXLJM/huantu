package com.gdpt.huantu.feature.auth;
import com.gdpt.huantu.R;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gdpt.huantu.MainActivity;
import com.gdpt.huantu.core.model.LoginResponse;
import com.gdpt.huantu.core.model.User;
import com.gdpt.huantu.core.model.request.LoginRequest;
import com.gdpt.huantu.core.network.ApiResponse;
import com.gdpt.huantu.core.network.ApiService;
import com.gdpt.huantu.core.network.TokenManager;
import com.gdpt.huantu.databinding.ActivityLoginBinding;
import android.widget.EditText;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Inject
    ApiService apiService;

    @Inject
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 如果已登录，直接跳转主页
        if (tokenManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        initViews();
    }

    private void initViews() {
        binding.btnLogin.setOnClickListener(v -> doLogin());
        binding.tvGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void doLogin() {
        EditText etPhone = binding.tilPhone.getEditText();
        EditText etPassword = binding.tilPassword.getEditText();

        if (etPhone == null || etPassword == null) return;

        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 校验
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        LoginRequest request = new LoginRequest(phone, password);
        apiService.login(request).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call,
                                   Response<ApiResponse<LoginResponse>> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResp = response.body();
                    if (apiResp.isSuccess() && apiResp.getData() != null) {
                        // 保存 sessionId
                        String sessionId = apiResp.getData().getSessionId();
                        tokenManager.saveSessionId(sessionId);

                        // 获取用户信息
                        loadUserProfile();

                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                apiResp.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败，请检查网络", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(LoginActivity.this,
                        "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserProfile() {
        apiService.getProfile().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call,
                                   Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess() && response.body().getData() != null) {
                    User user = response.body().getData();
                    tokenManager.saveUserInfo(
                            user.getId(),
                            user.getNickname(),
                            user.getAvatarUrl()
                    );
                    if (user.getCity() != null) {
                        tokenManager.saveCityInfo(user.getCity(), "");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // 静默失败，不影响主流程
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        binding.btnLogin.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
