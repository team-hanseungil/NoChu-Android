package com.school_of_company.signin.viewmodel.uistate // SignIn ViewModel 패키지 내에 정의

import com.school_of_company.model.music.response.PlaylistListModel

/**
 * 음악 화면의 UI 상태를 정의합니다. (SignInViewModel에서 사용)
 */
sealed interface MusicUiState {
    // 초기 상태 또는 데이터 로딩 중
    object Loading : MusicUiState

    // 유휴 상태 (아직 로드 시도가 없거나 초기화된 상태)
    object Idle : MusicUiState

    // 데이터 로딩 성공
    data class Success(val playlistList: PlaylistListModel) : MusicUiState

    // 오류 발생
    data class Error(val exception: Throwable) : MusicUiState
}