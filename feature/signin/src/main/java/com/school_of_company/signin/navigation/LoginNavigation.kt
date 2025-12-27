package com.school_of_company.signin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.school_of_company.nochumain.PhotoUploadRoute
import com.school_of_company.signin.view.SignInRoute
import com.school_of_company.signup.view.SignUpScreen

const val StartRoute = "Start_route"
const val SignInRoute = "Sign_in_route"
const val SignUpRoute = "Sign_up_route"

// ✅ PhotoFace (메인 탭 화면) Route 정의
const val PhotoFaceRoute = "Photo_face_route"
private const val MEMBER_ID_ARG = "memberId"
private const val PhotoFaceRouteWithArg = "$PhotoFaceRoute/{$MEMBER_ID_ARG}"

// ======================================================
// 🚀 음악 상세 화면 (Music Detail) Route 정의 및 Nav 통합
// ======================================================

const val MUSIC_DETAIL_ID_ARG = "playlistId"
const val MUSIC_DETAIL_ROUTE = "music_detail_route/{$MUSIC_DETAIL_ID_ARG}" // 상세 화면 경로

/**
 * [NavGraphBuilder] 확장 함수: 음악 상세 화면을 네비게이션 그래프에 등록합니다.
 */
fun NavGraphBuilder.musicDetailScreen() {
    composable(
        route = MUSIC_DETAIL_ROUTE,
        arguments = listOf(
            navArgument(MUSIC_DETAIL_ID_ARG) { type = NavType.LongType }
        )
    ) { backStackEntry ->
        val playlistId = backStackEntry.arguments?.getLong(MUSIC_DETAIL_ID_ARG) ?: 0L

        // TODO: MusicDetailScreen 컴포저블을 호출하고 playlistId를 넘겨줍니다.
        // 예: MusicDetailScreen(playlistId = playlistId)
    }
}

/**
 * [NavController] 확장 함수: 음악 상세 화면으로 이동합니다.
 */
fun NavController.navigateToMusicDetail(playlistId: Long, navOptions: NavOptions? = null) {
    this.navigate(MUSIC_DETAIL_ROUTE.replace("{$MUSIC_DETAIL_ID_ARG}", playlistId.toString()), navOptions)
}


// ======================================================
// ✅ 기존 로그인/회원가입 네비게이션 (변경 없음)
// ======================================================

fun NavController.navigateToStart(navOptions: NavOptions? = null) {
    this.navigate(StartRoute, navOptions)
}

fun NavGraphBuilder.startScreen(
    onSignUpClick: () -> Unit,
    onInputLoginClick: () -> Unit,
) {
    composable(route = StartRoute) {
        // StartRoute( onSignUpClick = onSignUpClick, onInputLoginClick = onInputLoginClick )
    }
}

fun NavController.navigateToSignIn(navOptions: NavOptions? = null) {
    this.navigate(SignInRoute, navOptions)
}

fun NavGraphBuilder.signInScreen(
    onBackClick: () -> Unit,
    onMainClick: (Long) -> Unit, // 로그인 성공 시 memberId 전달
    onErrorToast: (throwable: Throwable?, message: Int?) -> Unit,
    onSignUpClick: () -> Unit
) {
    composable(route = SignInRoute) {
        SignInRoute(
            onBackClick = onBackClick,
            onErrorToast = onErrorToast,
            // onMainClick: 로그인 성공 시 memberId를 전달하여 PhotoScreen으로 이동
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

// ======================================================
// ✅ PhotoFace (PhotoScreen) 네비게이션 (수정 필요)
// ======================================================

fun NavController.navigateToPhotoFace(
    memberId: Long,
    navOptions: NavOptions? = null
) {
    // memberId를 URL 인자로 삽입하여 네비게이션 수행
    this.navigate("$PhotoFaceRoute/$memberId", navOptions)
}

fun NavGraphBuilder.photoFaceScreen(
    onBackClick: () -> Unit,
    // ⚠️ 수정: Music Detail로 이동하는 콜백 추가
    onNavigateToMusicDetail: (Long) -> Unit
) {
    composable(
        route = PhotoFaceRouteWithArg,
        arguments = listOf(
            navArgument(MEMBER_ID_ARG) {
                type = NavType.LongType // 인자 타입 Long 명시
            }
        )
    ) { backStackEntry ->
        // LongType으로 인자를 가져옴.
        val memberId = backStackEntry.arguments?.getLong(MEMBER_ID_ARG) ?: 0L

        PhotoUploadRoute(
            memberId = memberId,
            // ⚠️ 콜백 전달
            onNavigateToMusicDetail = onNavigateToMusicDetail
        )
    }
}