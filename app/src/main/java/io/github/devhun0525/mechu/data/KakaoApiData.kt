package io.github.devhun0525.mechu.data

import android.location.Location
import android.provider.CallLog
import com.kakao.vectormap.LatLng
import io.github.devhun0525.mechu.BuildConfig
import io.github.devhun0525.mechu.kakaomap.Place

class KakaoApiData {
    companion object {
        var API_KEY = BuildConfig.KAKAO_MAPS_NATIVE_KEY
        var HTTP_API_KEY = "KakaoAK ${BuildConfig.KAKAO_MAPS_REST_API_KEY}"
        var location = Location("")
        var placeList : List<Place>? = null
    }
}