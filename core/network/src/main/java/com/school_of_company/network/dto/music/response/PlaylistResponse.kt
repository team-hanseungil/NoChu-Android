package com.school_of_company.network.dto.music.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 개별 플레이리스트 정보를 담는 응답 DTO입니다.
 */
@JsonClass(generateAdapter = true)
data class PlaylistResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "emotion") val emotion: String,
    @Json(name = "imageUrl") val imageUrl: String? = null, // nullable
    @Json(name = "count") val count: Long,
    @Json(name = "createdAt") val createdAt: String
)

/**
 * 플레이리스트 목록 조회의 전체 응답을 담는 DTO입니다.
 */
@JsonClass(generateAdapter = true)
data class PlaylistListResponse(
    @Json(name = "playlists") val playlists: List<PlaylistResponse>
)