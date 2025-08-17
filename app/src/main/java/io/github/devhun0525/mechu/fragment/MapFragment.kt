package io.github.devhun0525.mechu.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClientBuilder
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import io.github.devhun0525.mechu.R
import io.github.devhun0525.mechu.data.KakaoApiData
import io.github.devhun0525.mechu.kakaomap.KakaoLocalApiService
import io.github.devhun0525.mechu.kakaomap.KakaoMapManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader


class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var locationButton: Button
    private var kakaoMap: KakaoMap? = null
    private val PERMISSION_REQUEST_CODE = 1001
    private lateinit var fusedLocationClient: FusedLocationProviderClient // FusedLocationProviderClient 선언


    private val locationPermissions = arrayOf<String?>(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity()) // FusedLocationProviderClient 초기화

        getLocationPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById<MapView>(R.id.map_view)
        locationButton = view.findViewById<Button>(R.id.current_location_button)

        showMapView(view)
    }

    private fun showMapView(view: View) {
        // KakaoMapSDK 초기화!!
        KakaoMapSdk.init(requireContext(), KakaoApiData.API_KEY)
        Log.w("hun", "apikey : ${KakaoApiData.API_KEY}")



        locationButton.setOnClickListener {
            getCurLocation()
        }

        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출
                Log.d("KakaoMap", "onMapDestroy")
            }

            override fun onMapError(p0: Exception?) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출
                Log.e("KakaoMap", "onMapError", p0) // Log an exception
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaomap: KakaoMap) {
                // 정상적으로 인증이 완료되었을 때 호출
                kakaoMap = kakaomap

                getLocationPermissions()
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun getCurLocation() {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ) // PRIORITY_HIGH_ACCURACY 사용
            .addOnSuccessListener { location ->
                if (location != null) {
                    KakaoApiData.location = location;
                    val currentLatLng = LatLng.from(location.latitude, location.longitude)
                    kakaoMap?.moveCamera(
                        CameraUpdateFactory.newCenterPosition(currentLatLng),
                        CameraAnimation.from(10, true, true)
                    )

                    Log.w("MapFragment", "currentLatLng ${currentLatLng}")

                    val options = LabelOptions.from(currentLatLng)
                    kakaoMap?.labelManager?.layer?.addLabel(options)

                    Log.w("MapFragment", "kakaoMap ${kakaoMap}, kakaoMap ${kakaoMap?.labelManager}, kakaoMap ${kakaoMap?.labelManager?.layer}")

                } else {
                    Log.w("MapFragment", "Current location is null")
                }
            }
            .addOnFailureListener { e ->
                Log.e("MapFragment", "Error getting current location", e)
                getLocationPermissions()
            }


    }


    fun getLocationPermissions() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                locationPermissions[0]
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                locationPermissions[1]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 위치 권한이 이미 허용되어 있는 경우입니다.
            if (kakaoMap != null) {
                getCurLocation()
            }
        } else {
            // 위치 권한이 없는 경우, 권한 요청 다이얼로그를 띄웁니다.
            requestPermissions(locationPermissions, PERMISSION_REQUEST_CODE)
        }
    }


    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
    }

    /*override fun onDestroy() {
        super.onDestroy()
        mapView.finish()
    }*/
}

