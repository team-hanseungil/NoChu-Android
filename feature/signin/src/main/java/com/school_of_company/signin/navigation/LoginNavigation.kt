package com.school_of_company.signin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.school_of_company.signin.view.SignInRoute
import com.school_of_company.signup.view.SignUpScreen
import com.school_of_company.nochumain.PhotoUploadRoute

const val StartRoute = "Start_route"
const val SignInRoute = "Sign_in_route"
const val SignUpRoute = "Sign_up_route"

// ✅ 추가
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

// ======================================================
// ✅ PhotoFace (PhotoUpload) 네비게이션 추가
// ======================================================

fun NavController.navigateToPhotoFace(
    memberId: Long,
    navOptions: NavOptions? = null
) {
    this.navigate("$PhotoFaceRoute/$memberId", navOptions)
}

fun NavGraphBuilder.photoFaceScreen(
    onBackClick: () -> Unit,
) {
    composable(route = PhotoFaceRouteWithArg) { backStackEntry ->
        val memberId = backStackEntry.arguments
            ?.getString(MEMBER_ID_ARG)
            ?.toLongOrNull()
            ?: return@composable

        PhotoUploadRoute(
            memberId = memberId
        )
    }
}
