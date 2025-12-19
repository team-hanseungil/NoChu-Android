package com.school_of_company.post.viewmodel.uiState

import com.school_of_company.network.dto.reponse.EmotionHistoryResponse

sealed interface HistoryUiState {
    /** 데이터를 불러오는 중 */
    object Loading : HistoryUiState

    /** 데이터 로드 성공, 응답 데이터를 포함 */
    data class Success(val response: EmotionHistoryResponse) : HistoryUiState

    /** 데이터 로드 실패, 에러 메시지를 포함 */
    data class Error(val message: String) : HistoryUiState

    /** 데이터 로드 성공했으나, 목록이 비어 있음 */
    object Empty : HistoryUiState
}