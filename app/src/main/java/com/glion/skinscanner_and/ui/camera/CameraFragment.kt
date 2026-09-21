package com.glion.skinscanner_and.ui.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glion.skinscanner_and.BuildConfig
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.FragmentCameraBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.base.BaseFragment
import com.glion.skinscanner_and.util.LogUtil
import com.glion.skinscanner_and.util.Utility
//import com.glion.skinscanner_and.util.admob.AdmobInterface
//import com.glion.skinscanner_and.util.admob.AdmobUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding, MainActivity>(R.layout.fragment_camera), OnClickListener {
    private val viewModel: CameraViewModel by viewModels()
    private lateinit var mCameraProvider: ProcessCameraProvider
    companion object {
        var isBackCamera = true
    }

    private var mImageCapture: ImageCapture? = null
    private lateinit var mCameraExecutor: ExecutorService

    private var earnedReward: String = ""

    private var movedAction: CameraFragmentDirections.ActionCameraFragmentToResultFragment? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // onCreate 단계에서 광고 로드
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

        mCameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()

        with(mBinding) {
            btnClose.setOnClickListener(this@CameraFragment)
            btnChangeCamera.setOnClickListener(this@CameraFragment)
            btnCapture.setOnClickListener(this@CameraFragment)
            tvReCapture.setOnClickListener(this@CameraFragment)
            tvDoAnalyze.setOnClickListener(this@CameraFragment)
        }
        observeUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mCameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFeature = ProcessCameraProvider.getInstance(mContext)
        cameraProviderFeature.addListener({
            mCameraProvider = cameraProviderFeature.get()
            bindCamera()
        }, ContextCompat.getMainExecutor(mContext))
        mBinding.clCamera.visibility = View.VISIBLE
        mBinding.clPreview.visibility = View.GONE
    }

    private fun bindCamera() {
        mCameraProvider.unbindAll()
        try {
            val preview = Preview.Builder()
                .build().also {
                    it.setSurfaceProvider(mBinding.previewCamera.surfaceProvider)
                }
            val cameraSelector = if(isBackCamera) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
            mImageCapture = ImageCapture.Builder()
                .setTargetRotation(requireView().display.rotation)
                // 촬영된 이미지 비율 설정
                .apply {
                    val resolutionSelectorBuilder = ResolutionSelector.Builder().apply {
                        setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                    }
                    setResolutionSelector(resolutionSelectorBuilder.build())
                }
                .build()
            mCameraProvider.bindToLifecycle(this, cameraSelector, preview, mImageCapture) // CameraProvider 에 ImageCapture 정보 넘긴다.
        } catch(e: Exception) {
            LogUtil.e("User Case Binding Failed", e)
        }

    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            mBinding.btnClose.id -> {
                findNavController().navigateUp()
            }
            mBinding.btnChangeCamera.id -> {
                changeCamera()
            }
            mBinding.btnCapture.id -> {
                Utility.deleteImage(mContext) // 저장된 비트맵 이미지 제거
                takePhoto()
            }
            mBinding.tvReCapture.id -> {
                Utility.deleteImage(mContext) // 저장된 비트맵 이미지 제거
                startCamera()
            }
            mBinding.tvDoAnalyze.id -> {
                showProgress(mContext.getString(R.string.wait_for_process_image))
                viewModel.doCancerAnalyze()
            }
        }
    }

    private fun takePhoto() {
        if(mImageCapture == null) return
        showProgress()
        mImageCapture?.takePicture(ContextCompat.getMainExecutor(mContext), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                CoroutineScope(Dispatchers.Main).launch {
                    val croppedBitmap = withContext(Dispatchers.Default) {
                        val bitmap = Bitmap.createBitmap(image.toBitmap(), 0, 0, image.width, image.height, Matrix().also{ it.setRotate(90F) }, true)
                        image.close()
                        cropImage(bitmap)
                    }
                    Glide.with(mContext).load(croppedBitmap).apply(
                        // 캐시에 저장된 이전 이미지를 재활용 하지 않도록 처리한다
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                    ).into(mBinding.ivPreview)
                    mBinding.clCamera.visibility = View.GONE
                    mBinding.clPreview.visibility = View.VISIBLE
                    hideProgress()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                showToast(mContext.getString(R.string.fail_capture))
                hideProgress()
                mBinding.clCamera.visibility = View.VISIBLE
                mBinding.clPreview.visibility = View.GONE
            }
        })
    }

    /**
     * 전후 카메라 전환
     */
    private fun changeCamera() {
        isBackCamera = !isBackCamera
        bindCamera()
    }

    private fun cropImage(originBitmap: Bitmap): Bitmap {
        with(mBinding) {
            val heightOriginal = previewCamera.height
            val widthOriginal = previewCamera.width
            val heightFrame = vArea.height
            val widthFrame = vArea.width
            val leftFrame = vArea.left
            val topFrame = vArea.top
            val heightReal = originBitmap.height
            val widthReal = originBitmap.width
            val widthFinal = widthFrame * widthReal / widthOriginal
            val heightFinal = heightFrame * heightReal / heightOriginal
            val leftFinal = leftFrame * widthReal / widthOriginal
            val topFinal = topFrame * heightReal / heightOriginal
            val croppedBitmap = Bitmap.createBitmap(originBitmap, leftFinal, topFinal, widthFinal, heightFinal)
            Utility.saveBitmapInCache(croppedBitmap, mContext)

            return croppedBitmap
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when(uiState) {
                        is CameraUiState.OnLoading -> {  }
                        is CameraUiState.OnProcessing -> {
                            hideProgress()
                            mBinding.vAdDim.visibility = View.VISIBLE
//                            AdmobUtil.showAd()
                        }
                        is CameraUiState.OnError -> {
                            showToast(uiState.msg)
                            startCamera()
                        }
                        is CameraUiState.OnSuccess -> {
                            hideProgress()
                            movedAction = CameraFragmentDirections.actionCameraFragmentToResultFragment(uiState.analyzeResult.cancerType, uiState.analyzeResult.percent)
                            findNavController().navigate(movedAction!!)
                        }
                    }
                }
            }
        }
    }
}