package com.school_of_company.model.auth.request

data class SignUpRequestModel(
    val nickname: String,
    val password: String,
)