package io.github.devhun0525.mechu.data

import io.github.devhun0525.mechu.BuildConfig

class KakaoApiData {
    companion object {
        var API_KEY = BuildConfig.KAKAO_MAPS_NATIVE_KEY
        var HTTP_API_KEY = "KakaoAK ${BuildConfig.KAKAO_MAPS_REST_API_KEY}"
    }
}