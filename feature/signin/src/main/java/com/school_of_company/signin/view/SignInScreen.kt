package com.school_of_company.signin.view

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.school_of_company.design_system.R
import com.school_of_company.design_system.component.button.GwangSanStateButton
import com.school_of_company.design_system.component.button.state.ButtonState
import com.school_of_company.design_system.component.clickable.GwangSanClickable
import com.school_of_company.design_system.component.textfield.GwangSanTextField
import com.school_of_company.design_system.component.toast.makeToast
import com.school_of_company.design_system.theme.GwangSanTheme
import com.school_of_company.network.util.DeviceIdManager
import com.school_of_company.signin.viewmodel.SignInViewModel
import com.school_of_company.signin.viewmodel.uistate.SignInUiState
import com.school_of_company.ui.previews.GwangsanPreviews

@Composable
internal fun SignInRoute(
    onBackClick: () -> Unit,
    onMainClick: () -> Unit,
    onErrorToast: (throwable: Throwable?, message: Int?) -> Unit,
    onSignUpClick: () -> Unit, // 네비게이션 콜백 추가
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val signInUiState by viewModel.signInUiState.collectAsStateWithLifecycle()
    val id by viewModel.id.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()

    val deviceId = remember { DeviceIdManager.getDeviceId(context) }

    var idIsError by remember { mutableStateOf(false) }
    var passwordIsError by remember { mutableStateOf(false) }

    LaunchedEffect(signInUiState) {
        when (signInUiState) {
            is SignInUiState.Loading -> {
                idIsError = false
                passwordIsError = false
            }

            is SignInUiState.Success -> {
                onMainClick()
                makeToast(context, "로그인 성공")
            }

            is SignInUiState.NotFound -> {
                idIsError = true
                onErrorToast(null, R.string.error_user_missing)
            }

            is SignInUiState.BadRequest -> {
                passwordIsError = true
                onErrorToast(null, R.string.error_login)
            }

            is SignInUiState.IdNotValid -> {
                idIsError = true
                onErrorToast(null, R.string.error_id_not_validd)
            }

            is SignInUiState.Error -> {
                val e = (signInUiState as SignInUiState.Error).exception
                onErrorToast(e, R.string.error_login)
            }
            else -> { /* Idle 등 기타 상태 */ }
        }
    }

    SignInScreen(
        id = id,
        password = password,
        idIsError = idIsError,
        passwordIsError = passwordIsError,
        onIdChange = viewModel::onIdChange,
        onPasswordChange = viewModel::onPasswordChange,
        onBackClick = onBackClick,
        onMainClick = onMainClick,
        onSignUpClick = onSignUpClick,
        signInCallBack = {
            // deviceId(UUID)를 인자로 넘겨서 login 함수 호출
            viewModel.login(deviceId)
        }
    )
}

@Composable
private fun SignInScreen(
    modifier: Modifier = Modifier,
    signInCallBack: () -> Unit,
    onSignUpClick: () -> Unit,
    id: String,
    onBackClick: () -> Unit,
    onMainClick: () -> Unit,
    password: String,
    onIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    idIsError: Boolean = false,
    passwordIsError: Boolean = false
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
                    Text(text = "\uD83D\uDE0A", style = typography.titleMedium2)
                    Spacer(Modifier.height(12.dp))
                    Text(text = "로그인", style = typography.titleMedium2, color = colors.black, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Text(text = "감정 분석 앱에 오신 것을 환영합니다", style = typography.label, color = colors.black.copy(alpha = 0.7f))
                    Spacer(Modifier.height(28.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GwangSanTextField(
                            value = id, label = "이름", placeHolder = "이름을 입력하세요", onTextChange = onIdChange,
                            isError = idIsError, errorText = "유효하지 않은 이름입니다", modifier = Modifier.fillMaxWidth(), singleLine = true
                        )

                        GwangSanTextField(
                            value = password, label = "비밀번호", placeHolder = "비밀번호를 입력하세요", onTextChange = onPasswordChange,
                            isError = passwordIsError, errorText = "유효하지 않은 비밀번호입니다",
                            visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true
                        )
                    }

                    Spacer(Modifier.height(22.dp))

                    GwangSanStateButton(
                        text = "로그인",
                        state = if (id.isNotBlank() && password.isNotBlank()) ButtonState.Enable else ButtonState.Disable,
                        onClick = signInCallBack,
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    )

                    Spacer(Modifier.height(18.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "계정이 없으신가요? ", style = typography.label, color = colors.black.copy(alpha = 0.7f))
                        Text(text = "회원가입", style = typography.label, color = colors.subYellow500, modifier = Modifier.GwangSanClickable(onClick = onSignUpClick))
                    }
                }
            }
        }
    }
}

@GwangsanPreviews
@Composable
private fun SignInScreenPreview_Error() {
    SignInScreen(
        onBackClick = {}, signInCallBack = {}, onSignUpClick = {},
        id = "non", password = "1234", onIdChange = {}, onPasswordChange = {},
        idIsError = true, passwordIsError = true, onMainClick = {},
    )
}