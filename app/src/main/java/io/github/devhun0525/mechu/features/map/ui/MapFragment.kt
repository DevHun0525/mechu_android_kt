package io.github.devhun0525.mechu.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.mapwidget.InfoWindowOptions
import com.kakao.vectormap.mapwidget.component.GuiImage
import com.kakao.vectormap.mapwidget.component.GuiLayout
import com.kakao.vectormap.mapwidget.component.GuiText
import com.kakao.vectormap.mapwidget.component.Orientation
import io.github.devhun0525.mechu.R
import io.github.devhun0525.mechu.features.map.data.model.KakaoApiData


class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var locationButton: Button
    private lateinit var camera_button: Button
    private lateinit var radioGroup: RadioGroup
    private var radioButtonList: MutableList<RadioButton> = mutableListOf()
    private var kakaoMap: KakaoMap? = null
    private val PERMISSION_REQUEST_CODE = 1001
    private lateinit var fusedLocationClient: FusedLocationProviderClient // FusedLocationProviderClient 선언

    private lateinit var takePicturePreviewLauncher: ActivityResultLauncher<Void?>
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>

    private val locationPermissions = arrayOf<String?>(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity()) // FusedLocationProviderClient 초기화

        takePicturePreviewLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            // bitmap은 찍은 사진의 섬네일 이미지입니다.
            // null이 아니라면 성공적으로 사진을 가져온 것입니다.
            if (bitmap != null) {
                // TODO: 이 비트맵(bitmap) 이미지를 사용해서 원하는 작업을 하세요.
                // 예를 들어, 서버로 전송하거나 이미지뷰에 보여줄 수 있습니다.
                Log.d("Camera", "사진 촬영 성공! 비트맵을 받았습니다.")
            } else {
                // 사용자가 카메라 앱을 그냥 닫거나 촬영을 취소한 경우입니다.
                Log.d("Camera", "사진 촬영이 취소되었습니다.")
            }
        }
