package com.school_of_company.signin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument // NavType 사용을 위해 추가
import com.school_of_company.nochumain.PhotoUploadRoute
import com.school_of_company.signin.view.SignInRoute
import com.school_of_company.signup.view.SignUpScreen

const val StartRoute = "Start_route"
const val SignInRoute = "Sign_in_route"
const val SignUpRoute = "Sign_up_route"

// ✅ Route 이름: PhotoScreen의 Route를 정의
const val PhotoFaceRoute = "Photo_face_route"
private const val MEMBER_ID_ARG = "memberId"
private const val PhotoFaceRouteWithArg = "$PhotoFaceRoute/{$MEMBER_ID_ARG}"

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
// ✅ PhotoFace (PhotoScreen) 네비게이션
// ======================================================

fun NavController.navigateToPhotoFace(
    memberId: Long,
    navOptions: NavOptions? = null
) {
    // memberId를 URL 인자로 삽입하여 네비게이션 수행
    this.navigate("$PhotoFaceRoute/$memberId", navOptions)
}

fun NavGraphBuilder.photoFaceScreen(
    onBackClick: () -> Unit, // 현재 사용되지 않지만 구조 유지를 위해 남겨둠
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
            memberId = memberId
        )
    }
}