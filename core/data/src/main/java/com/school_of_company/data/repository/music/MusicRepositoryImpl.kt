package com.school_of_company.data.repository.music

import com.school_of_company.model.music.response.PlaylistDetailModel
import com.school_of_company.model.music.response.PlaylistListModel
import com.school_of_company.model.music.response.TrackModel
import com.school_of_company.network.datasource.music.MusicDataSource
import com.school_of_company.network.mapper.music.response.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val musicDataSource: MusicDataSource
) : MusicRepository {

    override fun getPlaylists(memberId: Long): Flow<PlaylistListModel> =
        musicDataSource.getPlaylists(memberId).map { it.toModel() }

    override fun getPlaylistDetail(playlistId: Long): Flow<PlaylistDetailModel> =
        musicDataSource.getPlaylistDetail(playlistId).map { it.toModel() }

    override fun postMusicRecommend(memberId: Long, comment: String?): Flow<PlaylistDetailModel> =
        musicDataSource.postMusicRecommend(memberId, comment).map { dto ->
            PlaylistDetailModel(
                id = dto.id,
                title = dto.title,
                imageUrl = dto.imageUrl,
                tracks = dto.tracks.map { track ->
                    TrackModel(
                        artists = track.artists,
                        title = track.title,
                        imageUrl = track.imageUrl,
                        previewUrl = track.previewUrl ?: "",
                        duration = track.duration
                    )
                }
            )
        }
}