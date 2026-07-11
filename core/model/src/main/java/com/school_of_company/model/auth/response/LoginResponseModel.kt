package com.school_of_company.model.auth.response

data class LoginResponseModel(
    val memberId: Long,
)

data class TokenResponseModel(
    val accessToken: String,
    val refreshToken: String
)

data class SurveyResponseModel(
    val id: String,
    val userId: String,
    val data: SurveyDataModel,
    val createdAt: String,
    val updatedAt: String
)
data class SurveyDataModel(
    val genres: List<String>,
    val artists: List<String>,
    val sadMoodOption: String,
    val happyMoodOption: String
)
