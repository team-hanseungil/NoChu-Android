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

const val DEFAULT_EMOJI = "😶"
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

@Composable
fun HistoryScreen(
    viewModel: PostViewModel = hiltViewModel()
) {
    val uiState by viewModel.emotionHistoryUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEmotionHistory()
    }

    GwangSanTheme { colors, typography ->
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(GwangSanColor.gray100)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
        contentPadding = PaddingValues(bottom = 16.dp),
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



