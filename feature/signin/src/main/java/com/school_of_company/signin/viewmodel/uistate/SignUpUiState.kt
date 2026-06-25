package com.school_of_company.signup.viewmodel.uistate

sealed interface SignUpUiState {
    data object Idle : SignUpUiState
    data object Loading : SignUpUiState
    data object Success : SignUpUiState
    data class Error(val exception: Throwable) : SignUpUiState
}