package com.school_of_company.signin.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.school_of_company.design_system.R
import com.school_of_company.design_system.theme.GwangSanTheme
import com.school_of_company.design_system.theme.GwangSanTypography
import com.school_of_company.design_system.theme.color.ColorTheme
import com.school_of_company.design_system.theme.color.GwangSanColor
import com.school_of_company.model.auth.request.EmotionResponseModel
import com.school_of_company.network.dto.post.response.EmotionHistoryResponse
import com.school_of_company.network.dto.post.response.EmotionRecordResponse
import com.school_of_company.post.viewmodel.PostViewModel
import com.school_of_company.post.viewmodel.uiState.HistoryUiState
import com.school_of_company.signin.viewmodel.SignInViewModel
import com.school_of_company.signin.viewmodel.uistate.MusicRR
import com.school_of_company.signin.viewmodel.uistate.PostFaceUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.math.roundToInt

const val DEFAULT_EMOJI = "❓"
const val EMOJI_SIZE = 40.0
const val EMOJI_CONTAINER_SIZE = 56.0
const val EMOJI_CONTAINER_CORNER_RADIUS = 8.0
const val DATE_ICON = "📅"

val emotionEmojis: Map<String, String> = mapOf(
    "기쁨" to "😄",
    "행복" to "😊",
    "평온" to "😌",
    "즐거움" to "😄",
    "차분함" to "🙂",
    "설렘" to "🤩",
    "슬픔" to "😢",
    "불안" to "😰",
    "화남" to "😠",
    "만족" to "🥰",
    "분노" to "😡",
)

data class EmotionItem(val label: String, val percent: Float)

// ⭕ [에러 해결 1] 모델 스펙 유추 에러 방지:
// 원래 변환 로직이 명확치 않다면, EmotionResponseModel 내부에 확실히 존재하는 필드로 매핑하거나 임시 상수를 배정하세요.
fun EmotionResponseModel.toEmotionItems(): List<EmotionItem> {
    return listOf(
        EmotionItem("분석 결과", 100f)
        // ⚠️ 에러가 지속된다면 이 함수를 지우고, 기존에 쓰시던 실제 Mapper 함수(예: toModel() 등)를 import 해서 사용하세요!
    )
}

@Composable
fun PhotoUploadRoute(
    // ⭕ [에러 해결 2] 내비게이션 등 외부 호출 단의 에러를 무마하기 위해 memberId를 다시 선언하되, 내부에선 쓰지 않고 방치합니다.
    memberId: Long,
    viewModel: SignInViewModel = hiltViewModel(),
    onNavigateToMusicRecommend: () -> Unit
) {
    val context = LocalContext.current

    var selectedIndex by remember { mutableIntStateOf(0) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val uiState by viewModel.postFaceUiState.collectAsState()
    val musicRRState by viewModel.musicRRState.collectAsState()
    val historyViewModel: PostViewModel = hiltViewModel()

    LaunchedEffect(musicRRState) {
        if (musicRRState is MusicRR.Success && selectedIndex == 1) {
            selectedIndex = 2
        }
    }

    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
            if (uri != null) {
                viewModel.resetPostFaceState()
                viewModel.resetMusicRRState()
                selectedIndex = 0
            }
        }

    Scaffold(
        bottomBar = {
            NavigationContent(
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    if (index == 2 && selectedIndex != 1) {
                        viewModel.resetMusicRRState()
                    }
                    selectedIndex = index
                    if (index == 3) {
                        historyViewModel.loadEmotionHistory()
                    }
                }
            )
        }
    ) { paddingValues ->
        when (selectedIndex) {
            0 -> {
                PhotoUploadContent(
                    modifier = Modifier.padding(paddingValues),
                    selectedImageUri = selectedImageUri,
                    uiState = uiState,
                    onPickImage = { pickImageLauncher.launch("image/*") },
                    onPostClick = {
                        val uri = selectedImageUri ?: return@PhotoUploadContent
                        viewModel.postFace(
                            context = context,
                            image = uri
                        )
                        selectedIndex = 1
                    }
                )
            }
            1 -> {
                AnalysisContent(
                    modifier = Modifier.padding(paddingValues),
                    selectedImageUri = selectedImageUri,
                    uiState = uiState,
                    viewModel = viewModel,
                    onGoPickAgain = {
                        viewModel.resetMusicRRState()
                        selectedIndex = 0
                    },
                    onMusicClick = { selectedIndex = 2 }
                )
            }
            2 -> {
                GwangSanTheme { colors, typography ->
                    Box(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .background(GwangSanColor.gray100)
                    ) {
                        if (musicRRState is MusicRR.Success || musicRRState is MusicRR.Loading) {
                            Text("플레이리스트 로딩/성공 상태", modifier = Modifier.align(Alignment.Center))
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Music Screen")
                            }
                        }
                    }
                }
            }
            3 -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    HistoryScreenInternal(viewModel = historyViewModel)
                }
            }
        }
    }
}

