package io.github.devhun0525.mechu.features.map.data.source

import android.util.Log
import androidx.lifecycle.ViewModel
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
    companion object{
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



        fun categorySearch(page: Int, x: Double, y: Double, radius: Int) : List<Place>? {
            var response: Response<CategorySearchResponse>? = null

            CoroutineScope(Dispatchers.IO).launch {
                try {
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


                } catch (e: Exception) {
                    Log.e("KakaoSearch", "오류: ", e)
                }
            }

            do {
                Thread.sleep(100)
            }while (response == null)

            return response.body()?.documents
        }

    }
}