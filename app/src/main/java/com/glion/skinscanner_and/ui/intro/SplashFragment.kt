package com.glion.skinscanner_and.ui.intro

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glion.skinscanner_and.BuildConfig
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.FragmentSplashBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.base.BaseFragment
import com.glion.skinscanner_and.ui.dialog.CommonDialog
import com.glion.skinscanner_and.ui.dialog.CommonDialogType
import com.glion.skinscanner_and.util.LogUtil
import com.glion.skinscanner_and.util.RootCheck
import com.glion.skinscanner_and.util.Utility
//import com.glion.skinscanner_and.util.admob.AdmobUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding, MainActivity>(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
//        // 광고 초기화
//        AdmobUtil.loadAd(mParentActivity) {
//            // note : 광고 초기화가 이뤄진 뒤에 버전체크 진행
//            observeState()
//
//            if(BuildConfig.DEBUG) {
//                if(Utility.checkNetworkStatus(mContext))
//                    viewModel.checkVersion()
//            } else {
//                if(RootCheck(mContext).isRooted()) {
//                    showDialog(
//                        dialogType = CommonDialogType.OneButton,
//                        title = mContext.getString(R.string.notice),
//                        contents = mContext.getString(R.string.maybe_rooted_app),
//                        listener = object : CommonDialog.DialogButtonClick {
//                            override fun singleBtnClick() {
//                                super.singleBtnClick()
//                                mParentActivity.finish()
//                            }
//                        }
//                    )
//                } else {
//                    // note : 인터넷이 연결되어있을때만 앱 버전 체크
//                    if(Utility.checkNetworkStatus(mContext))
//                        viewModel.checkVersion()
//                }
//            }
//        }
    }

//    private fun observeState() {
//        lifecycleScope.launch {
//            viewModel.uiState.collect { state ->
//                when(state) {
//                    is SplashState.OnLoading -> {
//
//                    }
//                    is SplashState.OnUpdate -> {
//                        when(state.flag) {
//                            0 -> { // note : 업데이트 하지 않음
//                                viewModel.exchangeKey()
//                            }
//                            1 -> {
//                                // note : 선택업데이트
//                                showDialog(
//                                    dialogType = CommonDialogType.TwoButton,
//                                    title = mContext.getString(R.string.default_dialog_title),
//                                    contents = mContext.getString(R.string.update_dialog_contents),
//                                    leftBtnStr = mContext.getString(R.string.update_later),
//                                    rightBtnStr = mContext.getString(R.string.update_now),
//                                    listener = object : CommonDialog.DialogButtonClick {
//                                        override fun rightBtnClick() {
//                                            super.rightBtnClick()
//                                            Utility.goMarket(mContext)
//                                        }
//                                    }
//                                )
//                            }
//                            2 -> {
//                                // note : 강제업데이트
//                                showDialog(
//                                    dialogType = CommonDialogType.OneButton,
//                                    title = mContext.getString(R.string.default_dialog_title),
//                                    contents = mContext.getString(R.string.update_force_dialog_contents),
//                                    singleBtnStr = mContext.getString(R.string.update_now),
//                                    listener = object : CommonDialog.DialogButtonClick {
//                                        override fun singleBtnClick() {
//                                            super.singleBtnClick()
//                                            Utility.goMarket(mContext)
//                                        }
//                                    }
//                                )
//                            }
//                        }
//                    }
//                    is SplashState.OnKeyExChange -> {
//                        if(state.isComplete) {
//                            viewModel.checkFile(Utility.getFileHashFromAssets(mContext, mContext.getString(R.string.model_name)))
//                        } else {
//                            // note : 키 교환 완료 X
//                            //  -> 앱을 종료해야 할지, 내장된 모델로 사용해야 할지 고민
//                        }
//                    }
//                    is SplashState.OnCheckFile -> { // 모델 다운로드가 필요하지 않을때(파일 확인 완료)
//                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
//                    }
//                    is SplashState.OnModelDownload -> {
//                        // TODO : S3 API 사용해서 모델 다운로드 진행
//                    }
//                    is SplashState.OnError -> {
//                        // TODO : 모델 Hash 비교해서 다를 경우 다운로드 필요
//                        when(state.type) {
//                            SplashViewModel.VERSION_CHECK -> {
//                                // TODO : 파이어베이스 crashlytics 로그 전송 - fail get version in rtdb
//                                // TODO : 서버와 키 교환 필요
//                                LogUtil.e("Error Getting Data :: Version Check", state.error)
//                            }
//                            SplashViewModel.EXCHANGE_KEY -> { // note : 키 교환 실패했을 경우
//                                // TODO : 모델 파일 존재 확인 후 모델이 없다면 Dialog 안내 후 앱 종료
//                                LogUtil.e("Error Getting Data :: exchangeKey", state.error)
//                            }
//                            SplashViewModel.MODEL_DOWNLOAD -> {
//                                // TODO : 모델 다운로드 실패. 이전 모델이 사용됨을 Dialog 로 안내
//                                // TODO : 메인 화면 이동
//                                LogUtil.e("Error Getting Data :: Model Download", state.error)
//                            }
//                        }
//                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
//                    }
//                }
//            }
//        }
//    }

    /**
     * Amazon S3 API 모델 다운로드
     */
    private fun downloadModel() {

    }
}