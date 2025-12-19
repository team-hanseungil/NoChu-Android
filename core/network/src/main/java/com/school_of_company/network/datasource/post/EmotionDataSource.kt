package com.school_of_company.network.datasource.post

import com.school_of_company.network.dto.reponse.EmotionHistoryResponse
import kotlinx.coroutines.flow.Flow

interface EmotionDataSource {
    fun getEmotionHistory(memberId: Long): Flow<EmotionHistoryResponse>
}