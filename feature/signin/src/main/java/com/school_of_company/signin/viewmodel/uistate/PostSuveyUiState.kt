package com.school_of_company.signin.viewmodel.uistate

import com.school_of_company.model.auth.request.EmotionResponseModel
import com.school_of_company.model.auth.request.PostSurveyRequestModel
import com.school_of_company.model.auth.request.PostSurveyWrapperModel
import com.school_of_company.network.dto.auth.reponse.PostSurveyResponse
import com.school_of_company.network.dto.auth.requset.PostSurveyWrapper

interface PostSuveyUiState {
    data object Idle : PostSuveyUiState   // ✅ 추가
    data object Loading : PostSuveyUiState
    data class Success(val data:  PostSurveyWrapperModel) :PostSuveyUiState
    data class Error(val exception: Throwable) : PostSuveyUiState
}