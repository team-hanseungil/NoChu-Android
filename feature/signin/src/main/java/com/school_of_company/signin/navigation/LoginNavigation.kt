package com.school_of_company.signin.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.school_of_company.nochumain.PhotoUploadRoute
import com.school_of_company.signin.view.PlaylistDetailContent
import com.school_of_company.signin.view.SignInRoute
import com.school_of_company.signin.viewmodel.SignInViewModel
import com.school_of_company.signup.view.SignUpScreen

const val StartRoute = "Start_route"
const val SignInRoute = "Sign_in_route"
const val SignUpRoute = "Sign_up_route"

const val PhotoFaceRoute = "Photo_face_route"
private const val MEMBER_ID_ARG = "memberId"
private const val PhotoFaceRouteWithArg = "$PhotoFaceRoute/{$MEMBER_ID_ARG}"

const val MUSIC_DETAIL_ID_ARG = "playlistId"
const val MUSIC_DETAIL_ROUTE = "music_detail_route/{$MUSIC_DETAIL_ID_ARG}"

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

fun NavController.navigateToStart(navOptions: NavOptions? = null) {
    this.navigate(StartRoute, navOptions)
}

fun NavGraphBuilder.startScreen(
    onSignUpClick: () -> Unit,
    onInputLoginClick: () -> Unit,
) {
    composable(route = StartRoute) {

    }
}

fun NavController.navigateToSignIn(navOptions: NavOptions? = null) {
    this.navigate(SignInRoute, navOptions)
}

fun NavGraphBuilder.signInScreen(
    onBackClick: () -> Unit,
    onMainClick: (Long) -> Unit,
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

fun NavController.navigateToSignUp(navOptions: NavOptions? = null) {
    this.navigate(SignUpRoute, navOptions)
}

fun NavGraphBuilder.signUpScreen(
    onBackClick: () -> Unit,
    onSignInClick: () -> Unit,
    onErrorToast: (throwable: Throwable?, message: Int?) -> Unit
) {
    composable(route = SignUpRoute) {
        SignUpScreen(
            onBackClick = onBackClick,
            onSignInClick = onSignInClick,
            onErrorToast = onErrorToast
        )
    }
}

fun NavController.navigateToPhotoFace(
    memberId: Long,
    navOptions: NavOptions? = null
) {
    this.navigate("$PhotoFaceRoute/$memberId", navOptions)
}

fun NavGraphBuilder.photoFaceScreen(
    onBackClick: () -> Unit,
    onNavigateToMusicDetail: (Long) -> Unit
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
            onNavigateToMusicDetail = onNavigateToMusicDetail
        )
    }
}