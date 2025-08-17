package io.github.devhun0525.mechu.kakaomap

// CategorySearchResponse.kt
data class CategorySearchResponse(
    val documents: List<Place>
)

data class Place(
    val place_name: String,
    val address_name: String,
    val x: String, // 경도(Longitude)
    val y: String  // 위도(Latitude)
)