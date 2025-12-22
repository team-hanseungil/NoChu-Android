package com.school_of_company.data.repository.post

import com.school_of_company.network.dto.reponse.EmotionHistoryResponse
import kotlinx.coroutines.flow.Flow

interface EmotionRepository {
    fun getEmotionHistory(memberId: Long): Flow<EmotionHistoryResponse>
}