package com.school_of_company.network.api

import com.school_of_company.network.dto.reponse.TokenResponse
import com.school_of_company.network.dto.auth.requset.SpotifyLoginRequest
import com.school_of_company.network.dto.auth.requset.RefreshTokenRequest
import com.school_of_company.network.dto.reponse.EmotionResponse
import com.school_of_company.network.dto.reponse.PlaylistResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface AuthAPI {

    @POST("/auth/spotify")
    suspend fun loginWithSpotify(
        @Body body: SpotifyLoginRequest
    ): TokenResponse

    @POST("/auth/refresh")
    suspend fun refreshToken(
        @Body body: RefreshTokenRequest
    ): TokenResponse

    @DELETE("/api/auth/signout")
    suspend fun logout()

    @DELETE("/api/auth/out")
    suspend fun signLogout()

    @Multipart
    @POST("/api/emotions/{memberId}")
    suspend fun postFace(
        @Path("memberId") memberId: Long,
        @Part image: MultipartBody.Part
    ): EmotionResponse

    @POST("/api/music/{memberId}")
    suspend fun musicRR(
        @Path("memberId") memberId: Long,
    ): PlaylistResponse
}