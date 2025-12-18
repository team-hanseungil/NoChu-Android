package com.school_of_company.signin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.school_of_company.signin.view.SignInRoute
import com.school_of_company.signup.view.SignUpScreen // 'SignUpScreen' import 유효

// 네비게이션 경로 상수
const val StartRoute = "Start_route"
const val SignInRoute = "Sign_in_route"
const val SignUpRoute = "Sign_up_route"

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
    this.navigate( SignInRoute, navOptions)
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

fun NavController.navigateToSignUp(navOptions: NavOptions? = null) {
    this.navigate(SignUpRoute, navOptions)
}

fun NavGraphBuilder.signUpScreen(
    onBackClick: () -> Unit,
    onSignInClick: () -> Unit, // 로그인 페이지로 이동하는 콜백
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