package com.school_of_company.signin.viewmodel

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.school_of_company.Regex.isValidId
import com.school_of_company.Regex.isValidPassword
import com.school_of_company.data.repository.auth.AuthRepository
import com.school_of_company.data.repository.local.LocalRepository
import com.school_of_company.model.auth.request.LoginRequestModel
import com.school_of_company.model.auth.request.SignUpRequestModel
import com.school_of_company.network.errorHandling
import com.school_of_company.network.util.DeviceIdManager
import com.school_of_company.post.viewmodel.uiState.ImageUpLoadUiState
import com.school_of_company.result.asResult
import com.school_of_company.result.Result
import com.school_of_company.signin.view.getMultipartFile
import com.school_of_company.signin.viewmodel.uistate.MusicRR
import com.school_of_company.signin.viewmodel.uistate.PostFaceUiState
import com.school_of_company.signin.viewmodel.uistate.SaveTokenUiState
import com.school_of_company.signin.viewmodel.uistate.SignInUiState
import com.school_of_company.signin.viewmodel.uistate.SignUpUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle,
    private val localRepository: LocalRepository
) : ViewModel() {
    companion object {
        private const val ID = "id"
        private const val PASSWORD = "password"
    }

    // =========================== 상태 ===========================

    internal var id = savedStateHandle.getStateFlow(key = ID, initialValue = "")
    internal var password = savedStateHandle.getStateFlow(key = PASSWORD, initialValue = "")

    private val _signInUiState = MutableStateFlow<SignInUiState>(SignInUiState.Loading)
    internal val signInUiState = _signInUiState.asStateFlow()

    private val _signUpUiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Loading)
    internal val signUpUiState = _signUpUiState.asStateFlow()

    // ========================= 로그인 로직 ==========================

    private val _postFaceUiState = MutableStateFlow<PostFaceUiState>(PostFaceUiState.Idle)
    val postFaceUiState = _postFaceUiState.asStateFlow()

    private val _musicRR = MutableStateFlow<MusicRR>(MusicRR.Loading)
    val musicRR = _musicRR.asStateFlow()

    internal fun muicRR(memberId: Long) = viewModelScope.launch {
        authRepository.musicRR(memberId)
            .asResult()
            .collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        _musicRR.value = MusicRR.Loading
                    }

                    is Result.Success -> {
                        _musicRR.value = MusicRR.Success(result.data)

                        Log.d(TAG, "muicRR: ${result.data}")
                    }

                    is Result.Error -> {
                        _musicRR.value = MusicRR.Error(result.exception)
                    }
                }
            }
    }


    internal fun postFace(memberId: Long, context: Context, image: Uri) = viewModelScope.launch {
        val multipartFile = getMultipartFile(context, image)
            ?: run {
                _postFaceUiState.value = PostFaceUiState.Error(
                    IllegalStateException("이미지 파일 변환 실패")
                )
                return@launch
            }

        authRepository.postFace(memberId, multipartFile)
            .asResult()
            .collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        _postFaceUiState.value = PostFaceUiState.Loading
                    }

                    is Result.Success -> {
                        _postFaceUiState.value = PostFaceUiState.Success(result.data)
                    }

                    is Result.Error -> {
                        _postFaceUiState.value = PostFaceUiState.Error(result.exception)
                        // ✅ 여기서 throw 하면 앱 죽을 수 있음 -> 제거
                    }
                }
            }
    }
    fun resetPostFaceState() {
        _postFaceUiState.value = PostFaceUiState.Idle
    }

    internal fun login() = viewModelScope.launch {
        _signInUiState.value = SignInUiState.Loading

        val nicknameValue = id.value
        val passwordValue = password.value



        val body = SignUpRequestModel(
            nickname = nicknameValue,
            password = passwordValue,
        )

        authRepository.signIn(
            body = body
        )
            .asResult()
            .collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        _signInUiState.value = SignInUiState.Loading
                    }
                    is Result.Success -> {

                        Log.d("LoginViewModel", "Login success, saving token...")
                        Log.d("LoginViewModel", "Token data: ${result.data}")
                        _signInUiState.value = SignInUiState.Success(result.data.memberId)

                        authRepository.saveToken(result.data)
                    }
                    is Result.Error -> {
                        Log.e("LoginViewModel", "Login failed: ${result.exception}")
                        _signInUiState.value = SignInUiState.Error(result.exception)
                        result.exception.errorHandling(
                            notFoundAction = { _signInUiState.value = SignInUiState.NotFound } ,
                            badRequestAction = { _signInUiState.value = SignInUiState.BadRequest }
                        )
                    }
                }
            }
    }

    // ========================= 회원가입 로직 =========================

    internal fun signUp(body: SignUpRequestModel) = viewModelScope.launch {
        _signUpUiState.value = SignUpUiState.Loading

        authRepository.signUp(body)
            .asResult()
            .collectLatest { result ->
                when (result) {
                    is Result.Success -> _signUpUiState.value = SignUpUiState.Success
                    is Result.Loading -> _signUpUiState.value = SignUpUiState.Loading
                    is Result.Error -> {
                        _signUpUiState.value = SignUpUiState.Error(result.exception)
                        result.exception.errorHandling(
                            conflictAction = { _signUpUiState.value = SignUpUiState.Conflict },
                            unauthorizedAction = { _signUpUiState.value = SignUpUiState.Unauthorized },
                        )
                    }
                }
            }
    }

    // ======================= 데이터 변경 핸들러 ========================

    internal fun onIdChange(value: String) {
        savedStateHandle[ID] = value
    }

    internal fun onPasswordChange(value: String) {
        savedStateHandle[PASSWORD] = value
    }
}

