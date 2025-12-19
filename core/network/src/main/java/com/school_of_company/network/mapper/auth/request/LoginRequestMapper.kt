package com.school_of_company.network.mapper.auth.request

import com.school_of_company.model.auth.request.EmotionResponseModel
import com.school_of_company.model.auth.request.EmotionsModel
import com.school_of_company.model.auth.request.LoginRequestModel
import com.school_of_company.network.dto.auth.requset.LoginRequest
import com.school_of_company.network.dto.reponse.EmotionResponse
import com.school_of_company.network.dto.reponse.Emotions

fun LoginRequestModel.toDto(): LoginRequest =
    LoginRequest(
        nickname = nickname,
        password = password,
        deviceToken = deviceToken,
        deviceId = deviceId,
        osType = osType
    )

fun EmotionResponse.toModel(): EmotionResponseModel =
    EmotionResponseModel(
        imageUrl = imageUrl,
        emotions = emotions.toModel(),
        emotion = emotion,
        comment = comment
    )

fun Emotions.toModel(): EmotionsModel =
    EmotionsModel(
        happy = happy,
        surprise = surprise,
        anger = anger,
        anxiety = anxiety,
        hurt = hurt,
        sad = sad
    )

