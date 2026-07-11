package com.school_of_company.network.datasource.auth

import com.school_of_company.network.dto.auth.reponse.EmotionResponse
import com.school_of_company.network.dto.auth.reponse.PlaylistResponse
import com.school_of_company.network.dto.auth.reponse.PostSurveyResponse
import com.school_of_company.network.dto.auth.reponse.TokenResponse
import com.school_of_company.network.dto.auth.requset.PostSurveyRequest
import com.school_of_company.network.dto.auth.requset.PostSurveyWrapper
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface AuthDataSource {
    fun loginWithSpotify(code: String): Flow<TokenResponse>
    fun refreshToken(refreshToken: String): Flow<TokenResponse>
    fun logout(): Flow<Unit>
    fun signLogout(): Flow<Unit>

    // ⭕ memberId: Long 파라미터 제거
    fun musicRR(): Flow<PlaylistResponse>

    // ⭕ memberId: Long 파라미터 제거
    fun postFace(image: MultipartBody.Part): Flow<EmotionResponse>

    fun postSurvey(body: PostSurveyWrapper): Flow<PostSurveyResponse>
}
