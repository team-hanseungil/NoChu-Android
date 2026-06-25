package com.school_of_company.network.datasource.auth

import com.school_of_company.network.dto.reponse.TokenResponse
import com.school_of_company.network.dto.reponse.EmotionResponse
import com.school_of_company.network.dto.reponse.PlaylistResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface AuthDataSource {

    fun loginWithSpotify(code: String): Flow<TokenResponse>

    fun refreshToken(refreshToken: String): Flow<TokenResponse>

    fun logout(): Flow<Unit>

    fun signLogout(): Flow<Unit>

    fun musicRR(memberId: Long): Flow<PlaylistResponse>

    fun postFace(memberId: Long, image: MultipartBody.Part): Flow<EmotionResponse>
}