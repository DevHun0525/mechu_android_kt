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
import io.github.devhun0525.mechu.R
import io.github.devhun0525.mechu.data.KakaoApiData
import io.github.devhun0525.mechu.kakaomap.KakaoLocalApiService
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
    val KEYWORD_URL: String = "https://dapi.kakao.com/v2/local/search/keyword"
    val CATEGORY_URL: String = "https://dapi.kakao.com/v2/local/search/category"

    // 1. 로깅 인터셉터 생성 (로그를 상세히 보기 위해 LEVEL_BODY 설정)
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 2. OkHttpClient에 인터셉터 추가
    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // retrofit 선언 (BASE URL 설정)
    val retrofit = Retrofit.Builder()
        .baseUrl("https://dapi.kakao.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    // retrofit 객체 생성 (인터페이스를 담아야됨)
    val kakaoApi = retrofit.create(KakaoLocalApiService::class.java)


    //요청할 위치 권한 목록입니다.
    private val locationPermissions = arrayOf<String?>(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun categorySearch(x : Double, y : Double, radius : Int){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = kakaoApi.searchByCategory(
                    apiKey =  KakaoApiData.HTTP_API_KEY,
                    categoryGroupCode = "BK9", // 은행
                    longitude = x,   // 강남역 경도
                    latitude = y,    // 강남역 위도
                    radius = radius,              // 1km 반경
                    rect = "",
                    page = 1,
                    size = 15,
                    sort = "accuracy",
                )

                if (response.isSuccessful) {
                    // 성공! UI 업데이트는 Dispatchers.Main 에서
                    val places = response.body()?.documents
                    places?.forEach {
                        Log.d("KakaoSearch", "장소: ${it.place_name}, 주소: ${it.address_name}")
                    }
                } else {
                    Log.w("KakaoSearch", "호출 실패: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("KakaoSearch", "오류: ", e)
            }
        }
    }

    // REST API 이용 메서드
    private fun getJson(apiUrl : String): JSONObject { //requestURL 설정 후 Kakao-API의 response 값 인 json 을 받아오는 메서드
        var json: String? = ""
        var client = HttpClientBuilder.create().build();
        var getRequest = HttpGet(apiUrl); //Get 메소드 URL 생성
        getRequest.addHeader("Authorization", "KakaoAK " + KakaoApiData.API_KEY); //API KEY 입력
        var getResponse = client.execute(getRequest); // 위에 보낸 request에 대한 response 내용(json)

        var br = BufferedReader(InputStreamReader(getResponse.getEntity().getContent(), "UTF-8"));
        json = br.readLine();


        return JSONObject(json);
    }

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
        showMapView(view)
    }

    private fun showMapView(view: View) {
        // KakaoMapSDK 초기화!!
        KakaoMapSdk.init(requireContext(), KakaoApiData.API_KEY)
        Log.w("hun", "apikey : ${KakaoApiData.API_KEY}")

        mapView = view.findViewById<MapView>(R.id.map_view)
        locationButton = view.findViewById<Button>(R.id.current_location_button)

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
                    val currentLatLng = LatLng.from(location.latitude, location.longitude)
                    kakaoMap?.moveCamera(
                        CameraUpdateFactory.newCenterPosition(currentLatLng),
                        CameraAnimation.from(500, true, true)
                    )
                    // 필요한 경우 마커 추가
//                    kakaoMap?.labelManager?.layer?.addLabel(
//                        LabelOptions.from(currentLatLng).setStyles(R.drawable.icon_location)


                    Log.w("MapFragment", "currentLatLng ${currentLatLng}")

                    categorySearch(location.latitude, location.longitude, 1000)
                    /*// 1. LabelStyles 생성하기 - Icon 이미지 하나만 있는 스타일
                    var styles =
                        kakaoMap?.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.icon_location)));
                    // 2. LabelOptions 생성하기
                    var options = LabelOptions.from(LatLng.from(currentLatLng)).setStyles(styles)
                    // 3. LabelLayer 가져오기 (또는 커스텀 Layer 생성)
                    var layer = kakaoMap?.labelManager?.layer

                    // 4. LabelLayer 에 LabelOptions 을 넣어 Label 생성하기
                    var label = layer?.addLabel(options)*/

                    Log.w("MapFragment", "Current location is null")
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

