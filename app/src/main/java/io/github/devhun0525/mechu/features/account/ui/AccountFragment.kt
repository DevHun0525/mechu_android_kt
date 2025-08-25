package io.github.devhun0525.mechu.fragment

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import io.github.devhun0525.mechu.BuildConfig
import io.github.devhun0525.mechu.R
import io.github.devhun0525.mechu.databinding.FragmentAccountBinding
import io.github.devhun0525.mechu.features.map.data.model.KakaoApiData
import io.github.devhun0525.mechu.features.map.data.source.KakaoMapManager.Companion.loggingInterceptor
import io.github.devhun0525.mechu.kakaomap.KakaoLocalApiService
import okhttp3.OkHttpClient

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        KakaoSdk.init(view.context, KakaoApiData.API_KEY)
        NaverIdLoginSDK.initialize(view.context, BuildConfig.NAVER_CLIENT_ID, BuildConfig.NAVER_CLIENT_SECRET, "mechu")

        view.findViewById<Button>(R.id.kakao_button).setOnClickListener {
            // 카카오톡으로 로그인
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("hun", "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.i("hun", "카카오계정으로 로그인 성공 ${token.accessToken}")
                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(view.context)) {
                UserApiClient.instance.loginWithKakaoTalk(view.context) { token, error ->
                    if (error != null) {
                        Log.e("hun", "카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(view.context, callback = callback)
                    } else if (token != null) {
                        Log.i("hun", "카카오톡으로 로그인 성공 ${token.accessToken}")
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(view.context, callback = callback)
            }
        }

        val launcher = registerForActivityResult<Intent, ActivityResult>(ActivityResultContracts.StartActivityForResult()) { result ->
            when(result.resultCode) {
                RESULT_OK -> {
                    // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                    Log.e("hun", "@@@@@@@@@@@@@ LOGIN SUCCESS @@@@@@@@@@@@@");
//                        binding.tvAccessToken.text = NaverIdLoginSDK.getAccessToken()
//                        binding.tvRefreshToken.text = NaverIdLoginSDK.getRefreshToken()
//                        binding.tvExpires.text = NaverIdLoginSDK.getExpiresAt().toString()
//                        binding.tvType.text = NaverIdLoginSDK.getTokenType()
//                        binding.tvState.text = NaverIdLoginSDK.getState().toString()
                }
                RESULT_CANCELED -> {
                    // 실패 or 에러
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(context, "errorCode:$errorCode, errorDesc:$errorDescription", Toast.LENGTH_SHORT).show()
                }
            }
        }
        view.findViewById<Button>(R.id.naver_button).setOnClickListener {
            NaverIdLoginSDK.authenticate(view.context, launcher)
        }
        view.findViewById<Button>(R.id.google_button).setOnClickListener {

        }
    }

}