package com.school_of_company.data.repository.post

import com.school_of_company.network.datasource.post.EmotionDataSource
import com.school_of_company.network.dto.post.response.EmotionHistoryResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EmotionRepositoryImpl @Inject constructor(
    private val emotionDataSource: EmotionDataSource
) : EmotionRepository {
    override fun getEmotionHistory(memberId: Long): Flow<EmotionHistoryResponse> {
        return emotionDataSource.getEmotionHistory(memberId)
    }
}