package com.school_of_company.network.mapper.auth.response

import com.school_of_company.model.auth.response.LoginResponseModel
import com.school_of_company.model.auth.response.TokenResponseModel
import com.school_of_company.network.dto.auth.reponse.LoginResponse
import com.school_of_company.network.dto.auth.reponse.TokenResponse

fun LoginResponse.toModel(): LoginResponseModel =
    LoginResponseModel(
        memberId = memberId
    )

fun TokenResponse.toTokenResponseModel(): TokenResponseModel =
    TokenResponseModel(
        accessToken = accessToken,
        refreshToken = refreshToken
    )