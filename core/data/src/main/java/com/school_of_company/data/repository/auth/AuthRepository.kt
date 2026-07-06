package com.school_of_company.data.repository.auth

import com.school_of_company.model.auth.request.EmotionResponseModel
import com.school_of_company.model.auth.request.PlaylistResponseModel
import com.school_of_company.model.auth.response.TokenResponseModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

// AuthRepository.kt 파일을 찾아서 아래와 같이 수정해 주세요.

interface AuthRepository {
    fun loginWithSpotify(code: String): Flow<TokenResponseModel>
    fun refreshToken(refreshToken: String): Flow<TokenResponseModel>
    fun logout(): Flow<Unit>

    fun signLogout(): Flow<Unit>

    fun musicRR(): Flow<PlaylistResponseModel>

    fun postFace(image: MultipartBody.Part): Flow<EmotionResponseModel>

    fun getRefreshToken(): Flow<String>

    fun getAccessToken(): Flow<String>

    suspend fun saveToken(token: TokenResponseModel)

    suspend fun deleteTokenData()
}