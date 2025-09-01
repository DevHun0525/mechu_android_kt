package io.github.devhun0525.mechu.features.map.data.source

import android.util.Log
import io.github.devhun0525.mechu.features.map.data.model.KakaoApiData
import io.github.devhun0525.mechu.kakaomap.CategorySearchResponse
import io.github.devhun0525.mechu.kakaomap.KakaoLocalApiService
import io.github.devhun0525.mechu.kakaomap.Place
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response

open class KakaoMapManager {
    companion object {
        // 1. 로깅 인터셉터 생성 (로그를 상세히 보기 위해 LEVEL_BODY 설정)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 2. OkHttpClient에 인터셉터 추가
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        // retrofit 선언 (BASE URL 설정)
        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .client(client)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()


        // retrofit 객체 생성 (인터페이스를 담아야됨)
        val kakaoApi = retrofit.create(KakaoLocalApiService::class.java)
        var place: List<Place>? = null
        var places: MutableList<Place>? = mutableListOf()
        fun categorySearch(page: Int, x: Double, y: Double, radius: Int) {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response : Response<CategorySearchResponse>

                    if (KakaoApiData.Companion.keyword != null) {
                        response = kakaoApi.searchByKeyword(
                            query = KakaoApiData.Companion.keyword,
                            apiKey = KakaoApiData.Companion.HTTP_API_KEY,
                            categoryGroupCode = "FD6", // 음식점
                            longitude = x,
                            latitude = y,
                            radius = radius,              // 단위: 미터(m), 최소: 0, 최대: 20000
                            page = page,              // 단위: 미터(m), 최소: 0, 최대: 20000
                            size = 15,              // 단위: 미터(m), 최소: 0, 최대: 20000
                            sort = "distance",
                        )
                    } else {
                        response = kakaoApi.searchByCategory(
                            apiKey = KakaoApiData.Companion.HTTP_API_KEY,
                            categoryGroupCode = "FD6", // 음식점
                            longitude = x,
                            latitude = y,
                            radius = radius,              // 단위: 미터(m), 최소: 0, 최대: 20000
                            page = page,              // 단위: 미터(m), 최소: 0, 최대: 20000
                            size = 15,              // 단위: 미터(m), 최소: 0, 최대: 20000
                            sort = "distance",
                        )
                    }

                    place = response.body()?.documents

                    if (response.isSuccessful) {
                        // 성공! UI 업데이트는 Dispatchers.Main 에서
                        if(place != null){
                            place?.forEach {
                                places?.add(it)
                            }
                        }


                        if(places != null){
                            places?.forEach {
                                Log.d("KakaoSearch", "places: ${it.place_name}, 주소: ${it.address_name}")
                            }
                        }

                        KakaoApiData.Companion.placeList = places
                    } else {
                        Log.w("KakaoSearch", "호출 실패: ${response.message()}")
                    }
                } catch (e: Exception) {
                    Log.e("KakaoSearch", "오류: ", e)
                }
            }
        }
    }
}