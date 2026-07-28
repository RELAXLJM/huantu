package com.gdpt.huantu.feature.search;
import com.gdpt.huantu.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gdpt.huantu.core.model.Scenic;
import com.gdpt.huantu.databinding.ActivitySearchBinding;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private SearchViewModel viewModel;
    private com.gdpt.huantu.feature.home.NearbyAdapter resultAdapter;
    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable debounceRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        ((androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> finish());

        resultAdapter = new com.gdpt.huantu.feature.home.NearbyAdapter(
                this, new ArrayList<>(), scenic -> {
            Toast.makeText(this, scenic.getName(), Toast.LENGTH_SHORT).show();
        });

        binding.rvResults.setLayoutManager(new LinearLayoutManager(this));
        binding.rvResults.setAdapter(resultAdapter);

        // 搜索按钮
        binding.btnSearch.setOnClickListener(v -> {
            String keyword = binding.etSearch.getText().toString().trim();
            if (keyword.length() >= 1) doSearch(keyword);
        });

        // 键盘搜索键
        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String keyword = binding.etSearch.getText().toString().trim();
                if (keyword.length() >= 1) doSearch(keyword);
                return true;
            }
            return false;
        });

        // 输入时800ms防抖自动搜索
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                debounceHandler.removeCallbacks(debounceRunnable);
                debounceRunnable = () -> {
                    String keyword = s.toString().trim();
                    if (keyword.length() >= 2) doSearch(keyword);
                };
                debounceHandler.postDelayed(debounceRunnable, 800);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        viewModel.getResults().observe(this, scenics -> {
            if (scenics != null && !scenics.isEmpty()) {
                binding.tvHint.setVisibility(View.GONE);
                binding.rvResults.setVisibility(View.VISIBLE);
                resultAdapter.updateData(scenics);
            } else if (scenics != null) {
                binding.tvHint.setText("未找到相关景点，换个关键词试试吧");
                binding.tvHint.setVisibility(View.VISIBLE);
                binding.rvResults.setVisibility(View.GONE);
            }
        });

        String poiType = getIntent().getStringExtra("poiType");
        if (poiType != null && !poiType.isEmpty()) {
            binding.etSearch.setHint("搜索" + poiType + "...");
        }
        binding.etSearch.requestFocus();
    }

    private void doSearch(String keyword) {
        String poiType = getIntent().getStringExtra("poiType");
        viewModel.search(null, keyword, poiType);
        binding.tvHint.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        debounceHandler.removeCallbacks(debounceRunnable);
    }
}
