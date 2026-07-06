package com.school_of_company.network.api

import com.school_of_company.network.dto.music.request.PlaylistRequest
import com.school_of_company.network.dto.music.response.PlaylistDetailResponse
import com.school_of_company.network.dto.music.response.PlaylistListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MusicAPI {
    @GET("/api/playlists/my") // 👈 서버 명세서에 맞춰 엔드포인트를 적절히 수정해 주세요 (예: "/api/playlists")
    suspend fun getPlaylists(): PlaylistListResponse

    @GET("/api/playlists/{playlistId}")
    suspend fun getPlaylistDetail(
        @Path("playlistId") playlistId: Long
    ): PlaylistDetailResponse

    @POST("api/music")
    suspend fun postMusicRecommend(
        @Body body: PlaylistRequest
    ): PlaylistDetailResponse
}