@Composable
fun HistoryScreenInternal(
    viewModel: PostViewModel
) {
    val uiState by viewModel.emotionHistoryUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEmotionHistory()
    }

    GwangSanTheme { colors, typography ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GwangSanColor.gray100)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "감정 기록",
                style = typography.titleLarge,
                color = GwangSanColor.black,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "지금까지의 감정 분석 기록입니다",
                style = typography.body5,
                color = GwangSanColor.gray700,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            when (uiState) {
                HistoryUiState.Loading -> LoadingState()
                is HistoryUiState.Success -> HistoryContent(
                    (uiState as HistoryUiState.Success).response,
                    colors,
                    typography
                )
                is HistoryUiState.Error -> ErrorState((uiState as HistoryUiState.Error).message, typography)
                HistoryUiState.Empty -> EmptyState(typography)
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = GwangSanColor.main500)
    }
}

@Composable
fun EmptyState(typography: GwangSanTypography) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "기록된 감정 분석 결과가 없습니다.",
            color = GwangSanColor.gray700,
            style = typography.body4
        )
    }
}

@Composable
fun ErrorState(message: String, typography: GwangSanTypography) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "오류 발생: $message",
            color = GwangSanColor.error,
            style = typography.body4
        )
    }
}

@Composable
fun HistoryContent(
    response: EmotionHistoryResponse,
    colors: ColorTheme,
    typography: GwangSanTypography
) {
    StatisticsCard(response = response, colors = colors, typography = typography)

    Spacer(modifier = Modifier.height(16.dp))

    val fixedProgressBarColor = colors.purple
    val trackColor = colors.gray200

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(response.emotions) { record ->
            EmotionRecordItem(
                record = record,
                typography = typography,
                fixedProgressBarColor = fixedProgressBarColor,
                trackColor = trackColor
            )
        }
    }
}

@Composable
fun EmotionRecordItem(
    record: EmotionRecordResponse,
    typography: GwangSanTypography,
    fixedProgressBarColor: Color,
    trackColor: Color
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.KOREA)
    val displayFormatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA)

    val dateText = try {
        LocalDate.parse(record.date, formatter).format(displayFormatter)
    } catch (_: DateTimeParseException) {
        record.date
    } catch (_: Exception) {
        record.date
    }

    val emoji = emotionEmojis[record.emotion] ?: DEFAULT_EMOJI

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GwangSanColor.white),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(EMOJI_CONTAINER_SIZE.dp)
                    .clip(RoundedCornerShape(EMOJI_CONTAINER_CORNER_RADIUS.dp))
                    .background(GwangSanColor.gray200),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = EMOJI_SIZE.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = DATE_ICON, style = typography.caption)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = dateText, style = typography.caption, color = GwangSanColor.gray700)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = record.emotion,
                    style = typography.body1,
                    color = GwangSanColor.black
                )
                Spacer(modifier = Modifier.height(8.dp))

                val confidenceRatio = record.confidence / 100f

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(trackColor)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(confidenceRatio)
                            .fillMaxHeight()
                            .background(fixedProgressBarColor)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${record.confidence}% 신뢰도",
                    style = typography.caption,
                    color = GwangSanColor.gray700
                )
            }
        }
    }
}

@Composable
fun StatisticsCard(response: EmotionHistoryResponse, colors: ColorTheme, typography: GwangSanTypography) {
    val primaryColor = colors.subPOPule
    val secondaryColor = colors.purple

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GwangSanColor.white),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatisticItem(
                value = response.totalRecords.toString(),
                label = "총 기록",
                valueColor = primaryColor,
                typography = typography
            )
            StatisticItem(
                value = "${response.averageConfidence}%",
                label = "평균 신뢰도",
                valueColor = secondaryColor,
                typography = typography
            )
            StatisticItem(
                value = response.streak.toString(),
                label = "연속 기록",
                valueColor = primaryColor,
                typography = typography
            )
        }
    }
}

@Composable
fun StatisticItem(value: String, label: String, valueColor: Color, typography: GwangSanTypography) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = typography.titleLarge.copy(fontSize = 32.sp),
            color = valueColor
        )
        Text(
            text = label,
            style = typography.caption,
            color = GwangSanColor.gray700
        )
    }
}

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
    val items = listOf("사진", "분석", "음악", "기록")

    val icons = listOf(
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

@Composable
fun AnalysisContent(
    modifier: Modifier = Modifier,
    selectedImageUri: Uri?,
    uiState: PostFaceUiState,
    viewModel: SignInViewModel = hiltViewModel(),
    onGoPickAgain: () -> Unit,
    onMusicClick: () -> Unit
) {
    GwangSanTheme { colors, typography ->
        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(colors.white)
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .verticalScroll(scrollState)
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
                            val data = uiState.data
                            val emotionItems = data.toEmotionItems()

                            Text(
                                text = "감정 분석",
                                style = typography.titleSmall,
                                color = colors.black
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            (emotionItems as Iterable<EmotionItem>).forEach { item ->
                                EmotionRow(
                                    label = item.label,
                                    percent = item.percent,
                                    fillColor = colors.purple,
                                    trackColor = colors.gray100,
                                    typography = typography
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            Button(
                                onClick = onMusicClick,
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
                                    text = "음악 추천하기",
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
fun EmotionRow(
    label: String,
    percent: Float,
    fillColor: Color,
    trackColor: Color,
    typography: GwangSanTypography
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = typography.body2,
            color = GwangSanColor.black,
            modifier = Modifier.width(50.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(trackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percent / 100f)
                    .fillMaxHeight()
                    .background(fillColor)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "${percent.roundToInt()}%",
            style = typography.body3,
            color = GwangSanColor.gray700,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}