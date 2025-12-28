package com.school_of_company.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.school_of_company.design_system.theme.GwangSanTheme
import com.school_of_company.design_system.theme.color.ColorTheme
import com.school_of_company.design_system.theme.color.GwangSanColor
import com.school_of_company.design_system.theme.GwangSanTypography
import com.school_of_company.network.dto.post.response.EmotionHistoryResponse
import com.school_of_company.network.dto.post.response.EmotionRecordResponse
import com.school_of_company.post.viewmodel.PostViewModel
import com.school_of_company.post.viewmodel.uiState.HistoryUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

const val DEFAULT_EMOJI = "😶" // 중립적인 얼굴로 변경
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

/**
 * 감정 기록 화면의 메인 컴포넌트입니다.
 * PostViewModel에 통합된 감정 기록 조회 기능을 사용합니다.
 */
@Composable
fun HistoryScreen(
    // PostViewModel을 Hilt를 통해 주입받아 사용합니다.
    viewModel: PostViewModel = hiltViewModel()
) {
    // PostViewModel의 emotionHistoryUiState를 관찰합니다.
    val uiState by viewModel.emotionHistoryUiState.collectAsState()

    // 임시 멤버 ID (PostViewModel에서 사용된 값과 동일하게 가정)
    val currentMemberId: Long = 1L

    // 화면이 처음 나타날 때 데이터를 로드합니다.
    LaunchedEffect(Unit) {
        viewModel.loadEmotionHistory(currentMemberId)
    }

    // GwangSanTheme 적용
    GwangSanTheme { colors, typography ->
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                // 배경색을 디자인 시스템의 gray100 (Color(0xFFF5F6F8) 또는 유사 색상)으로 설정
                .background(GwangSanColor.gray100)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
            ) {
                // --- 헤더 영역 ---
                Text(
                    text = "감정 기록",
                    // titleLarge (fontSize = 30.sp, fontWeight = SemiBold) 사용
                    style = typography.titleLarge,
                    color = GwangSanColor.black,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "지금까지의 감정 분석 기록입니다",
                    // body5 (fontSize = 14.sp, fontWeight = Normal) 사용
                    style = typography.body5,
                    color = GwangSanColor.gray700, // Color.Gray 대신 gray700 사용
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
}

// ----------------------------------------------------------------------
// --- UI 상태별 컴포넌트 ---
// ----------------------------------------------------------------------

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = GwangSanColor.main500) // main500 사용
    }
}

@Composable
fun EmptyState(typography: GwangSanTypography) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "기록된 감정 분석 결과가 없습니다.",
            color = GwangSanColor.gray700,
            style = typography.body4 // body4 (fontSize = 16.sp, fontWeight = Normal) 사용
        )
    }
}

@Composable
fun ErrorState(message: String, typography: GwangSanTypography) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "오류 발생: $message",
            color = GwangSanColor.error, // error 색상 사용
            style = typography.body4 // body4 (fontSize = 16.sp, fontWeight = Normal) 사용
        )
    }
}

@Composable
fun HistoryContent(
    response: EmotionHistoryResponse,
    colors: ColorTheme,
    typography: GwangSanTypography
) {
    // 통계 요약 카드
    StatisticsCard(response = response, colors = colors, typography = typography)

    Spacer(modifier = Modifier.height(16.dp))

    // 신뢰도 바 색상 설정 (purple 변수를 사용하여 일관성 유지)
    val fixedProgressBarColor = colors.purple
    val trackColor = colors.gray200

    // 감정 기록 목록
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // EmotionRecordItem 로직 통합
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

    // 감정에 맞는 이모지를 찾거나 기본 이모지를 사용합니다.
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
            // --- 이모지 아이콘 영역 ---
            Box(
                modifier = Modifier
                    .size(EMOJI_CONTAINER_SIZE.dp) // 56.dp
                    .clip(RoundedCornerShape(EMOJI_CONTAINER_CORNER_RADIUS.dp)) // 8.dp
                    .background(GwangSanColor.gray200), // 배경색 유지
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = EMOJI_SIZE.sp // 40.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // 날짜 정보
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = DATE_ICON, style = typography.caption) // 📅 아이콘
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = dateText, style = typography.caption, color = GwangSanColor.gray700)
                }
                Spacer(modifier = Modifier.height(4.dp))
                // 감정 이름
                Text(
                    text = record.emotion,
                    style = typography.body1, // 18.sp SemiBold
                    color = GwangSanColor.black
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 신뢰도 바 (커스텀)
                val confidenceRatio = record.confidence / 100f // Int를 Float으로 변환하여 비율 계산

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
                // 신뢰도 텍스트
                Text(
                    text = "${record.confidence}% 신뢰도",
                    style = typography.caption, // 12.sp Normal
                    color = GwangSanColor.gray700
                )
            }
        }
    }
}


// ----------------------------------------------------------------------
// --- 하위 UI 컴포넌트 ---
// ----------------------------------------------------------------------

@Composable
fun StatisticsCard(response: EmotionHistoryResponse, colors: ColorTheme, typography: GwangSanTypography) {
    // subPOPule(0xFF5E5BD6)와 purple(0xFF9E7FFF) 색상을 통계 항목에 사용
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
            // titleLarge 기반, 크기만 32.sp로 조정
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

// ----------------------------------------------------------------------
// --- Preview 컴포넌트 ---
// ----------------------------------------------------------------------

/**
 * 데이터가 성공적으로 로드된 상태의 미리보기를 위한 Mock 데이터
 */
private val mockHistoryResponse = EmotionHistoryResponse(
    totalRecords = 12,
    averageConfidence = 78,
    streak = 15,
    emotions = listOf(
        EmotionRecordResponse(1, "2025-12-19", "행복", 95),
        EmotionRecordResponse(2, "2025-12-18", "평온", 70),
        EmotionRecordResponse(3, "2025-12-17", "즐거움", 88),
        EmotionRecordResponse(4, "2025-12-16", "차분함", 55),
        EmotionRecordResponse(5, "2025-12-15", "설렘", 62),
        EmotionRecordResponse(6, "2025-12-14", "슬픔", 80),
        EmotionRecordResponse(7, "2025-12-13", "불안", 40),
        EmotionRecordResponse(8, "2025-12-12", "화남", 90),
    )
)

/**
 * Preview: HistoryContent (통계 및 목록)만 미리보기
 */
@Preview(showBackground = true, name = "History Content Success")
@Composable
fun PreviewHistoryContent() {
    GwangSanTheme { colors, typography ->
        Column(
            modifier = Modifier
                .padding(24.dp)
                .background(GwangSanColor.gray100) // 디자인 시스템 색상 적용
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
            HistoryContent(response = mockHistoryResponse, colors = colors, typography = typography)
        }
    }
}

/**
 * Preview: 전체 화면 미리보기 (Mock 상태)
 */
@Preview(showBackground = true, name = "History Screen Full Preview")
@Composable
fun PreviewHistoryScreen() {
    GwangSanTheme { colors, typography ->
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .background(GwangSanColor.gray100) // 디자인 시스템 색상 적용
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

                // Mock Success State Content
                HistoryContent(response = mockHistoryResponse, colors = colors, typography = typography)
            }
        }
    }
}