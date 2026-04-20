package com.school_of_company.network.dto.music.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaylistRequest(
    @Json(name = "comment") val comment: String?
)