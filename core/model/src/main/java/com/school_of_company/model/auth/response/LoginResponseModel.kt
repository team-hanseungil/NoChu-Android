package com.school_of_company.model.auth.response

data class LoginResponseModel(
    val memberId: Long,
)

data class TokenResponseModel(
    val accessToken: String,
    val refreshToken: String
)