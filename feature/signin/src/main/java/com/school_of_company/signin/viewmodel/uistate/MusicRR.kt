package com.school_of_company.signin.viewmodel.uistate

import com.school_of_company.model.auth.request.PlaylistResponseModel


interface MusicRR {// // ✅ 추가
    data object Idle : MusicRR
    data object Loading : MusicRR
    data class Success(val data: PlaylistResponseModel) : MusicRR
    data class Error(val exception: Throwable) : MusicRR
}