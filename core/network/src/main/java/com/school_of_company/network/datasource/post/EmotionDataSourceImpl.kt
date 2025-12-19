package com.school_of_company.network.datasource.post
import com.school_of_company.network.api.EmotionAPI
import com.school_of_company.network.dto.reponse.EmotionHistoryResponse
import com.school_of_company.network.util.performApiRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EmotionDataSourceImpl @Inject constructor(
    private val emotionAPI: EmotionAPI
) : EmotionDataSource {
    override fun getEmotionHistory(memberId: Long): Flow<EmotionHistoryResponse> =
        performApiRequest { emotionAPI.getEmotionHistoryByMemberId(memberId) }
}