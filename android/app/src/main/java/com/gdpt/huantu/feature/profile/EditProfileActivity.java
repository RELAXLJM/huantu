package com.gdpt.huantu.feature.profile;
import com.gdpt.huantu.R;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gdpt.huantu.core.model.User;
import com.gdpt.huantu.core.network.ApiResponse;
import com.gdpt.huantu.core.network.ApiService;
import com.gdpt.huantu.core.network.TokenManager;
import com.gdpt.huantu.databinding.ActivityEditProfileBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;

    @Inject
    ApiService apiService;

    @Inject
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setTitle("编辑资料");
        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> finish());

        // 加载现有资料
        loadProfile();

        binding.btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        apiService.getProfile().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call,
                                   Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess() && response.body().getData() != null) {
                    User user = response.body().getData();
                    binding.etNickname.setText(user.getNickname() != null ? user.getNickname() : "");
                    binding.etBio.setText(user.getBio() != null ? user.getBio() : "");
                    binding.etCity.setText(user.getCity() != null ? user.getCity() : "");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
            }
        });
    }

    private void saveProfile() {
        String nickname = binding.etNickname.getText().toString().trim();
        String bio = binding.etBio.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();

        apiService.updateProfile(nickname, null, city, bio).enqueue(
                new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call,
                                           Response<ApiResponse<User>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            Toast.makeText(EditProfileActivity.this,
                                    "保存成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        Toast.makeText(EditProfileActivity.this,
                                "保存失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
