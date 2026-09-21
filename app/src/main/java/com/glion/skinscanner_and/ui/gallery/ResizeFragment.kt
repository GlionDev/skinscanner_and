package com.glion.skinscanner_and.ui.gallery

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.glion.skinscanner_and.BuildConfig
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.FragmentResizeBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.base.BaseFragment
import com.glion.skinscanner_and.util.Utility
//import com.glion.skinscanner_and.util.admob.AdmobInterface
//import com.glion.skinscanner_and.util.admob.AdmobUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ResizeFragment : BaseFragment<FragmentResizeBinding, MainActivity>(R.layout.fragment_resize), OnClickListener{
    private var earnedReward: String = ""
    private val viewModel : ResizeViewModel by viewModels()
    private var movedAction: ResizeFragmentDirections.ActionResizeFragmentToResultFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        AdmobUtil.setListener(object : AdmobInterface {
//            override fun adDismiss() {
//                if(BuildConfig.DEBUG) {
//                    if(earnedReward == "coins") {
//                        hideProgress()
//                        findNavController().navigate(movedAction!!)
//                    }
//                } else {
//                    if(mContext.getString(R.string.reward_type) == earnedReward) { // note : 얻은 보상 타입이 미리 지정한 보상 타입과 같은 경우, 화면 이동
//                        hideProgress()
//                        findNavController().navigate(movedAction!!)
//                    }
//                }
//            }
//
//            override fun getReward(rewardType: String) {
//                earnedReward = rewardType
//            }
//
//            override fun adError() {
//                hideProgress()
//                findNavController().navigate(movedAction!!)
//            }
//        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(mBinding) {
            CoroutineScope(Dispatchers.Main).launch {
                val bitmap = withContext(Dispatchers.Main) {
                    Utility.getImageToBitmap(mContext, mContext.getString(R.string.saved_file_name))
                }
                cropView.setImageBitmap(bitmap)
                tvDoAnalyze.setOnClickListener(this@ResizeFragment)
            }
        }
        observeUiState()
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.tv_do_analyze -> {
                val croppedImage = mBinding.cropView.getCroppedImage()!!
                Utility.saveBitmapInCache(croppedImage, mContext)
                viewModel.doCancerAnalyze()
            }
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when(uiState) {
                        is ResizeUiState.OnLoading -> {

                        }
                        is ResizeUiState.OnProcessing -> {
                            mBinding.vAdDim.visibility = View.VISIBLE
//                            AdmobUtil.showAd()
                        }
                        is ResizeUiState.OnError -> {
                            showToast(uiState.msg)
                        }
                        is ResizeUiState.OnSuccess -> {
                            hideProgress()
                            movedAction = ResizeFragmentDirections.actionResizeFragmentToResultFragment(uiState.analyzeResult.cancerType, uiState.analyzeResult.percent)
                            findNavController().navigate(movedAction!!)
                        }
                    }
                }
            }
        }
    }
}