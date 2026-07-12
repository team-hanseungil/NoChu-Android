package com.school_of_company.signin.viewmodel

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.school_of_company.data.repository.auth.AuthRepository
import com.school_of_company.data.repository.local.LocalRepository
import com.school_of_company.data.repository.music.MusicRepository
import com.school_of_company.model.auth.request.PostSurveyRequestModel
import com.school_of_company.model.auth.request.PostSurveyWrapperModel
import com.school_of_company.network.errorHandling
import com.school_of_company.result.asResult
import com.school_of_company.result.Result
import com.school_of_company.signin.viewmodel.uistate.MusicRR
import com.school_of_company.signin.viewmodel.uistate.MusicUiState
import com.school_of_company.signin.viewmodel.uistate.PlaylistDetailUiState
import com.school_of_company.signin.viewmodel.uistate.PostFaceUiState
import com.school_of_company.signin.viewmodel.uistate.PostSuveyUiState
import com.school_of_company.signin.viewmodel.uistate.SignInUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import getMultipartFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle,
    private val localRepository: LocalRepository,
    private val musicRepository: MusicRepository
) : ViewModel() {

    // =========================== 상태 ===========================

    private val _signInUiState = MutableStateFlow<SignInUiState>(SignInUiState.Loading)
    internal val signInUiState = _signInUiState.asStateFlow()

    private val _musicRRState = MutableStateFlow<MusicRR>(MusicRR.Loading)
    internal val musicRRState = _musicRRState.asStateFlow()

    private val _musicUiState = MutableStateFlow<MusicUiState>(MusicUiState.Idle)
    val musicUiState = _musicUiState.asStateFlow()

    private val _playlistDetailUiState = MutableStateFlow<PlaylistDetailUiState>(PlaylistDetailUiState.Idle)
    val playlistDetailUiState = _playlistDetailUiState.asStateFlow()

    private val _postFaceUiState = MutableStateFlow<PostFaceUiState>(PostFaceUiState.Idle)
    val postFaceUiState = _postFaceUiState.asStateFlow()

    private val _postSuveyUiState = MutableStateFlow<PostSuveyUiState>(PostSuveyUiState.Idle)
    val postSuveyUiState = _postSuveyUiState.asStateFlow()


    private val _currentMemberId = MutableStateFlow<Long>(0L)
    val currentMemberId = _currentMemberId.asStateFlow()

    // =========================== 초기화 ===========================

    init {
        viewModelScope.launch {
            _currentMemberId.value = localRepository.getMemberId()
        }
    }

    // ========================= Spotify 로그인 ==========================

    internal fun loginWithSpotify(code: String) = viewModelScope.launch {
        _signInUiState.value = SignInUiState.Loading

        authRepository.loginWithSpotify(code = code)
            .asResult()
            .collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        _signInUiState.value = SignInUiState.Loading
                    }
                    is Result.Success -> {
                        Log.d("SignInViewModel", "Spotify login success")
                        authRepository.saveToken(result.data)
                        _signInUiState.value = SignInUiState.Success
                    }
                    is Result.Error -> {
                        Log.e("SignInViewModel", "Spotify login failed: ${result.exception}")
                        _signInUiState.value = SignInUiState.Error(result.exception)
                    }
                }
            }
    }


    internal fun postSurvey(
        genres: List<String>,
        artists: List<String>,
        sadMoodOption: String,
        happyMoodOption: String
    ) = viewModelScope.launch {
        _postSuveyUiState.value = PostSuveyUiState.Loading

        val requestModel = PostSurveyWrapperModel(
            data = PostSurveyRequestModel(
                genres = genres,
                artists = artists,
                sadMoodOption = sadMoodOption,
                happyMoodOption = happyMoodOption
            )
        )

        authRepository.postSurvey(body = requestModel)
            .asResult()
            .collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        _postSuveyUiState.value = PostSuveyUiState.Loading
                    }
                    is Result.Success -> {
                        Log.d("SignInViewModel", "Survey post success")
                        _postSuveyUiState.value = PostSuveyUiState.Success(data = requestModel)
                    }
                    is Result.Error -> {
                        Log.e("SignInViewModel", "Survey post failed: ${result.exception}")
                        _postSuveyUiState.value = PostSuveyUiState.Error(result.exception)
                    }
                }
            }
    }

    // ========================= 음악 로직 ==========================

    // 헤더로 처리하므로 memberId 파라미터 제거
    internal fun fetchPlaylists() = viewModelScope.launch {
        musicRepository.getPlaylists()
            .asResult()
            .collectLatest { result ->
                when (result) {
                    is Result.Loading -> _musicUiState.value = MusicUiState.Loading
                    is Result.Success -> {
                        _musicUiState.value = MusicUiState.Success(result.data)
                        Log.d(TAG, "Playlists fetched successfully: ${result.data}")
                    }
                    is Result.Error -> {
                        _musicUiState.value = MusicUiState.Error(result.exception)
                        Log.e(TAG, "Failed to fetch playlists: ${result.exception.message}")
                    }
                }
            }
    }

    // SignInViewModel.musicRR 함수에 로그 추가
    internal fun musicRR() = viewModelScope.launch {
        _musicRRState.value = MusicRR.Loading
        authRepository.musicRR()
            .asResult()
            .collectLatest { result ->
                Log.d("MusicRR", "Result: $result")
                when (result) {
                    is Result.Loading -> _musicRRState.value = MusicRR.Loading
                    is Result.Success -> _musicRRState.value = MusicRR.Success(result.data)
                    is Result.Error   -> _musicRRState.value = MusicRR.Error(result.exception)
                }
            }
    }
    internal fun fetchPlaylistDetail(playlistId: Long) = viewModelScope.launch {
        musicRepository.getPlaylistDetail(playlistId)
            .asResult()
            .collectLatest { result ->
                when (result) {
                    is Result.Loading -> _playlistDetailUiState.value = PlaylistDetailUiState.Loading
                    is Result.Success -> {
                        _playlistDetailUiState.value = PlaylistDetailUiState.Success(result.data)
                        Log.d(TAG, "Playlist detail fetched: ${result.data.id}")
                    }
                    is Result.Error -> {
                        _playlistDetailUiState.value = PlaylistDetailUiState.Error(result.exception)
                        Log.e(TAG, "Failed to fetch playlist detail: ${result.exception.message}")
                    }
                }
            }
    }

    // ========================= 기타 로직 ==========================

    internal fun postFace(context: Context, image: Uri) = viewModelScope.launch {
        _postFaceUiState.value = PostFaceUiState.Loading

        val multipartFile = getMultipartFile(context, image)
            ?: run {
                _postFaceUiState.value = PostFaceUiState.Error(
                    IllegalStateException("이미지 파일 변환 실패")
                )
                return@launch
            }

        authRepository.postFace(image = multipartFile)
            .asResult()
            .collectLatest { result ->
                when (result) {
                    is Result.Loading -> _postFaceUiState.value = PostFaceUiState.Loading
                    is Result.Success -> _postFaceUiState.value = PostFaceUiState.Success(result.data)
                    is Result.Error   -> _postFaceUiState.value = PostFaceUiState.Error(result.exception)
                }
            }
    }

    fun resetPostFaceState() {
        _postFaceUiState.value = PostFaceUiState.Idle
    }

    fun resetMusicRRState() {
        _musicRRState.value = MusicRR.Idle
    }
}