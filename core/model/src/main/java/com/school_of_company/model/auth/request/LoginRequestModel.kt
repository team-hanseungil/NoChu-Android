package com.school_of_company.model.auth.request

data class LoginRequestModel(
    val nickname: String,
    val password: String,
    val deviceToken: String,
    val deviceId: String,
    val osType: String = "ANDROID"
)
data class EmotionResponseModel(
    val imageUrl: String,
    val emotions: EmotionsModel,
    val emotion: String,
    val comment: String
)

data class PlaylistResponseModel(
    val id: Long,
    val title: String,
    val imageUrl: String?,
    val tracks: List<TrackModel>
)

data class TrackModel(
    val artists: List<String>,
    val title: String,
    val imageUrl: String?,
    val previewUrl: String,
    val duration: String
)


data class EmotionsModel(
    val happy: Double,
     val surprise: Double,
   val anger: Double,
     val anxiety: Double,
     val hurt: Double,
  val sad: Double
)