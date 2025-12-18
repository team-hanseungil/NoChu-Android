package com.school_of_company.network.dto.reponse

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String,
    @Json(name = "accessTokenExpiresIn") val accessTokenExpiresIn: String,
    @Json(name = "refreshTokenExpiresIn") val refreshTokenExpiresIn: String,
)


@JsonClass(generateAdapter = true)
data class EmotionResponse(
    @Json(name = "imageUrl") val imageUrl: String,
    @Json(name = "emotions") val emotions: Emotions,
    @Json(name = "emotion") val emotion: String,
    @Json(name = "comment") val comment: String
)

@JsonClass(generateAdapter = true)
data class Emotions(
    @Json(name = "happy") val happy: Double,
    @Json(name = "surprise") val surprise: Double,
    @Json(name = "anger") val anger: Double,
    @Json(name = "anxiety") val anxiety: Double,
    @Json(name = "hurt") val hurt: Double,
    @Json(name = "sad") val sad: Double
)
