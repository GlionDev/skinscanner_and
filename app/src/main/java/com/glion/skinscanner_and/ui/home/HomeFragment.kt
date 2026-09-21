package com.glion.skinscanner_and.ui.home

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.FragmentHomeBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.base.BaseFragment
import com.glion.skinscanner_and.ui.camera.CameraFragment
import com.glion.skinscanner_and.ui.dialog.CommonDialog
import com.glion.skinscanner_and.ui.dialog.CommonDialogType
import com.glion.skinscanner_and.util.Utility
import com.glion.skinscanner_and.util.extension.checkPermission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, MainActivity>(R.layout.fragment_home), OnClickListener {
    companion object {
        private const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES

        private const val READ_MEDIA_VISUAL_USER_SELECTED = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
    }

    private val requestCameraPermission = registerForActivityResult(RequestPermission()) { isGranted ->
        if(isGranted) {
            findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
        } else {
            showDeniedCameraPermissionDialog()
        }
    }

    private val requestGalleryPermissions = registerForActivityResult(RequestMultiplePermissions()) { result ->
        if(result.values.any { isAllow -> !isAllow }) { // note : 권한을 허용하지 않았다면
            showDeniedMediaPermissionDialog()
        } else {
            findNavController().navigate(R.id.action_homeFragment_to_galleryFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.llOpenCamera.setOnClickListener(this@HomeFragment)
        mBinding.llOpenGallery.setOnClickListener(this@HomeFragment)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            mBinding.llOpenCamera.id -> {
                checkPermissionCamera()
            }

            // TODO : 권한 허용 수정 필요
            mBinding.llOpenGallery.id -> {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                        handleClickOpenGallery(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED))
                    }

                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                        handleClickOpenGallery(arrayOf(READ_MEDIA_IMAGES))
                    }

                    else -> {
                        handleClickOpenGallery(arrayOf(READ_EXTERNAL_STORAGE))
                    }
                }
            }
        }
    }

    private fun checkPermissionCamera() {
        when {
            mContext.checkPermission(Manifest.permission.CAMERA) -> {
                CameraFragment.isBackCamera = true
                findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(mParentActivity, Manifest.permission.CAMERA) -> {
                showDeniedCameraPermissionDialog()
            }
            else -> {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun handleClickOpenGallery(permissions: Array<String>) {
        val rejectedPermissions: MutableList<String> = mutableListOf()
        for(pm in permissions) {
            when {
                mContext.checkPermission(pm) -> {  }
                shouldShowRequestPermissionRationale(pm) -> {
                    showDeniedMediaPermissionDialog()
                    return
                }
                else -> {
                    rejectedPermissions.add(pm)
                }
            }
        }
        if(rejectedPermissions.isEmpty()) { // note : 허용하지 않은 권한이 없을때
            findNavController().navigate(R.id.action_homeFragment_to_galleryFragment)
        } else {
            requestGalleryPermissions.launch(rejectedPermissions.toTypedArray())
        }
    }

    /**
     * 카메라 권한 허옹하지 않았을때 팝업
     */
    private fun showDeniedCameraPermissionDialog() {
        showDialog(
            dialogType = CommonDialogType.TwoButton,
            title = mContext.getString(R.string.default_dialog_title),
            contents = mContext.getString(R.string.permission_dialog_contents_camera),
            listener = object : CommonDialog.DialogButtonClick {
                override fun leftBtnClick() {
                    super.leftBtnClick()
                    showToast(mContext.getString(R.string.denied_permission_camera))
                }

                override fun rightBtnClick() {
                    super.rightBtnClick()
                    Utility.goSetting(mContext)
                }
            }
        )
    }

    /**
     * 사진 및 미디어 권한 허용하지 않았을때 팝업
     */
    private fun showDeniedMediaPermissionDialog() {
        showDialog(
            dialogType = CommonDialogType.TwoButton,
            title = mContext.getString(R.string.default_dialog_title),
            contents = mContext.getString(R.string.permission_dialog_contents_gallery),
            listener = object : CommonDialog.DialogButtonClick {
                override fun leftBtnClick() {
                    super.leftBtnClick()
                    showToast(mContext.getString(R.string.denied_permission_gallery))
                }

                override fun rightBtnClick() {
                    super.rightBtnClick()
                    Utility.goSetting(mContext)
                }
            }
        )
    }
}