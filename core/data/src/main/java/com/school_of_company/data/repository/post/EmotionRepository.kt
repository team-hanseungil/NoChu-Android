package com.school_of_company.data.repository.post // post 패키지 경로 사용

import com.school_of_company.network.dto.reponse.EmotionHistoryResponse
import kotlinx.coroutines.flow.Flow

interface EmotionRepository {
    fun getEmotionHistory(memberId: Long): Flow<EmotionHistoryResponse>
}