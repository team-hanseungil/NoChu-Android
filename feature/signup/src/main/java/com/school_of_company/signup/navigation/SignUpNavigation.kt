package com.school_of_company.signup.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.school_of_company.signup.view.SignUpScreen

const val SignUpRoute = "Sign_up_route"

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