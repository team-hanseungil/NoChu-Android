package com.school_of_company.network.mapper.auth.response

import com.school_of_company.model.auth.response.LoginResponseModel
import com.school_of_company.model.auth.response.SurveyDataModel
import com.school_of_company.model.auth.response.SurveyResponseModel
import com.school_of_company.model.auth.response.TokenResponseModel
import com.school_of_company.network.dto.auth.reponse.LoginResponse
import com.school_of_company.network.dto.auth.reponse.PostSurveyResponse
import com.school_of_company.network.dto.auth.reponse.SurveyDataResponse
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



fun PostSurveyResponse.toModel(): SurveyResponseModel =
    SurveyResponseModel(
        id = this.id,
        userId = this.userId,
        data = this.data.toModel(),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

fun SurveyDataResponse.toModel(): SurveyDataModel =
    SurveyDataModel(
        genres = this.genres,
        artists = this.artists,
        sadMoodOption = this.sadMoodOption,
        happyMoodOption = this.happyMoodOption
    )


