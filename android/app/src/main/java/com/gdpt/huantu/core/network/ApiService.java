package com.gdpt.huantu.core.network;

import com.gdpt.huantu.core.model.*;
import com.gdpt.huantu.core.model.request.*;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * Retrofit API 接口定义
 * 所有端点与后端 Spring Boot Controller 一一对应
 */
public interface ApiService {

    // ==================== 用户 /api/user ====================

    @POST("api/user/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest request);

    @POST("api/user/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @POST("api/user/logout")
    Call<ApiResponse<Void>> logout();

    @GET("api/user/profile")
    Call<ApiResponse<User>> getProfile();

    @PUT("api/user/profile")
    @FormUrlEncoded
    Call<ApiResponse<User>> updateProfile(
            @Field("nickname") String nickname,
            @Field("avatarUrl") String avatarUrl,
            @Field("city") String city,
            @Field("bio") String bio
    );

    // ==================== 探索 /api/explore ====================

    @GET("api/explore/nearby")
    Call<ApiResponse<List<Scenic>>> getNearby(
            @Query("city") String city,
            @Query("limit") int limit
    );

    @GET("api/explore/weather")
    Call<ApiResponse<WeatherInfo>> getWeather(@Query("cityCode") String cityCode);

    @GET("api/explore/rankings")
    Call<ApiResponse<List<Scenic>>> getRankings(
            @Query("city") String city,
            @Query("tag") String tag,
            @Query("limit") int limit
    );

    @GET("api/explore/search")
    Call<ApiResponse<List<Scenic>>> search(
            @Query("cityCode") String cityCode,
            @Query("keyword") String keyword,
            @Query("poiType") String poiType
    );

    @GET("api/explore/nearby-gps")
    Call<ApiResponse<List<Scenic>>> getNearbyByGps(
            @Query("lng") double lng,
            @Query("lat") double lat,
            @Query("limit") int limit
    );

    // ==================== 路线 /api/route ====================

    @POST("api/route/generate")
    Call<ApiResponse<Route>> generateRoute(@Body RouteGenerateRequest request);

    @GET("api/route/list")
    Call<ApiResponse<List<Route>>> getRouteList(@Query("status") Integer status);

    @GET("api/route/{id}")
    Call<ApiResponse<Route>> getRouteDetail(@Path("id") long id);

    @PUT("api/route/{id}")
    Call<ApiResponse<Route>> updateRoute(
            @Path("id") long id,
            @Query("status") Integer status,
            @Query("title") String title
    );

    @DELETE("api/route/{id}")
    Call<ApiResponse<Void>> deleteRoute(@Path("id") long id);

    @PUT("api/route/{id}/reorder")
    Call<ApiResponse<Route>> reorderScenics(
            @Path("id") long id,
            @Body List<ReorderRequest> items
    );

    // ==================== 帖子 /api/post ====================

    @GET("api/post/list")
    Call<ApiResponse<List<Post>>> getPostList(
            @Query("cityCode") String cityCode,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    @GET("api/post/{id}")
    Call<ApiResponse<Post>> getPostDetail(@Path("id") long id);

    @POST("api/post/publish")
    Call<ApiResponse<Post>> publishPost(@Body PostPublishRequest request);

    @POST("api/post/{id}/interact")
    Call<ApiResponse<Map<String, Object>>> interact(
            @Path("id") long id,
            @Query("type") int type
    );

    @POST("api/post/{id}/add-to-route")
    Call<ApiResponse<Void>> addToRoute(
            @Path("id") long id,
            @Query("routeId") long routeId
    );

    // ==================== 收藏 /api/favorite ====================

    @POST("api/favorite/add")
    Call<ApiResponse<Void>> addFavorite(
            @Query("targetId") long targetId,
            @Query("targetType") int targetType
    );

    @DELETE("api/favorite/remove")
    Call<ApiResponse<Void>> removeFavorite(
            @Query("targetId") long targetId,
            @Query("targetType") int targetType
    );

    @GET("api/favorite/list")
    Call<ApiResponse<List<Map<String, Object>>>> getFavoriteList(
            @Query("targetType") Integer targetType
    );

    // ==================== 足迹 /api/footprint ====================

    @POST("api/footprint/checkin")
    Call<ApiResponse<Void>> checkin(@Query("scenicId") long scenicId);

    @GET("api/footprint/list")
    Call<ApiResponse<List<Map<String, Object>>>> getFootprintList();

    @GET("api/footprint/map")
    Call<ApiResponse<List<Map<String, Object>>>> getFootprintMap();
}
