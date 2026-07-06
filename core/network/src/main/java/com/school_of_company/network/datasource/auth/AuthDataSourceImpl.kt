package com.school_of_company.network.datasource.auth

import com.school_of_company.network.api.AuthAPI
import com.school_of_company.network.dto.auth.requset.SpotifyLoginRequest
import com.school_of_company.network.dto.auth.requset.RefreshTokenRequest
import com.school_of_company.network.dto.auth.reponse.TokenResponse
import com.school_of_company.network.dto.auth.reponse.EmotionResponse
import com.school_of_company.network.dto.auth.reponse.PlaylistResponse
import com.school_of_company.network.util.performApiRequest
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val authAPI: AuthAPI
) : AuthDataSource {

    override fun loginWithSpotify(code: String): Flow<TokenResponse> =
        performApiRequest { authAPI.loginWithSpotify(SpotifyLoginRequest(code = code)) }

    override fun refreshToken(refreshToken: String): Flow<TokenResponse> =
        performApiRequest { authAPI.refreshToken(RefreshTokenRequest(refreshToken = refreshToken)) }

    override fun logout(): Flow<Unit> =
        performApiRequest { authAPI.logout() }

    override fun signLogout(): Flow<Unit> =
        performApiRequest { authAPI.signLogout() }

    override fun musicRR(): Flow<PlaylistResponse> =
        performApiRequest { authAPI.musicRR() }

    override fun postFace(image: MultipartBody.Part): Flow<EmotionResponse> =
        performApiRequest { authAPI.postFace(image = image) }
}