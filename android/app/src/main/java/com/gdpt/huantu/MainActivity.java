package com.gdpt.huantu;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.gdpt.huantu.core.network.TokenManager;
import com.gdpt.huantu.databinding.ActivityMainBinding;
import com.gdpt.huantu.feature.auth.LoginActivity;
import com.gdpt.huantu.feature.community.CommunityFragment;
import com.gdpt.huantu.feature.home.HomeFragment;
import com.gdpt.huantu.feature.profile.ProfileFragment;
import com.gdpt.huantu.feature.trip.TripFragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Inject
    TokenManager tokenManager;

    private HomeFragment homeFragment;
    private TripFragment tripFragment;
    private CommunityFragment communityFragment;
    private ProfileFragment profileFragment;
    private Fragment activeFragment;

    private static final String TAG_HOME = "home";
    private static final String TAG_TRIP = "trip";
    private static final String TAG_COMMUNITY = "community";
    private static final String TAG_PROFILE = "profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!tokenManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        initFragments();
        setupBottomNav();
    }

    private void initFragments() {
        homeFragment = new HomeFragment();
        tripFragment = new TripFragment();
        communityFragment = new CommunityFragment();
        profileFragment = new ProfileFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, profileFragment, TAG_PROFILE).hide(profileFragment)
                .add(R.id.fragment_container, communityFragment, TAG_COMMUNITY).hide(communityFragment)
                .add(R.id.fragment_container, tripFragment, TAG_TRIP).hide(tripFragment)
                .add(R.id.fragment_container, homeFragment, TAG_HOME)
                .commit();

        activeFragment = homeFragment;
    }

    private void setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                switchFragment(homeFragment, TAG_HOME);
                return true;
            } else if (itemId == R.id.nav_trip) {
                switchFragment(tripFragment, TAG_TRIP);
                return true;
            } else if (itemId == R.id.nav_community) {
                switchFragment(communityFragment, TAG_COMMUNITY);
                return true;
            } else if (itemId == R.id.nav_profile) {
                switchFragment(profileFragment, TAG_PROFILE);
                return true;
            }
            return false;
        });

        binding.bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void switchFragment(Fragment target, String tag) {
        if (target == activeFragment) return;
        getSupportFragmentManager().beginTransaction()
                .hide(activeFragment)
                .show(target)
                .commit();
        activeFragment = target;
    }

    public void switchToTripTab() {
        binding.bottomNav.setSelectedItemId(R.id.nav_trip);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
