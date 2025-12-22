package com.school_of_company.nochumain

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.school_of_company.design_system.R
import com.school_of_company.design_system.theme.GwangSanTheme
import com.school_of_company.design_system.theme.GwangSanTypography
import com.school_of_company.design_system.theme.color.GwangSanColor
import com.school_of_company.model.auth.request.EmotionResponseModel
import com.school_of_company.signin.viewmodel.SignInViewModel
import com.school_of_company.signin.viewmodel.uistate.PostFaceUiState
import kotlin.math.roundToInt

// ======================================================
// Route
// ======================================================
@Composable
fun PhotoUploadRoute(
    memberId: Long,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var selectedIndex by remember { mutableIntStateOf(1) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val uiState by viewModel.postFaceUiState.collectAsState()

    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
            if (uri != null) {
                viewModel.resetPostFaceState()
                selectedIndex = 1
            }
        }

    Scaffold(
        bottomBar = {
            NavigationContent(
                selectedIndex = selectedIndex,
                onItemSelected = { index -> selectedIndex = index }
            )
        }
    ) { paddingValues ->
        when (selectedIndex) {
            1 -> {
                PhotoUploadContent(
                    modifier = Modifier.padding(paddingValues),
                    selectedImageUri = selectedImageUri,
                    uiState = uiState,
                    onPickImage = { pickImageLauncher.launch("image/*") },
                    onPostClick = {
                        val uri = selectedImageUri ?: return@PhotoUploadContent
                        viewModel.postFace(
                            memberId = memberId,
                            context = context,
                            image = uri
                        )
                        selectedIndex = 2
                    }
                )
            }

            2 -> {
                AnalysisContent(
                    modifier = Modifier.padding(paddingValues),
                    selectedImageUri = selectedImageUri,
                    uiState = uiState,
                    onGoPickAgain = { selectedIndex = 1 },
                    onMusicClick = { selectedIndex = 3 }
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(Color.White)
                )
            }
        }
    }
}

// ======================================================
// Navigation Bar
// ======================================================
@Composable
fun RowScope.NoChuNavigationBarItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    enabled: Boolean = true,
    label: @Composable () -> Unit,
    onClick: () -> Unit,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
) {
    NavigationBarItem(
        enabled = enabled,
        selected = selected,
        label = label,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = GwangSanColor.purple,
            unselectedIconColor = GwangSanColor.gray500,
            selectedTextColor = GwangSanColor.purple,
            unselectedTextColor = GwangSanColor.gray500,
            indicatorColor = GwangSanColor.white
        ),
        modifier = modifier
    )
}

@Composable
fun NoChuNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    GwangSanTheme { colors, _ ->
        Column {
            HorizontalDivider(
                thickness = 1.dp,
                color = colors.gray200
            )

            NavigationBar(
                containerColor = colors.white,
                contentColor = colors.gray200,
                tonalElevation = 0.dp,
                content = content,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun NavigationContent(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf("홈", "사진", "분석", "음악", "기록")

    val icons = listOf(
        R.drawable.home,
        R.drawable.camera_icon,
        R.drawable.chartbar_icon,
        R.drawable.music_icon,
        R.drawable.history_icon,
    )

    GwangSanTheme { colors, typography ->
        NoChuNavigationBar {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                val iconRes = icons[index]

                NoChuNavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = item,
                            tint = colors.gray500
                        )
                    },
                    selectedIcon = {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = item,
                            tint = colors.purple
                        )
                    },
                    label = {
                        Text(
                            text = item,
                            style = typography.label.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        )
                    },
                    selected = isSelected,
                    onClick = { onItemSelected(index) },
                )
            }
        }
    }
}

