package com.school_of_company.network.dto.auth.requset

import com.school_of_company.network.dto.auth.reponse.SurveyDataResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "nickname") val nickname: String,
    @Json(name = "password") val password: String,
    @Json(name = "deviceToken") val deviceToken: String,
    @Json(name = "deviceId") val deviceId: String,
    @Json(name = "osType") val osType: String = "ANDROID"
)

@JsonClass(generateAdapter = true)
data class SpotifyLoginRequest(
    @Json(name = "code") val code: String
)

@JsonClass(generateAdapter = true)
data class RefreshTokenRequest(
    @Json(name = "refreshToken") val refreshToken: String
)

@JsonClass(generateAdapter = true)
data class PostSurveyRequest(
    @Json(name = "genres") val genres: List<String>,
    @Json(name = "artists") val artists: List<String>,
    @Json(name = "sadMoodOption") val sadMoodOption: String,
    @Json(name = "happyMoodOption") val happyMoodOption: String
)
@JsonClass(generateAdapter = true)
data class PostSurveyWrapper(
    @Json(name = "data") val data: PostSurveyRequest
)