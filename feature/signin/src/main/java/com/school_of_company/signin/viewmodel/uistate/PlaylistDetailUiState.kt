package com.school_of_company.signin.viewmodel.uistate

import com.school_of_company.model.music.response.PlaylistDetailModel

sealed interface PlaylistDetailUiState {
    data object Idle : PlaylistDetailUiState
    data object Loading : PlaylistDetailUiState
    data class Success(val playlistDetail: PlaylistDetailModel) : PlaylistDetailUiState
    data class Error(val exception: Throwable) : PlaylistDetailUiState
}