package com.school_of_company.network.datasource.music

import com.school_of_company.network.api.MusicAPI
import com.school_of_company.network.dto.music.response.PlaylistListResponse
import com.school_of_company.network.util.performApiRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MusicDataSourceImpl @Inject constructor(
    private val musicAPI: MusicAPI
) : MusicDataSource {

    override fun getPlaylists(memberId: Long): Flow<PlaylistListResponse> =
        performApiRequest { musicAPI.getPlaylists(memberId) }

}