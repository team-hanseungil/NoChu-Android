package com.school_of_company.signin.view

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.school_of_company.design_system.component.button.GwangSanStateButton
import com.school_of_company.design_system.component.button.state.ButtonState
import com.school_of_company.design_system.component.clickable.GwangSanClickable
import com.school_of_company.design_system.component.toast.makeToast
import com.school_of_company.design_system.theme.GwangSanTheme
import com.school_of_company.signin.viewmodel.SignInViewModel
import com.school_of_company.signin.viewmodel.uistate.SignInUiState
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

@Composable
internal fun SignInRoute(
    onBackClick: () -> Unit,
    onMainClick: () -> Unit,
    onErrorToast: (throwable: Throwable?, message: Int?) -> Unit,
    onSignUpClick: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val activity = context as Activity
    val signInUiState by viewModel.signInUiState.collectAsStateWithLifecycle()
    val currentMemberId by viewModel.currentMemberId.collectAsStateWithLifecycle()

    val spotifyLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val response = AuthorizationClient.getResponse(result.resultCode, result.data)
        when (response.type) {
            AuthorizationResponse.Type.CODE -> {
                viewModel.loginWithSpotify(response.code)
            }
            AuthorizationResponse.Type.ERROR -> {
                onErrorToast(Exception(response.error), null)
            }
            else -> Unit
        }
    }

    LaunchedEffect(signInUiState) {
        when (signInUiState) {
            is SignInUiState.Loading -> Unit
            is SignInUiState.Success -> {
                makeToast(context, "로그인 성공")
                onMainClick()
            }
            is SignInUiState.Error -> {
                val e = (signInUiState as SignInUiState.Error).exception
                onErrorToast(e, null)
            }
        }
    }

    SignInScreen(
        onSpotifyLoginClick = {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            val spotifyClientId = appInfo.metaData?.getString("spotify_client_id") ?: ""

            val spotifyRedirectUri ="nochu://auth/spotify/callback"

            val request = AuthorizationRequest.Builder(
                spotifyClientId,
                AuthorizationResponse.Type.CODE,
                spotifyRedirectUri
            )
                .setScopes(arrayOf("streaming", "user-read-email"))
                .build()

            val intent = AuthorizationClient.createLoginActivityIntent(activity, request)
            spotifyLauncher.launch(intent)
        },
        onBackClick = onBackClick,
        onSignUpClick = onSignUpClick,
    )
}

@Composable
private fun SignInScreen(
    modifier: Modifier = Modifier,
    onSpotifyLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    onSignUpClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    GwangSanTheme { colors, typography ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colors.white)
                .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = colors.white),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🎵", style = typography.titleMedium2)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "로그인",
                        style = typography.titleMedium2,
                        color = colors.black,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "이미지 기반 AI 감정 분석을 시작하세요",
                        style = typography.label,
                        color = colors.black.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(28.dp))

                    GwangSanStateButton(
                        text = "Spotify로 로그인",
                        state = ButtonState.Enable,
                        onClick = onSpotifyLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )

                    Spacer(Modifier.height(18.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "계정이 없으신가요? ",
                            style = typography.label,
                            color = colors.black.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "회원가입",
                            style = typography.label,
                            color = colors.subYellow500,
                            modifier = Modifier.GwangSanClickable(onClick = onSignUpClick)
                        )
                    }
                }
            }
        }
    }
}