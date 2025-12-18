package com.school_of_company.signup.view

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
import com.school_of_company.signup.viewmodel.SignUpViewModel // ⚠️ ViewModel 경로 확인
import com.school_of_company.signup.viewmodel.uistate.SignUpUiState // ⚠️ UiState 경로 확인
import com.school_of_company.ui.previews.GwangsanPreviews // ⚠️ 프리뷰 경로 확인

// =========================================================================================
// 1. SignUpScreen (Route 역할): ViewModel 연결 및 상태 관리
// =========================================================================================
@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onSignInClick: () -> Unit,
    onErrorToast: (throwable: Throwable?, message: Int?) -> Unit,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    // ViewModel 상태 관찰
    val nickname by viewModel.nickname.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val checkPassword by viewModel.checkPassword.collectAsStateWithLifecycle()
    val signUpUiState by viewModel.signUpUiState.collectAsStateWithLifecycle()

    // UI 오류 상태
    var nicknameIsError by remember { mutableStateOf(false) }
    var passwordCheckIsError by remember { mutableStateOf(false) }

    LaunchedEffect(signUpUiState) {
        // 매 상태 변화 시 오류 플래그 초기화 (로딩 시 제외)
        nicknameIsError = false
        passwordCheckIsError = false

        when (signUpUiState) {
            is SignUpUiState.Success -> {
                makeToast(context, "회원가입 성공! 로그인해주세요.")
                onSignInClick() // 성공 시 로그인 페이지로 이동
            }
            is SignUpUiState.PasswordMismatch -> {
                passwordCheckIsError = true
                onErrorToast(null, R.string.error_password_mismatch)
            }
            is SignUpUiState.Conflict -> {
                nicknameIsError = true
                onErrorToast(null, R.string.error_user_already_exists)
            }
            is SignUpUiState.BadRequest -> {
                onErrorToast(null, R.string.error_bad_request)
            }
            is SignUpUiState.Error -> {
                val e = (signUpUiState as SignUpUiState.Error).exception
                onErrorToast(e, R.string.error_generic)
            }
            SignUpUiState.Idle, SignUpUiState.Loading -> {
                // do nothing
            }
        }
    }

    SignUpContent(
        nickname = nickname,
        password = password,
        checkPassword = checkPassword,
        nicknameIsError = nicknameIsError,
        passwordCheckIsError = passwordCheckIsError,
        onNicknameChange = viewModel::onNicknameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onCheckPasswordChange = viewModel::onCheckPasswordChange,
        onSignInClick = onSignInClick,
        signUpCallBack = viewModel::signUp,
    )
}

// =========================================================================================
// 2. SignUpContent: 순수 UI 정의 (SignInScreen 구조 기반)
// =========================================================================================
@Composable
private fun SignUpContent(
    modifier: Modifier = Modifier,
    signUpCallBack: () -> Unit,
    onSignInClick: () -> Unit,
    nickname: String,
    password: String,
    checkPassword: String,
    onNicknameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onCheckPasswordChange: (String) -> Unit,
    nicknameIsError: Boolean = false,
    passwordCheckIsError: Boolean = false
) {
    val focusManager = LocalFocusManager.current

    val isInputValid = nickname.isNotBlank() && password.isNotBlank() && checkPassword.isNotBlank()
    val buttonState = if (isInputValid) ButtonState.Enable else ButtonState.Disable

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
                    Text(
                        text = "\uD83D\uDE0A",
                        style = typography.titleMedium2
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "회원가입",
                        style = typography.titleMedium2,
                        color = colors.black,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "새로운 계정을 만들어보세요",
                        style = typography.label,
                        color = colors.black.copy(alpha = 0.7f)
                    )

                    Spacer(Modifier.height(28.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. 닉네임 필드 (SignUpScreen의 id 역할)
                        GwangSanTextField(
                            value = nickname,
                            label = "닉네임",
                            placeHolder = "사용할 닉네임을 입력하세요",
                            onTextChange = onNicknameChange,
                            isError = nicknameIsError,
                            errorText = "이미 사용 중인 닉네임입니다.",
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // 2. 비밀번호 필드
                        GwangSanTextField(
                            value = password,
                            label = "비밀번호",
                            placeHolder = "비밀번호를 입력하세요",
                            onTextChange = onPasswordChange,
                            isError = false, // ViewModel에서 PasswordMismatch 오류는 checkPassword 필드에만 표시
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // 3. 비밀번호 확인 필드 (새로 추가)
                        GwangSanTextField(
                            value = checkPassword,
                            label = "비밀번호 확인",
                            placeHolder = "비밀번호를 다시 입력하세요",
                            onTextChange = onCheckPasswordChange,
                            isError = passwordCheckIsError, // ViewModel에서 PasswordMismatch 오류 표시
                            errorText = "비밀번호가 일치하지 않습니다.",
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    Spacer(Modifier.height(22.dp))

                    // GwangSanStateButton (SignInScreen과 동일한 텍스트 전용 형태 사용)
                    GwangSanStateButton(
                        text = "회원가입",
                        state = buttonState,
                        onClick = signUpCallBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )

                    Spacer(Modifier.height(18.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "이미 계정이 있으신가요? ",
                            style = typography.label,
                            color = colors.black.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "로그인",
                            style = typography.label,
                            color = colors.subYellow500,
                            modifier = Modifier.GwangSanClickable(onClick = onSignInClick)
                        )
                    }
                }
            }
        }
    }
}

 @GwangsanPreviews
 @Composable
 private fun SignUpScreenPreview() {
     SignUpContent(
         signUpCallBack = {},
         onSignInClick = {},
       nickname = "newuser",
        password = "password123",
        checkPassword = "password123",
        onNicknameChange = {},
     onPasswordChange = {},
         onCheckPasswordChange = {},
        nicknameIsError = false,
        passwordCheckIsError = false
    )
 }