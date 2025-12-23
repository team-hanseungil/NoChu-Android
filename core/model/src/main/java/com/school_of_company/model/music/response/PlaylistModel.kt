package com.school_of_company.model.music.response

/**
 * 플레이리스트 정보의 도메인 모델
 */
data class PlaylistModel(
    val id: Long,
    val title: String,
    val emotion: String,
    val imageUrl: String? = null,
    val trackCount: Long, // count를 trackCount로 도메인 네이밍 변경
    val createdAt: String
)

/**
 * 플레이리스트 목록 도메인 모델
 */
data class PlaylistListModel(
    val playlists: List<PlaylistModel>
)