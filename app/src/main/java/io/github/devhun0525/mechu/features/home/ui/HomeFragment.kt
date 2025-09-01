package io.github.devhun0525.mechu.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import io.github.devhun0525.mechu.features.map.data.model.KakaoApiData
import io.github.devhun0525.mechu.features.map.data.source.KakaoMapManager
import io.github.devhun0525.mechu.features.map.data.source.KakaoMapManager.Companion.places
import io.github.devhun0525.mechu.kakaomap.Place

class HomeFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                HomeScreen(modifier = Modifier.fillMaxSize(), KakaoApiData.placeList)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        Log.e("hun", "onResume")
        refreshRestaurantList()
    }

    private fun refreshRestaurantList() {
        val location = KakaoApiData.location

        object : Thread() {
            override fun run() {
                places = mutableListOf()

                for(i in 1..3){
                    KakaoMapManager.categorySearch(
                        i,
                        location.longitude,
                        location.latitude,
                        350
                    )
                }

                while (KakaoApiData.placeList == null) {
                    Log.e("HomeFragment", "API 호출 결과: ${KakaoApiData.placeList}")
                    Thread.sleep(1000)
                }

                if(KakaoApiData.placeList != null){
                    Log.e("HomeFragment", "API 호출 결과: ${KakaoApiData.placeList}")
                    Handler(Looper.getMainLooper()).post {
                        KakaoApiData.placeList?.forEach { place ->
                        }
                    }
                }

            }
        }.start()

    }

}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    names: List<Place>?
){
    var randomPlaceToShow by remember { mutableStateOf<Place?>(null) }

    // Column으로 전체를 감싸서 세로로 배치합니다.
    // HomeScreen으로 전달된 modifier는 Column에 적용합니다.
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally // 자식들을 가로 중앙에 정렬
    ) {
        // Greeting이 남은 공간을 모두 차지하도록 weight(1f)를 줍니다.
        // 이렇게 하면 Button이 항상 하단에 위치하게 됩니다.
        if (randomPlaceToShow != null) {
            Greeting(modifier = Modifier.weight(1f), name = randomPlaceToShow)
        } else {
            Greeting(modifier = Modifier.weight(1f), names = names)
        }

        // 이제 Column의 자식인 Button은 지정된 크기를 가질 수 있습니다.
        Button(
            modifier = Modifier
                .width(150.dp)
                .height(70.dp)
                .padding(bottom = 16.dp), // 하단에 여백 추가 (선택 사항)
            onClick = {
                if (!names.isNullOrEmpty()) {
                    val randomIndex = names.indices.random()
                    randomPlaceToShow = names[randomIndex]
                } else {
                    randomPlaceToShow = null
                }
            }
        ) {
            Text("Show Random Place")
        }
    }

//    Text("Randomly Selected Place:", modifier = Modifier.padding(bottom = 8.dp))

}

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    names: List<Place>?
){
    LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
        names?.forEach {
            item {
                Text(text = it.place_name)
            }
        }
    }
}

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    name: Place?
){
    LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
        item {
            Text(text = name?.place_name ?: "NULL")
        }
    }
}