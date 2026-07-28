package com.gdpt.huantu.feature.profile;
import com.gdpt.huantu.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.gdpt.huantu.MainActivity;
import com.gdpt.huantu.core.model.User;
import com.gdpt.huantu.core.network.TokenManager;
import com.gdpt.huantu.databinding.FragmentProfileBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Inject
    TokenManager tokenManager;

    private ProfileViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        observeData();
        loadData();
    }

    private void initViews() {
        // 编辑资料
        binding.layoutProfileHeader.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), EditProfileActivity.class));
        });

        // 编辑按钮
        binding.btnEdit.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), EditProfileActivity.class));
        });

        // 我的行程
        binding.itemMyTrips.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTripTab();
            }
        });

        // 收藏夹
        binding.itemFavorites.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), FavoriteListActivity.class));
        });

        // 足迹地图
        binding.layoutFootprintMap.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), FootprintMapActivity.class));
        });

        // 设置
        binding.itemSettings.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SettingsActivity.class));
        });
    }

    private void observeData() {
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                updateProfileUI(user);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfileUI(User user) {
        binding.tvNickname.setText(
                user.getNickname() != null ? user.getNickname() : "未设置昵称");
        binding.tvBio.setText(
                user.getBio() != null ? user.getBio() : "这个人很懒，什么都没写...");

        // 头像
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getAvatarUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .circleCrop()
                    .into(binding.ivAvatar);
        }

        // 统计数据
        binding.tvFootprintCount.setText(String.valueOf(user.getFootprintCount()));
        binding.tvRouteCount.setText(String.valueOf(user.getRouteCount()));
        binding.tvLikeCount.setText(String.valueOf(user.getLikeCount()));
    }

    private void loadData() {
        viewModel.loadProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 从编辑页返回时刷新
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
