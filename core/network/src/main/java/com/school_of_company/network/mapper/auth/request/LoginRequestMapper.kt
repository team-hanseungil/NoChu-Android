package com.school_of_company.network.mapper.auth.request

import com.school_of_company.model.auth.request.EmotionResponseModel
import com.school_of_company.model.auth.request.EmotionsModel
import com.school_of_company.model.auth.request.LoginRequestModel
import com.school_of_company.model.auth.request.PlaylistResponseModel
import com.school_of_company.model.auth.request.TrackModel
import com.school_of_company.network.dto.auth.requset.LoginRequest
import com.school_of_company.network.dto.reponse.EmotionResponse
import com.school_of_company.network.dto.reponse.Emotions
import com.school_of_company.network.dto.reponse.PlaylistResponse
import com.school_of_company.network.dto.reponse.Track

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

fun PlaylistResponse.toModel(): PlaylistResponseModel =
    PlaylistResponseModel(
        id = id,
        title = title,
        imageUrl = imageUrl,
        tracks = tracks.map { it.toModel() },

    )

fun Track.toModel(): TrackModel =
    TrackModel(
        artists = artists,
        title = title,
        imageUrl = imageUrl,
        previewUrl = previewUrl,
        duration = duration
    )
