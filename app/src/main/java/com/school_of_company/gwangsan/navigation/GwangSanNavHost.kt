package com.school_of_company.gwangsan.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import com.school_of_company.gwangsan.R
import com.school_of_company.gwangsan.ui.GwangSanAppState
import com.school_of_company.design_system.component.toast.makeToast
import com.school_of_company.common.ForBiddenException
import com.school_of_company.common.NoInternetException
import com.school_of_company.common.OtherHttpException
import com.school_of_company.common.ServerException
import com.school_of_company.common.TimeOutException
import com.school_of_company.common.UnKnownException
import com.school_of_company.signin.navigation.StartRoute
import com.school_of_company.signin.navigation.SignUpRoute
import com.school_of_company.signin.navigation.musicDetailScreen
import com.school_of_company.signin.navigation.musicRecommendScreen
import com.school_of_company.signin.navigation.navigateToMusicDetail
import com.school_of_company.signin.navigation.navigateToMusicRecommend
import com.school_of_company.signin.navigation.navigateToPhotoFace
import com.school_of_company.signin.navigation.photoFaceScreen
import com.school_of_company.signin.navigation.signInScreen
import com.school_of_company.signin.navigation.signUpScreen

@Composable
fun GwangsanNavHost(
    modifier: Modifier = Modifier,
    appState: GwangSanAppState,
    startDestination: String = StartRoute
) {
    val navController = appState.navController
    val context = LocalContext.current

    val onErrorToast: (throwable: Throwable?, message: Int?) -> Unit = { throwable, message ->
        val errorMessage = throwable?.let {
            when (it) {
                is ForBiddenException -> R.string.error_forbidden
                is TimeOutException -> R.string.error_time_out
                is ServerException -> R.string.error_server
                is NoInternetException -> R.string.error_no_internet
                is OtherHttpException -> R.string.error_no_internet
                is UnKnownException -> R.string.error_un_known
                else -> message
            }
        } ?: message ?: R.string.error_default

        makeToast(context, context.getString(errorMessage))
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        signInScreen(
            onBackClick = { navController.popBackStack() },
            onMainClick = { id ->
                navController.navigateToPhotoFace(memberId = id)
            },
            onErrorToast = onErrorToast,
            onSignUpClick = { navController.navigate(SignUpRoute) }
        )

        signUpScreen(
            onBackClick = { navController.popBackStack() },
            onSignInClick = { navController.popBackStack() },
            onErrorToast = onErrorToast
        )

        photoFaceScreen(
            onBackClick = { navController.popBackStack() },
            onNavigateToMusicRecommend = { memberId ->
                navController.navigateToMusicRecommend(memberId)
            }
        )

        musicDetailScreen(
            onBackClick = { navController.popBackStack() }
        )

        musicRecommendScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}
