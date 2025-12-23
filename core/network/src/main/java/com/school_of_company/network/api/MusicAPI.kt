package com.school_of_company.network.api

import com.school_of_company.network.dto.music.response.PlaylistListResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MusicAPI {
    /**
     * 특정 멤버(사용자)의 감정 기반 추천 플레이리스트 목록을 조회합니다.
     * @param memberId 조회할 멤버의 ID입니다.
     */
    @GET("/api/playlists/member/{memberId}")
    suspend fun getPlaylists(
        @Path("memberId") memberId: Long
    ): PlaylistListResponse
}