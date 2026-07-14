package com.school_of_company.network.dto.auth.reponse

import com.school_of_company.model.auth.response.SurveyDataModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "memberId") val memberId: Long,
)

@JsonClass(generateAdapter = true)
data class TokenResponse(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String,
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
    @Json(name = "sad") val sad: Double
)

@JsonClass(generateAdapter = true)
data class PlaylistResponse(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "imageUrl") val imageUrl: String?,
    @Json(name= "spotifyUrl") val spotifyUrl: String?,
    @Json(name = "tracks") val tracks: List<Track>
)

@JsonClass(generateAdapter = true)
data class Track(
    @Json(name = "artists") val artists: List<String>,
    @Json(name = "title") val title: String,
    @Json(name = "imageUrl") val imageUrl: String?,
    @Json(name = "spotifyUrl") val previewUrl: String,
    @Json(name = "duration") val duration: String
)

@JsonClass(generateAdapter = true)
data class PostSurveyResponse(
    @Json(name = "id") val id: String,
    @Json(name = "userId") val userId: String,
    @Json(name = "data") val data: SurveyDataResponse,
    @Json(name = "createdAt") val createdAt: String,
    @Json(name = "updatedAt") val updatedAt: String
)

@JsonClass(generateAdapter = true)
data class SurveyDataResponse(
    @Json(name = "genres") val genres: List<String>,
    @Json(name = "artists") val artists: List<String>,
    @Json(name = "sadMoodOption") val sadMoodOption: String,
    @Json(name = "happyMoodOption") val happyMoodOption: String
)
