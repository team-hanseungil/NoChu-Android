package com.school_of_company.signup.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.school_of_company.data.repository.auth.AuthRepository
import com.school_of_company.model.auth.request.SignUpRequestModel
import com.school_of_company.network.errorHandling
import com.school_of_company.result.asResult
import com.school_of_company.signup.viewmodel.uistate.SignUpUiState // 이제 Idle 포함
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.school_of_company.result.Result

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    companion object {
        private const val NICKNAME = "nickname"
        private const val PASSWORD = "password"
        private const val RE_PASSWORD = "rePassword"
    }

    // 회원가입 전반의 UI 상태
    private val _signUpUiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle) // 🚀 Idle 참조 가능
    internal val signUpUiState = _signUpUiState.asStateFlow()

    // TextField 상태 관리
    internal val nickname = savedStateHandle.getStateFlow(NICKNAME, "")
    internal val password = savedStateHandle.getStateFlow(PASSWORD, "")
    internal val checkPassword = savedStateHandle.getStateFlow(RE_PASSWORD, "")

    // --- 데이터 변경 함수 ---

    internal fun onNicknameChange(value: String) {
        savedStateHandle[NICKNAME] = value.trim()
    }

    internal fun onPasswordChange(value: String) {
        savedStateHandle[PASSWORD] = value
    }

    internal fun onCheckPasswordChange(value: String) {
        savedStateHandle[RE_PASSWORD] = value
    }

    // --- 핵심 로직: 회원가입 ---

    fun signUp() =
        viewModelScope.launch {
            _signUpUiState.value = SignUpUiState.Loading

            val currentNickname = nickname.value
            val currentPassword = password.value
            val currentCheckPassword = checkPassword.value

            // 1. 비밀번호 일치 여부 확인 (클라이언트 측 검증)
            if (currentPassword != currentCheckPassword) {
                _signUpUiState.value = SignUpUiState.PasswordMismatch
                return@launch
            }

            // 2. SignUpRequestModel 생성
            val body = SignUpRequestModel(
                nickname = currentNickname,
                password = currentPassword
            )

            // 3. API 호출
            authRepository.signUp(body = body)
                .asResult()
                .collectLatest { result ->
                    when (result) {
                        is Result.Loading -> _signUpUiState.value =
                            SignUpUiState.Loading

                        is Result.Success -> _signUpUiState.value =
                            SignUpUiState.Success

                        is Result.Error -> {
                            _signUpUiState.value = SignUpUiState.Error(result.exception)
                            result.exception.errorHandling(
                                badRequestAction = { _signUpUiState.value = SignUpUiState.BadRequest },
                                conflictAction = { _signUpUiState.value = SignUpUiState.Conflict }
                            )
                        }
                    }
                }
        }
}