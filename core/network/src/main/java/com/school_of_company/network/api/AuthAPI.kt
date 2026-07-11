package com.school_of_company.network.api

import com.school_of_company.network.dto.auth.reponse.TokenResponse
import com.school_of_company.network.dto.auth.requset.SpotifyLoginRequest
import com.school_of_company.network.dto.auth.requset.RefreshTokenRequest
import com.school_of_company.network.dto.auth.reponse.EmotionResponse
import com.school_of_company.network.dto.auth.reponse.PlaylistResponse
import com.school_of_company.network.dto.auth.reponse.PostSurveyResponse
import com.school_of_company.network.dto.auth.requset.PostSurveyRequest
import com.school_of_company.network.dto.auth.requset.PostSurveyWrapper
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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

    // ⭕ {memberId}와 @Path 제거
    @Multipart
    @POST("/emotions")
    suspend fun postFace(
        @Part image: MultipartBody.Part
    ): EmotionResponse

    // ⭕ {memberId}와 @Path 제거
    @POST("/api/music")
    suspend fun musicRR(): PlaylistResponse

    @POST("/preferences")
    suspend fun postSurVey(
        @Body body: PostSurveyWrapper
    )  : PostSurveyResponse
}