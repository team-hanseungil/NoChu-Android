package com.school_of_company.network.datasource.post

import com.school_of_company.network.api.PostAPI
import com.school_of_company.network.dto.post.response.EmotionHistoryResponse
import com.school_of_company.network.util.performApiRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EmotionDataSourceImpl @Inject constructor(
    private val postAPI: PostAPI
) : EmotionDataSource {

    override fun getEmotionHistory(): Flow<EmotionHistoryResponse> =
        performApiRequest {
            postAPI.getEmotionHistory()
        }
}