// ======================================================
// Upload Screen
// ======================================================
@Composable
fun PhotoUploadContent(
    modifier: Modifier = Modifier,
    selectedImageUri: Uri?,
    uiState: PostFaceUiState,
    onPickImage: () -> Unit,
    onPostClick: () -> Unit,
) {
    GwangSanTheme { colors, typography ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(colors.white)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "사진 업로드",
                style = typography.titleMedium2,
                color = colors.black
            )

            Text(
                text = "분석할 사진을 선택해주세요",
                style = typography.body4,
                color = colors.gray600
            )

            Spacer(modifier = Modifier.height(30.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.white,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, colors.gray200, RoundedCornerShape(8.dp))
                            .background(colors.gray100),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri == null) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.upload),
                                    contentDescription = "이미지 선택 아이콘",
                                    tint = colors.gray500,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "이미지를 선택해주세요",
                                    style = typography.body2,
                                    color = colors.gray500
                                )
                            }
                        } else {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "선택된 이미지",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onPickImage,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.white,
                            contentColor = colors.gray800
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(1.dp, colors.gray200, RoundedCornerShape(10.dp)),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_image),
                            contentDescription = "갤러리 아이콘",
                            tint = colors.gray800,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = " 갤러리에서 선택",
                            style = typography.body1.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val isLoading = uiState is PostFaceUiState.Loading
                    val canPost = selectedImageUri != null && !isLoading

                    Button(
                        onClick = onPostClick,
                        enabled = canPost,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.subPOPule,
                            contentColor = colors.white,
                            disabledContainerColor = colors.gray200,
                            disabledContentColor = colors.gray500
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            text = "감정 분석하기",
                            style = typography.body1.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (uiState) {
                        is PostFaceUiState.Success -> {
                            Text(
                                text = "업로드 성공",
                                style = typography.body2,
                                color = colors.purple
                            )
                        }

                        is PostFaceUiState.Error -> {
                            Text(
                                text = "업로드 실패: ${uiState.exception.message ?: "알 수 없는 오류"}",
                                style = typography.body2,
                                color = colors.error
                            )
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}

// ======================================================
// Analysis Screen
// ======================================================
private data class EmotionItem(
    val label: String,
    val percent: Int
)

@Composable
fun AnalysisContent(
    modifier: Modifier = Modifier,
    selectedImageUri: Uri?,
    uiState: PostFaceUiState,
    onGoPickAgain: () -> Unit,
    onMusicClick: () -> Unit
) {
    GwangSanTheme { colors, typography ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(colors.white)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "분석",
                style = typography.titleMedium2,
                color = colors.black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.white,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, colors.gray200, RoundedCornerShape(8.dp))
                            .background(colors.gray100),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri == null) {
                            Text(
                                text = "선택된 이미지가 없습니다",
                                style = typography.body2,
                                color = colors.gray500
                            )
                        } else {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "선택된 이미지",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (uiState) {
                        is PostFaceUiState.Loading -> {
                            Text(
                                text = "분석 중...",
                                style = typography.body2,
                                color = colors.gray600
                            )
                        }

                        is PostFaceUiState.Error -> {
                            Text(
                                text = "분석 실패: ${uiState.exception.message ?: "알 수 없는 오류"}",
                                style = typography.body2,
                                color = colors.error
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = onGoPickAgain,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.white,
                                    contentColor = colors.gray800
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .border(1.dp, colors.gray200, RoundedCornerShape(10.dp)),
                            ) {
                                Text("사진 다시 선택하기", style = typography.body1)
                            }
                        }

                        is PostFaceUiState.Success -> {
                            val data = uiState.data  // EmotionResponse
                            val emotionItems = data.toEmotionItems()

                            Text(
                                text = "감정 분석",
                                style = typography.titleSmall,
                                color = colors.black
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            emotionItems.forEach { item ->
                                EmotionRow(
                                    label = item.label,
                                    percent = item.percent,
                                    fillColor = colors.purple,
                                    trackColor = colors.gray100,
                                    typography = typography
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = colors.white,
                                shadowElevation = 0.dp,
                                border = BorderStroke(1.dp, colors.gray200)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text(
                                        text = "AI 코멘트",
                                        style = typography.titleSmall,
                                        color = colors.black
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = data.comment,
                                        style = typography.body2,
                                        color = colors.gray700
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = onMusicClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.subPOPule,
                                    contentColor = colors.white
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.music_icon),
                                    contentDescription = "음악",
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "  음악 추천받기",
                                    style = typography.body1.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun EmotionRow(
    label: String,
    percent: Int,
    fillColor: Color,
    trackColor: Color,
    typography: GwangSanTypography
) {
    val p = percent.coerceIn(0, 100)
    val progress = p / 100f

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = typography.body2)
            Text(
                text = "${p}%",
                style = typography.body2,
                textAlign = TextAlign.End
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(trackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(fillColor)
            )
        }
    }
}

// ======================================================
// EmotionResponse -> EmotionRow Data
// ======================================================
private fun EmotionResponseModel.toEmotionItems(): List<EmotionItem> {
    fun pct(v: Double): Int = (v * 100).roundToInt().coerceIn(0, 100)

    return listOf(
        EmotionItem("행복", pct(emotions.happy)),
        EmotionItem("놀람", pct(emotions.surprise)),
        EmotionItem("분노", pct(emotions.anger)),
        EmotionItem("불안", pct(emotions.anxiety)),
        EmotionItem("상처", pct(emotions.hurt)),
        EmotionItem("슬픔", pct(emotions.sad)),
    ).sortedByDescending { it.percent }
}

// ======================================================
// Preview
// ======================================================
@Preview(showBackground = true)
@Composable
fun FullPhotoUploadScreenPreview() {
    GwangSanTheme { _, _ ->
        PhotoUploadContent(
            selectedImageUri = null,
            uiState = PostFaceUiState.Loading,
            onPickImage = {},
            onPostClick = {}
        )
    }
}
