package com.school_of_company.domain.repository.music

import com.school_of_company.model.music.response.PlaylistListModel
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    /**
     * 특정 멤버 ID에 해당하는 플레이리스트 목록을 가져옵니다.
     */
    fun getPlaylists(memberId: Long): Flow<PlaylistListModel>
}