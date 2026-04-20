package com.school_of_company.network.datasource.music

import com.school_of_company.network.dto.music.response.PlaylistDetailResponse
import com.school_of_company.network.dto.music.response.PlaylistListResponse
import kotlinx.coroutines.flow.Flow

interface MusicDataSource {
    fun getPlaylists(memberId: Long): Flow<PlaylistListResponse>
    fun getPlaylistDetail(playlistId: Long): Flow<PlaylistDetailResponse>
    fun postMusicRecommend(memberId: Long, comment: String?): Flow<PlaylistDetailResponse>  // 추가
}