// 2단계: 권한 요청 런처를 초기화합니다.
        requestCameraPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // 권한을 허용한 경우, 카메라를 실행합니다.
                Log.d("Permission", "카메라 권한 허용됨")
                takePicturePreviewLauncher.launch(null)
            } else {
                // 권한을 거부한 경우, 사용자에게 안내 메시지를 보여줍니다.
                Log.d("Permission", "카메라 권한 거부됨")
                Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
        getLocationPermissions()


    }

    /*override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return ComposeView(requireContext()).apply {
            setContent {
                // FirstScreen(...) EXTRACT FROM HERE
            }
        }
    }*/

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
        camera_button = view.findViewById<Button>(R.id.camera_button)
        locationButton = view.findViewById<Button>(R.id.current_location_button)
        radioGroup = view.findViewById<RadioGroup>(R.id.options_group)

        showMapView(view)
    }

    private fun showMapView(view: View) {
        // KakaoMapSDK 초기화!!
        KakaoMapSdk.init(requireContext(), KakaoApiData.API_KEY)
        Log.w("hun", "apikey : ${KakaoApiData.API_KEY}")

        var optionLength: Int = KakaoApiData.KeywordOptions.entries.size
        /*for(i in 0..optionLength - 1){
            radioButtonList.add(RadioButton(requireContext()))
            radioButtonList[i].text = KakaoApiData.KeywordOptions.entries[i].toString()
            radioButtonList[i].setTextColor(Color.BLACK)
            radioGroup.addView(radioButtonList[i])
        }
*/

        camera_button.setOnClickListener {
//            val cameraManager = CameraManager()
//            cameraManager.startCamera(requireActivity())

            checkCameraPermissionAndLaunch()
        }

        // ✨ 개선된 방식
        KakaoApiData.KeywordOptions.entries.forEach { option ->
            val radioButton = RadioButton(requireContext())
            radioButton.text = option.keyword // 'option'으로 직접 접근!
            radioButton.setTextColor(Color.BLACK)
            radioGroup.addView(radioButton)
        }

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


                    if (kakaoMap != null && kakaoMap?.labelManager != null) {
                        Log.w(
                            "MapFragment",
                            "kakaoMap ${kakaoMap}, kakaoMap ${kakaoMap?.labelManager}, kakaoMap ${kakaoMap?.labelManager?.layer}"
                        )

                        // InfoWindow
                        val body = GuiLayout(Orientation.Horizontal)
                        body.setPadding(20, 20, 20, 18)

                        //only image
                        val bgImage = GuiImage(R.drawable.test, true);
                        bgImage.setFixedArea(7, 7, 7, 7) // 말풍선 이미지 각 모서리의 둥근 부분만큼(7px)은 늘어나지 않도록 고정.
                        body.setBackground(bgImage)

                        val text = GuiText("InfoWindow!")
                        text.setTextSize(30)
                        val myRedColor = 0xFFFF0000 // Fully opaque red
                        text.textColor = myRedColor.toInt()
                        body.addView(text)
                        val options = InfoWindowOptions.from(currentLatLng)
                        options.setBody(body)
                        options.setBodyOffset(0f, -4f) // Body 와 겹치게 -4px 만큼 올려줌.
//                        val options2 = options.setTail(GuiImage(R.drawable.icon_search, false));
                        kakaoMap?.mapWidgetManager?.infoWindowLayer?.addInfoWindow(options)


                        // label
                        /*val styles = LabelStyles.from(LabelStyle.from(R.drawable.icon_heart).setZoomLevel(0),
                        LabelStyle.from(R.drawable.icon_heart).setTextStyles(15, Color.BLACK).setZoomLevel(5),
                        LabelStyle.from(R.drawable.icon_heart).setZoomLevel(10))

                        val style = kakaoMap?.labelManager?.addLabelStyles(styles)
                        val options = LabelOptions.from(currentLatLng).setStyles(style)
                        val layer = kakaoMap?.labelManager?.layer
                        val label = layer?.addLabel(options)
                        label?.changeStyles(LabelStyles.from(LabelStyle.from(R.drawable.icon_camera)));
                        label?.changeText("안녕하세요!")*/
                    }


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
        Log.e("hun", "@@@@@@@@@@@@@@@@@@ ${radioGroup.checkedRadioButtonId}");
        var selectedButton = radioGroup.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
        if (selectedButton != null) {
            Log.e("hun", "@@@@@@@@@@@@@@@@@@ ${selectedButton.text}");
            KakaoApiData.keyword = selectedButton.text.toString()
        }

    }

    /*override fun onDestroy() {
        super.onDestroy()
        mapView.finish()
    }*/// 4단계: 권한을 확인하고 분기 처리하는 함수를 만듭니다.
    private fun checkCameraPermissionAndLaunch() {
        when {
            // 권한이 이미 허용되어 있는 경우
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 바로 카메라를 실행합니다.
                takePicturePreviewLauncher.launch(null)
            }
            // 권한이 필요한 이유를 사용자에게 설명해야 하는 경우 (옵션)
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(requireContext(), "카메라 기능을 사용하려면 권한이 필요합니다.", Toast.LENGTH_SHORT)
                    .show()
                // 다시 권한 요청
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            // 권한이 없는 경우, 권한 요청 런처를 실행합니다.
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}


//@Composable
//fun SampleNavHost(
//    navController: NavHostController
//) {
//    NavHost(navController = navController, startDestination = GlobalData.First.toString()) {
//        composable<GlobalData.First> {
////            FirstScreen(/* ... */) // EXTRACT TO HERE
//        }
//        composable<GlobalData.Second> {
////            SecondScreen(/* ... */)
//        }
//        // ...
//    }
//}
//
//@Composable
//fun FirstScreen(
//    // viewModel: FirstViewModel = viewModel(),
//    viewModel: FirstViewModel = hiltViewModel(),
//    onButtonClick: () -> Unit = {},
//) {
//    // ...
//}
