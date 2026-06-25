package com.school_of_company.signup.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.school_of_company.signup.viewmodel.uistate.SignUpUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val NICKNAME = "nickname"
        private const val PASSWORD = "password"
        private const val RE_PASSWORD = "rePassword"
    }

    private val _signUpUiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    internal val signUpUiState = _signUpUiState.asStateFlow()

    internal val nickname = savedStateHandle.getStateFlow(NICKNAME, "")
    internal val password = savedStateHandle.getStateFlow(PASSWORD, "")
    internal val checkPassword = savedStateHandle.getStateFlow(RE_PASSWORD, "")

    internal fun onNicknameChange(value: String) {
        savedStateHandle[NICKNAME] = value.trim()
    }

    internal fun onPasswordChange(value: String) {
        savedStateHandle[PASSWORD] = value
    }

    internal fun onCheckPasswordChange(value: String) {
        savedStateHandle[RE_PASSWORD] = value
    }
}