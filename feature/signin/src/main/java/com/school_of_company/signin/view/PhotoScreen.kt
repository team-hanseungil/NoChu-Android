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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.school_of_company.model.music.response.PlaylistModel
import com.school_of_company.network.dto.reponse.EmotionHistoryResponse
import com.school_of_company.network.dto.reponse.EmotionRecordResponse
import com.school_of_company.post.viewmodel.PostViewModel
import com.school_of_company.post.viewmodel.uiState.HistoryUiState
import com.school_of_company.signin.view.MusicScreen
import com.school_of_company.signin.viewmodel.SignInViewModel
import com.school_of_company.signin.viewmodel.uistate.PostFaceUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.math.roundToInt

const val DEFAULT_EMOJI = "❓"
const val EMOJI_SIZE = 40.0 // 40.sp

const val EMOJI_CONTAINER_SIZE = 56.0 // 56.dp

const val EMOJI_CONTAINER_CORNER_RADIUS = 8.0 // 8.dp

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

@Composable
fun PhotoUploadRoute(
    memberId: Long,
    viewModel: SignInViewModel = hiltViewModel(),
    // 🚀 수정: 음악 상세 화면 네비게이션 콜백 추가
    onNavigateToMusicDetail: (Long) -> Unit
) {
    val context = LocalContext.current

    // "사진"은 인덱스 1, "기록"은 인덱스 4
    var selectedIndex by remember { mutableIntStateOf(1) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val uiState by viewModel.postFaceUiState.collectAsState()

    // HistoryScreen에서 사용할 PostViewModel을 hiltViewModel로 주입받습니다.
    val historyViewModel: PostViewModel = hiltViewModel()

    // MusicScreen에서 사용할 ViewModel을 hiltViewModel로 주입받습니다. (PhotoUploadRoute의 ViewModel과 분리)
    val musicViewModel: SignInViewModel = hiltViewModel()

    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
            if (uri != null) {
                viewModel.resetPostFaceState()
                selectedIndex = 1 // 이미지 선택 후 다시 사진 업로드 탭으로 돌아옵니다.
            }
        }

    Scaffold(
        bottomBar = {
            NavigationContent(
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    selectedIndex = index
                    // History 탭이 선택되었을 때만 데이터 로드를 시작합니다.
                    if (index == 4) {
                        historyViewModel.loadEmotionHistory(memberId)
                    }
                }
            )
        }
    ) { paddingValues ->
        when (selectedIndex) {
            0 -> {
                // 홈 화면 (미구현)
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(GwangSanColor.white),
                    contentAlignment = Alignment.Center
                ) { Text("홈 화면 (미구현)") }
            }
            1 -> { // 사진 업로드
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
            2 -> { // 분석
                AnalysisContent(
                    modifier = Modifier.padding(paddingValues),
                    selectedImageUri = selectedImageUri,
                    uiState = uiState,
                    onGoPickAgain = { selectedIndex = 1 },
                    onMusicClick = { selectedIndex = 3 } // 음악 탭으로 이동
                )
            }
            3 -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(GwangSanColor.gray100)
                ) {
                    MusicScreen(
                        viewModel = musicViewModel,
                        selectedIndex = selectedIndex,
                        onItemSelected = { index -> selectedIndex = index },
                        memberId = memberId,
                        onNavigateToDetails = { playlist: PlaylistModel ->
                            onNavigateToMusicDetail(playlist.id)
                        }
                    )
                }
            }
            4 -> { // 기록
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    HistoryScreenInternal(viewModel = historyViewModel, memberId = memberId)
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(GwangSanColor.white)
                )
            }
        }
    }
}

@Composable
fun HistoryScreenInternal(
    viewModel: PostViewModel,
    memberId: Long
) {
    // PostViewModel의 emotionHistoryUiState를 관찰합니다.
    val uiState by viewModel.emotionHistoryUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEmotionHistory(memberId)
    }

    // GwangSanTheme 적용
    GwangSanTheme { colors, typography ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // 배경색을 디자인 시스템의 gray100으로 설정
                .background(GwangSanColor.gray100)
                .padding(horizontal = 24.dp)
        ) {
            // --- 헤더 영역 ---
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

            // --- UI 상태에 따른 화면 분기 ---
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
            EmotionRecordItem(record = record, typography = typography, fixedProgressBarColor = fixedProgressBarColor, trackColor = trackColor)
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
    } catch (e: DateTimeParseException) {
        record.date
    } catch (e: Exception) {
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
                            val data = uiState.data
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

                            // Music 탭으로 이동하는 버튼 (onMusicClick 콜백 실행)
                            Button(
                                onClick = onMusicClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.subPOPule,
                                    contentColor = colors.white,
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                Text(
                                    text = "분석 기반 음악 추천 받기",
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