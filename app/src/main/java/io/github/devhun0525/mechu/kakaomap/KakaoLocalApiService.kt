package io.github.devhun0525.mechu.kakaomap
// KakaoLocalApiService.kt
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoLocalApiService {
        @GET("v2/local/search/category.json")
        suspend fun searchByCategory(
            @Header("Authorization") apiKey: String,
            @Query("category_group_code") categoryGroupCode: String,
            @Query("x") longitude: Double,
            @Query("y") latitude: Double,
            @Query("radius") radius: Int,
//            @Query("rect") rect: String,
            @Query("page") page: Int,
            @Query("size") size: Int,
            @Query("sort") sort: String,
        ): Response<CategorySearchResponse>

    @GET("v2/local/search/keyword.json")
    suspend fun searchByKeyword(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String?,
        @Query("category_group_code") categoryGroupCode: String,
        @Query("x") longitude: Double,
        @Query("y") latitude: Double,
        @Query("radius") radius: Int,
//            @Query("rect") rect: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
    ): Response<CategorySearchResponse>
}