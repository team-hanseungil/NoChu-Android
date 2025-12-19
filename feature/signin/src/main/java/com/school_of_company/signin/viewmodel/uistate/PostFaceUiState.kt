package com.school_of_company.signin.viewmodel.uistate

import com.school_of_company.model.auth.request.EmotionResponseModel

interface PostFaceUiState {
    data object Idle : PostFaceUiState   // ✅ 추가
    data object Loading : PostFaceUiState
    data class Success(val data: EmotionResponseModel) : PostFaceUiState
    data class Error(val exception: Throwable) : PostFaceUiState
}