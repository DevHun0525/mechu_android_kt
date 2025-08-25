package io.github.devhun0525.mechu.features.map.data.model

import android.location.Location
import io.github.devhun0525.mechu.BuildConfig
import io.github.devhun0525.mechu.kakaomap.Place

class KakaoApiData {
    enum class KeywordOptions(val keyword : String) {
        All("전체"),
        Cafe("카페"),
        KoreanFood("한식"),
        ChineseFood("중식"),
        JapaneseFood("일식"),
        WesternFood("양식"),
    }
    companion object {
        var API_KEY = BuildConfig.KAKAO_MAPS_NATIVE_KEY
        var HTTP_API_KEY = "KakaoAK ${BuildConfig.KAKAO_MAPS_REST_API_KEY}"
        var location = Location("")
        var placeList : List<Place>? = null
        var keyword : String? = null


        enum class Options(keyword : String) {
            DIET("다이어트"),
            LOCATION("위치"),
            WEATHER("날씨"),
            SEASON("계절"),
            PRICE("가격"),
        }
    }
}