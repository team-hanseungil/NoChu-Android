package com.school_of_company.signin.view


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.school_of_company.design_system.component.button.GwangSanStateButton
import com.school_of_company.design_system.component.button.state.ButtonState
import com.school_of_company.design_system.theme.GwangSanTheme

@Composable
internal fun EmotionAnalyzeScreen(
    modifier: Modifier = Modifier,
    onAnalyzeClick: (Uri) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedUri = uri }
    )

    GwangSanTheme { colors, typography ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(colors.white)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // ✅ 이미지 프리뷰 박스 (스샷처럼 큰 박스)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = 1.dp,
                        color = colors.gray300,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .background(colors.gray100),
                contentAlignment = Alignment.Center
            ) {
                if (selectedUri == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // 아이콘은 너 디자인시스템 아이콘으로 바꿔도 됨 (여기선 텍스트로 대체)
                        Text(
                            text = "⬆️",
                            style = typography.titleMedium2,
                            color = colors.gray500
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "이미지를 선택해주세요",
                            style = typography.body5,
                            color = colors.black.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    // ✅ 선택된 이미지 프리뷰
                    AsyncImage(
                        model = selectedUri,
                        contentDescription = "selected image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(18.dp))
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ✅ 갤러리에서 선택 버튼 (스샷처럼 흰색/테두리)
            OutlinedButton(
                onClick = {
                    pickerLauncher.launch(
                        ActivityResultContracts.PickVisualMedia.Request.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            .build()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, colors.gray300),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = colors.white,
                    contentColor = colors.black
                )
            ) {
                Text(text = "갤러리에서 선택", style = typography.body1)
            }

            Spacer(Modifier.height(16.dp))

            // ✅ 감정 분석하기 버튼 (이미지 선택 전에는 Disable)
            val canAnalyze = selectedUri != null

            GwangSanStateButton(
                text = "감정 분석하기",
                state = if (canAnalyze) ButtonState.Enable else ButtonState.Disable,
                onClick = { selectedUri?.let(onAnalyzeClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}
