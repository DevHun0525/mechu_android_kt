package io.github.devhun0525.mechu.core.data

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data object First
@kotlinx.serialization.Serializable
data class Second(val id: String)
@Serializable
data object Third
