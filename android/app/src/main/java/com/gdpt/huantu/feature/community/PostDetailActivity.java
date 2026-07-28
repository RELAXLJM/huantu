package com.gdpt.huantu.feature.community;
import com.gdpt.huantu.R;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gdpt.huantu.core.model.Post;
import com.gdpt.huantu.core.model.Route;
import com.gdpt.huantu.core.network.ApiResponse;
import com.gdpt.huantu.core.network.ApiService;
import com.gdpt.huantu.core.util.Constants;
import com.gdpt.huantu.databinding.ActivityPostDetailBinding;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class PostDetailActivity extends AppCompatActivity {

    private ActivityPostDetailBinding binding;
    private PostDetailViewModel viewModel;

    @Inject
    ApiService apiService;

    private long postId;
    private Post currentPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(PostDetailViewModel.class);

        postId = getIntent().getLongExtra("postId", 0);

        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> finish());

        binding.btnLike.setOnClickListener(v -> interact(Constants.INTERACT_TYPE_LIKE));
        binding.btnCollect.setOnClickListener(v -> interact(Constants.INTERACT_TYPE_COLLECT));
        binding.btnUseful.setOnClickListener(v -> interact(Constants.INTERACT_TYPE_USEFUL));
        binding.btnAddToRoute.setOnClickListener(v -> showRoutePicker());

        viewModel.getPost().observe(this, this::displayPost);
        viewModel.loadPost(postId);
    }

    private void displayPost(Post post) {
        if (post == null) return;
        currentPost = post;

        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setTitle(post.getAuthorNickname());
        binding.tvTitle.setText(post.getTitle() != null && !post.getTitle().isEmpty()
                ? post.getTitle() : "帖子详情");
        binding.tvContent.setText(post.getContent());
        binding.tvAuthor.setText(post.getAuthorNickname());
        binding.tvLocation.setText(post.getLocationTag() != null
                ? post.getLocationTag() : post.getCity());

        binding.tvLocalBadge.setVisibility(post.isLocalAuth() ? android.view.View.VISIBLE
                : android.view.View.GONE);
        binding.tvScenicName.setVisibility(
                post.getScenicName() != null ? android.view.View.VISIBLE : android.view.View.GONE);
        if (post.getScenicName() != null) {
            binding.tvScenicName.setText("📍 " + post.getScenicName());
        }

        updateInteractUI(post);
    }

    private void updateInteractUI(Post post) {
        binding.tvLikeCount.setText(String.valueOf(post.getLikeCount()));
        binding.tvCollectCount.setText(String.valueOf(post.getCollectCount()));
        binding.tvUsefulCount.setText(String.valueOf(post.getUsefulCount()));
    }

    private void interact(int type) {
        if (!isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        apiService.interact(postId, type).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call,
                                   Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    // 重新加载帖子获取最新状态
                    viewModel.loadPost(postId);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRoutePicker() {
        if (!isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        apiService.getRouteList(1).enqueue(new Callback<ApiResponse<List<Route>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Route>>> call,
                                   Response<ApiResponse<List<Route>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess() && response.body().getData() != null) {
                    List<Route> routes = response.body().getData();
                    if (routes.isEmpty()) {
                        Toast.makeText(PostDetailActivity.this,
                                "暂无已保存的路线", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String[] names = new String[routes.size()];
                    for (int i = 0; i < routes.size(); i++) {
                        names[i] = routes.get(i).getTitle() != null
                                ? routes.get(i).getTitle() : routes.get(i).getDestination();
                    }
                    new AlertDialog.Builder(PostDetailActivity.this)
                            .setTitle("选择要加入的路线")
                            .setItems(names, (dialog, which) -> {
                                addToRoute(routes.get(which).getId());
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Route>>> call, Throwable t) {
            }
        });
    }

    private void addToRoute(long routeId) {
        apiService.addToRoute(postId, routeId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call,
                                   Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PostDetailActivity.this,
                            "已加入行程！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
            }
        });
    }

    private boolean isLoggedIn() {
        try {
            return getApplication() instanceof com.gdpt.huantu.HuantuApplication;
        } catch (Exception e) {
            return false;
        }
    }
}
