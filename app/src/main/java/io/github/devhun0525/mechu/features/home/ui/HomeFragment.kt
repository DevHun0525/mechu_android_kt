package io.github.devhun0525.mechu.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.launch
import androidx.compose.ui.semantics.text
import androidx.lifecycle.lifecycleScope
import io.github.devhun0525.mechu.R
import io.github.devhun0525.mechu.features.map.data.model.KakaoApiData
import io.github.devhun0525.mechu.features.map.data.source.KakaoMapManager
import io.github.devhun0525.mechu.features.map.data.source.KakaoMapManager.Companion.places
import io.github.devhun0525.mechu.kakaomap.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.random.Random
import kotlin.text.append

class HomeFragment : Fragment() {
    var restaurantListTextView: TextView? = null
    var randomButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restaurantListTextView = view.findViewById<TextView>(R.id.restaurant_list_text)
        randomButton = view.findViewById<Button>(R.id.random_button)

        randomButton?.setOnClickListener {
            restaurantListTextView?.text = ""
            restaurantListTextView?.text = KakaoApiData.placeList?.randomOrNull()?.place_name

        }
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
                    Handler(Looper.getMainLooper()).post { restaurantListTextView?.text = "주변 음식점 정보를 불러오는 중입니다." }
                    Log.e("HomeFragment", "API 호출 결과: ${KakaoApiData.placeList}")
                    Thread.sleep(1000)
                }

                Log.e("HomeFragment", "API 호출 결과: ${KakaoApiData.placeList}")
                Handler(Looper.getMainLooper()).post {
                    restaurantListTextView?.text = ""
                    KakaoApiData.placeList?.forEach { place ->
                        restaurantListTextView?.append("${place.place_name}\n")
                    }
                }
            }
        }.start()

    }

}