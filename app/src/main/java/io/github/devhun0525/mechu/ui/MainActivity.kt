package io.github.devhun0525.mechu

import android.os.Bundle
import android.util.Log
// import android.view.Menu //  이 줄을 삭제하거나 주석 처리합니다.
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.kakao.vectormap.KakaoMapSdk
import io.github.devhun0525.mechu.data.GlobalData
import io.github.devhun0525.mechu.firebase.FirebaseManager

class MainActivity : AppCompatActivity() {
    var log = "MainActivity"

    //    private lateinit var mapView: MapView
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Toolbar 설정
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 커스텀 툴바 설정
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val customToolbarView = layoutInflater.inflate(R.layout.custom_toolbar_layout, null)
        supportActionBar?.customView = customToolbarView
        // 툴바의 너비를 match_parent로 설정하기 위해 LayoutParams를 설정합니다.
        val params = Toolbar.LayoutParams(
            Toolbar.LayoutParams.MATCH_PARENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        )
        customToolbarView.layoutParams = params



        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_main)
        bottomNavigationView.itemIconTintList = null

        // navHost에서 destination을 관리하는 객체
        val navController =
            supportFragmentManager.findFragmentById(R.id.container_main)?.findNavController()
        navController?.let {
            bottomNavigationView.setupWithNavController(it)
        }
        FirebaseApp.initializeApp(this)
        db = Firebase.firestore
        addTestUserToFirestore()
    }


    // onCreateOptionsMenu 메서드를 삭제합니다.

    /**
     * NAVER MAP API SETTING
     */

    /*fun naverMapSetting(){
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NcpKeyClient(GlobalData.apiKey)

        NaverMapSdk.getInstance(this).onAuthFailedListener =
            NaverMapSdk.OnAuthFailedListener { p0 -> Log.e("hun", "authorization : ${p0.message}") }

        Log.w(log, "apikey : ${GlobalData.apiKey}")
    }

    override fun onMapReady(naverMap: NaverMap) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.5666102, 126.9783881)) // 서울시청 위도, 경도
        naverMap.moveCamera(cameraUpdate)

        val marker = Marker()
        marker.position = LatLng(37.5666102, 126.9783881)
        marker.map = naverMap
    }*/

    val TAG = "FirebaseManager"
    //FirebaseFirestore.getInstance()




    fun addTestUserToFirestore(){
        val user = hashMapOf(
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to 1815,
        )

        // Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        /*db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }*/
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
