package com.glion.skinscanner_and.ui.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glion.skinscanner_and.BuildConfig
import com.glion.skinscanner_and.data.api.data.RequestCheckFile
import com.glion.skinscanner_and.data.api.data.RequestExchangeKey
import com.glion.skinscanner_and.data.api.repository.NetworkDatasource
import com.glion.skinscanner_and.util.CryptoUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val networkDatasource: NetworkDatasource
) : ViewModel() {
    companion object {
        const val VERSION_CHECK = 1
        const val EXCHANGE_KEY = 2
        const val CHECK_FILE = 3
        const val MODEL_DOWNLOAD = 4
    }

    private val _uiState = MutableStateFlow<SplashState>(SplashState.OnLoading)
    val uiState: StateFlow<SplashState> = _uiState

//    /**
//     *  버전 체크
//     */
//    fun checkVersion() {
//        viewModelScope.launch {
//            networkDatasource.getVersion()
//                .onStart { _uiState.emit(SplashState.OnLoading) }
//                .catch { e -> _uiState.emit(SplashState.OnError(e, VERSION_CHECK)) }
//                .collect { flag ->
//                    if(flag != null)
//                        _uiState.emit(SplashState.OnUpdate(flag))
//                    else
//                        _uiState.emit(SplashState.OnError(null, VERSION_CHECK))
//                }
//        }
//    }

//    /**
//     * 서버와 키 교환
//     */
//    fun exchangeKey() {
//        viewModelScope.launch {
//            CryptoUtils.rsaInitialize()
//            val request = RequestExchangeKey(CryptoUtils.publicKeyToPEM())
//            if(BuildConfig.DEBUG) CryptoUtils.testRSAEncryption()
//            networkDatasource.exchangeKey(request)
//                .onStart { _uiState.emit(SplashState.OnLoading) }
//                .catch { e -> _uiState.emit(SplashState.OnError(e, EXCHANGE_KEY)) }
//                .collect {
//                    _uiState.emit(SplashState.OnKeyExChange(true))
//                }
//        }
//    }

//    /**
//     * 파일 해시값 비교
//     */
//    fun checkFile(fileHash: String) {
//        viewModelScope.launch {
//            networkDatasource.checkFile(RequestCheckFile(fileHash))
//                .onStart { _uiState.emit(SplashState.OnLoading) }
//                .catch { e -> _uiState.emit(SplashState.OnError(e, CHECK_FILE)) }
//                .collect { result ->
//                    if(result) { // 모델 다운로드가 필요할 경우
//                        _uiState.emit(SplashState.OnModelDownload)
//                    } else { // 모델 다운로드가 필요하지 않을 때
//                        _uiState.emit(SplashState.OnCheckFile(true))
//                    }
//                }
//        }
//    }
}

sealed interface SplashState {
    data object OnLoading : SplashState
    data class OnUpdate(val flag: Int) : SplashState
    data class OnKeyExChange(val isComplete: Boolean) : SplashState
    data class OnCheckFile(val isComplete: Boolean) : SplashState
    data object OnModelDownload : SplashState
    data class OnError(val error: Throwable?, val type: Int) : SplashState
}