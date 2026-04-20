package com.school_of_company.signin.viewmodel.uistate

import com.school_of_company.model.music.response.PlaylistDetailModel

sealed interface MusicRR {
    data object Idle : MusicRR
    data object Loading : MusicRR
    data class Success(val data: PlaylistDetailModel) : MusicRR
    data class Error(val exception: Throwable) : MusicRR
}