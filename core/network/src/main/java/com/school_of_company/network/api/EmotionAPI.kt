package com.school_of_company.network.api

import com.school_of_company.network.dto.post.response.EmotionHistoryResponse
import retrofit2.http.GET

interface EmotionAPI {
    @GET("/emotions")
    suspend fun getEmotionHistory(): EmotionHistoryResponse
}