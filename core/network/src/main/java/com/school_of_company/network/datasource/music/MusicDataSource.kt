package com.school_of_company.network.datasource.music

import com.school_of_company.network.dto.music.response.PlaylistDetailResponse // <-- import 추가
import com.school_of_company.network.dto.music.response.PlaylistListResponse
import kotlinx.coroutines.flow.Flow

interface MusicDataSource {
    /**
     * Flow를 통해 멤버 ID에 따른 플레이리스트 목록을 가져옵니다.
     */
    fun getPlaylists(memberId: Long): Flow<PlaylistListResponse>

    /**
     * Flow를 통해 플레이리스트 ID에 따른 상세 정보를 가져옵니다.
     */
    fun getPlaylistDetail(playlistId: Long): Flow<PlaylistDetailResponse> // <-- 새로운 함수 추가
}