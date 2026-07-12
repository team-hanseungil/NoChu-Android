package com.school_of_company.signin.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.school_of_company.signin.view.PhotoUploadRoute
import com.school_of_company.signin.view.PlaylistDetailContent
import com.school_of_company.signin.view.SignInRoute
import com.school_of_company.signin.view.SurveyRoute
import com.school_of_company.signin.view.lPlaylistDetailContent
import com.school_of_company.signin.viewmodel.SignInViewModel

const val StartRoute = "Start_route"
const val SignInRoute = "Sign_in_route"

const val PhotoFaceRoute = "Photo_face_route"
private const val MEMBER_ID_ARG = "memberId"
private const val PhotoFaceRouteWithArg = "$PhotoFaceRoute/{$MEMBER_ID_ARG}"

const val MUSIC_DETAIL_ID_ARG = "playlistId"
const val SURVEY_ROUTE = "survey_route"
const val MUSIC_DETAIL_ROUTE = "music_detail_route/{$MUSIC_DETAIL_ID_ARG}"

// memberId 아규먼트 제거
const val MUSIC_RECOMMEND_ROUTE = "musicrecommend"

fun NavGraphBuilder.surveyScreen(
    onBackClick: () -> Unit,
    onSurveyComplete: () -> Unit,
    onErrorToast: (throwable: Throwable?, message: Int?) -> Unit,
) {
    composable(route = SURVEY_ROUTE) {
        SurveyRoute(
            onBackClick = onBackClick,
            onSurveyComplete = onSurveyComplete,
            onErrorToast = onErrorToast,
        )
    }
}

fun NavGraphBuilder.musicDetailScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = MUSIC_DETAIL_ROUTE,
        arguments = listOf(
            navArgument(MUSIC_DETAIL_ID_ARG) { type = NavType.LongType }
        )
    ) { backStackEntry ->
        val playlistId = backStackEntry.arguments?.getLong(MUSIC_DETAIL_ID_ARG) ?: 0L
        val viewModel: SignInViewModel = hiltViewModel()
        val detailUiState = viewModel.playlistDetailUiState.collectAsState()

        LaunchedEffect(playlistId) {
            viewModel.fetchPlaylistDetail(playlistId)
        }

        com.school_of_company.design_system.theme.GwangSanTheme { colors, typography ->
            androidx.compose.material3.Scaffold(
                containerColor = colors.gray100
            ) { paddingValues ->
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    PlaylistDetailContent(
                        colors = colors,
                        typography = typography,
                        playlistId = playlistId,
                        uiState = detailUiState.value,
                        onBackClicked = onBackClick
                    )
                }
            }
        }
    }
}

fun NavController.navigateToMusicDetail(playlistId: Long, navOptions: NavOptions? = null) {
    this.navigate(
        MUSIC_DETAIL_ROUTE.replace("{$MUSIC_DETAIL_ID_ARG}", playlistId.toString()),
        navOptions
    )
}

fun NavGraphBuilder.musicRecommendScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = MUSIC_RECOMMEND_ROUTE // Argument가 없는 라우트로 변경
    ) {
        val viewModel: SignInViewModel = hiltViewModel()
        val detailUiState = viewModel.musicRRState.collectAsState()

        // 인자에서 memberId 제거하고 comment 위치에 null 전달
        LaunchedEffect(Unit) {
            viewModel.musicRR()
        }

        com.school_of_company.design_system.theme.GwangSanTheme { colors, typography ->
            androidx.compose.material3.Scaffold(
                containerColor = colors.gray100
            ) { paddingValues ->
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    lPlaylistDetailContent(
                        colors = colors,
                        typography = typography,
                        playlistId = 0L,
                        uiState = detailUiState.value,
                        onBackClicked = onBackClick
                    )
                }
            }
        }
    }
}

// memberId 파라미터 완전 제거
fun NavController.navigateToMusicRecommend(
    navOptions: NavOptions? = null
) {
    this.navigate(MUSIC_RECOMMEND_ROUTE, navOptions)
}

fun NavController.navigateToStart(navOptions: NavOptions? = null) {
    this.navigate(StartRoute, navOptions)
}

fun NavController.navigateToSignIn(navOptions: NavOptions? = null) {
    this.navigate(SignInRoute, navOptions)
}

fun NavGraphBuilder.signInScreen(
    onBackClick: () -> Unit,
    onMainClick: () -> Unit,
    onErrorToast: (throwable: Throwable?, message: Int?) -> Unit,
    onSignUpClick: () -> Unit
) {
    composable(route = SignInRoute) {
        SignInRoute(
            onBackClick = onBackClick,
            onErrorToast = onErrorToast,
            onMainClick = onMainClick,
            onSignUpClick = onSignUpClick
        )
    }
}

fun NavController.navigateToPhotoFace(
    memberId: Long,
    navOptions: NavOptions? = null
) {
    this.navigate("$PhotoFaceRoute/$memberId", navOptions)
}

fun NavController.navigateToSurvey(navOptions: NavOptions? = null) {
    this.navigate(SURVEY_ROUTE, navOptions)
}

fun NavGraphBuilder.photoFaceScreen(
    onBackClick: () -> Unit,
    onNavigateToMusicRecommend: () -> Unit // (Long) -> Unit 에서 파라미터가 없도록 변경
) {
    composable(
        route = PhotoFaceRouteWithArg,
        arguments = listOf(
            navArgument(MEMBER_ID_ARG) {
                type = NavType.LongType
            }
        )
    ) { backStackEntry ->
        val memberId = backStackEntry.arguments?.getLong(MEMBER_ID_ARG) ?: 0L

        PhotoUploadRoute(
            memberId = memberId,
            onNavigateToMusicRecommend = onNavigateToMusicRecommend
        )
    }
}
