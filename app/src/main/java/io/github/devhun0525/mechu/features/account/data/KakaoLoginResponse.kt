package io.github.devhun0525.mechu.kakaomap

data class KakaoLoginResponse(
    val documents: List<Login>? = null
)

data class Login(
    val code: String,
    val error: String,
    val error_description: String,
    val state: String
)