package com.school_of_company.data.repository.music

import com.school_of_company.model.music.response.PlaylistDetailModel
import com.school_of_company.model.music.response.PlaylistListModel
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getPlaylists(memberId: Long): Flow<PlaylistListModel>
    fun getPlaylistDetail(playlistId: Long): Flow<PlaylistDetailModel>
}