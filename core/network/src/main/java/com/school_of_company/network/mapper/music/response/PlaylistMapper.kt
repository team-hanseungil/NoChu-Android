package com.school_of_company.network.mapper.music.response

import com.school_of_company.model.music.response.PlaylistDetailModel
import com.school_of_company.model.music.response.PlaylistListModel
import com.school_of_company.model.music.response.PlaylistModel
import com.school_of_company.model.music.response.TrackModel
import com.school_of_company.network.dto.music.response.PlaylistDetailResponse
import com.school_of_company.network.dto.music.response.PlaylistListResponse
import com.school_of_company.network.dto.music.response.PlaylistResponse
import com.school_of_company.network.dto.music.response.TrackResponse

/**
 * PlaylistResponse DTO를 PlaylistModel 도메인 모델로 변환합니다.
 */
fun PlaylistResponse.toModel() = PlaylistModel(
    id = this.id,
    title = this.title,
    emotion = this.emotion,
    imageUrl = this.imageUrl,
    trackCount = this.count,
    createdAt = this.createdAt
)

/**
 * PlaylistListResponse DTO를 PlaylistListModel 도메인 모델로 변환합니다.
 */
fun PlaylistListResponse.toModel() = PlaylistListModel(
    playlists = this.playlists.map { it.toModel() }
)

fun TrackResponse.toModel() = TrackModel( // <-- 새로운 toModel 매퍼
    artists = this.artists,
    title = this.title,
    imageUrl = this.imageUrl,
    previewUrl = this.previewUrl,
    duration = this.duration
)

/**
 * PlaylistDetailResponse DTO를 PlaylistDetailModel 도메인 모델로 변환합니다.
 */
fun PlaylistDetailResponse.toModel() = PlaylistDetailModel( // <-- 새로운 toModel 매퍼
    id = this.id,
    title = this.title,
    imageUrl = this.imageUrl,
    tracks = this.tracks.map { it.toModel() }
)