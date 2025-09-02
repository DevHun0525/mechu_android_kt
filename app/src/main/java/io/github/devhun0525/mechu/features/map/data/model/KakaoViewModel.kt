package io.github.devhun0525.mechu.features.map.data.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.devhun0525.mechu.features.map.data.model.KakaoApiData.Companion.location
import io.github.devhun0525.mechu.features.map.data.source.KakaoMapManager
import io.github.devhun0525.mechu.kakaomap.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlaceState(
    var place: List<Place>? = null,
    var places: MutableList<Place>? = mutableListOf()
)

class KakaoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PlaceState())
    val uiState: StateFlow<PlaceState> = _uiState.asStateFlow()
    var placeList: List<Place>? = null
    var placesList: MutableList<Place>? = mutableListOf()

    init {
        viewModelScope.launch {
            getMapList()
        }
    }

    fun getMapList(){
        for(i in 1..3){
            placeList = KakaoMapManager.categorySearch(
                i,
                location.longitude,
                location.latitude,
                350
            )
            placeList?.forEach {
                placesList?.add(it)
            }
        }

        _uiState.update { currentState ->
            currentState.copy(
                place = placeList,
                places = placesList,

            )

        }
        Log.e("KakaoViewModel", placeList.toString())
    }
}