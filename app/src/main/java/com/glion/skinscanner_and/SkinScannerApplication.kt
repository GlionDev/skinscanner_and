package com.glion.skinscanner_and

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.glion.skinscanner_and.util.LogUtil
//import com.google.android.gms.ads.MobileAds
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SkinScannerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
        KakaoMapSdk.init(this, BuildConfig.KAKAO_MAP_KEY)
        LogUtil.d(KakaoSdk.keyHash)
//        MobileAds.initialize(this)
    }
}