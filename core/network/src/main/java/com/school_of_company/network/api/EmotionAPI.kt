package com.school_of_company.network.api

import com.school_of_company.network.dto.post.response.EmotionHistoryResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface EmotionAPI {
    @GET("/api/emotions/{memberId}")
    suspend fun getEmotionHistoryByMemberId(
        @Path("memberId") memberId: Long
    ): EmotionHistoryResponse
}