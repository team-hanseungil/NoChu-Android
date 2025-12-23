package com.school_of_company.data.repository.music

import com.school_of_company.domain.repository.music.MusicRepository
import com.school_of_company.model.music.response.PlaylistListModel
import com.school_of_company.network.datasource.music.MusicDataSource
import com.school_of_company.network.mapper.music.response.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val musicDataSource: MusicDataSource
) : MusicRepository {

    override fun getPlaylists(memberId: Long): Flow<PlaylistListModel> {
        // DataSource에서 DTO를 받아와 Mapper를 통해 Model로 변환
        return musicDataSource.getPlaylists(memberId).map { it.toModel() }
    }
}