package com.school_of_company.signup.viewmodel.uistate

import com.school_of_company.result.Result // 또는 해당 Result 클래스의 정확한 경로

/**
 * 회원가입 화면의 UI 상태를 정의하는 sealed class입니다.
 */
sealed interface SignUpUiState {

    // 🚀 Unresolved reference 'Idle' 오류 해결: 초기 상태를 정의합니다.
    data object Idle : SignUpUiState

    data object Loading : SignUpUiState

    // 회원가입 성공
    data object Success : SignUpUiState

    // 클라이언트 측 오류: 비밀번호 불일치
    data object PasswordMismatch : SignUpUiState

    // 서버 측 오류: 닉네임 중복 등 (HTTP 409)
    data object Conflict : SignUpUiState

    // 서버 측 오류: 잘못된 요청 (HTTP 400)
    data object BadRequest : SignUpUiState

    // 일반적인 네트워크/통신 오류
    data class Error(val exception: Throwable) : SignUpUiState
}