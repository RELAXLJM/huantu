package com.gdpt.huantu.feature.trip;
import com.gdpt.huantu.R;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gdpt.huantu.core.model.Route;
import com.gdpt.huantu.core.model.request.RouteGenerateRequest;
import com.gdpt.huantu.core.network.ApiResponse;
import com.gdpt.huantu.core.network.ApiService;
import com.gdpt.huantu.databinding.ActivityRouteGenerateBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class RouteGenerateActivity extends AppCompatActivity {

    private ActivityRouteGenerateBinding binding;

    @Inject
    ApiService apiService;

    private String selectedCompanionType = "solo";
    private Route generatedRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRouteGenerateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setTitle("AI 路线生成");
        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> finish());

        initCompanionChips();
        binding.btnGenerate.setOnClickListener(v -> generateRoute());
        binding.btnSaveRoute.setOnClickListener(v -> saveRoute());
    }

    private void initCompanionChips() {
        binding.chipGroupCompanion.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_solo) selectedCompanionType = "solo";
            else if (checkedId == R.id.chip_couple) selectedCompanionType = "couple";
            else if (checkedId == R.id.chip_family) selectedCompanionType = "family";
            else if (checkedId == R.id.chip_elderly) selectedCompanionType = "elderly";
        });
        binding.chipSolo.setChecked(true);
    }

    private void generateRoute() {
        String destination = binding.etDestination.getText().toString().trim();
        String daysStr = binding.etDays.getText().toString().trim();
        String budgetMinStr = binding.etBudgetMin.getText().toString().trim();
        String budgetMaxStr = binding.etBudgetMax.getText().toString().trim();

        if (destination.isEmpty()) {
            Toast.makeText(this, "请输入目的地", Toast.LENGTH_SHORT).show();
            return;
        }
        if (daysStr.isEmpty()) {
            Toast.makeText(this, "请输入游玩天数", Toast.LENGTH_SHORT).show();
            return;
        }

        int days = Integer.parseInt(daysStr);
        int budgetMin = budgetMinStr.isEmpty() ? 0 : Integer.parseInt(budgetMinStr);
        int budgetMax = budgetMaxStr.isEmpty() ? 1000 : Integer.parseInt(budgetMaxStr);

        // 收集偏好
        StringBuilder prefs = new StringBuilder();
        if (binding.chipNature.isChecked()) prefs.append("自然风光,");
        if (binding.chipFoodPref.isChecked()) prefs.append("美食,");
        if (binding.chipCulture.isChecked()) prefs.append("人文历史,");
        if (binding.chipShopping.isChecked()) prefs.append("购物,");

        RouteGenerateRequest request = new RouteGenerateRequest();
        request.setDestination(destination);
        request.setDays(days);
        request.setCompanionType(selectedCompanionType);
        request.setBudgetMin(budgetMin);
        request.setBudgetMax(budgetMax);
        request.setPreference(prefs.toString());

        setLoading(true);

        apiService.generateRoute(request).enqueue(new Callback<ApiResponse<Route>>() {
            @Override
            public void onResponse(Call<ApiResponse<Route>> call,
                                   Response<ApiResponse<Route>> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    generatedRoute = response.body().getData();
                    showResult(generatedRoute);
                } else if (response.body() != null) {
                    Toast.makeText(RouteGenerateActivity.this,
                            response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Route>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(RouteGenerateActivity.this,
                        "生成失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showResult(Route route) {
        binding.layoutResult.setVisibility(View.VISIBLE);
        if (route.getDayPlans() != null) {
            DayTimelineAdapter adapter = new DayTimelineAdapter(route.getDayPlans());
            binding.rvResult.setLayoutManager(new LinearLayoutManager(this));
            binding.rvResult.setAdapter(adapter);
        }
    }

    private void saveRoute() {
        if (generatedRoute == null) return;
        apiService.updateRoute(generatedRoute.getId(), 1, null).enqueue(
                new Callback<ApiResponse<Route>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Route>> call,
                                           Response<ApiResponse<Route>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RouteGenerateActivity.this,
                                    "路线已保存！", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Route>> call, Throwable t) {
                    }
                });
    }

    private void setLoading(boolean loading) {
        binding.btnGenerate.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
