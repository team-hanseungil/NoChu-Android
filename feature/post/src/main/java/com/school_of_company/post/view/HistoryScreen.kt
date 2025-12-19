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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.school_of_company.network.dto.reponse.EmotionHistoryResponse
import com.school_of_company.network.dto.reponse.EmotionRecordResponse
import com.school_of_company.post.viewmodel.PostViewModel // 👈 PostViewModel import
import com.school_of_company.post.viewmodel.uiState.HistoryUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

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

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7)) // 배경색 설정
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
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "지금까지의 감정 분석 기록입니다",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // --- UI 상태에 따른 화면 분기 ---
            when (uiState) {
                HistoryUiState.Loading -> LoadingState()
                is HistoryUiState.Success -> HistoryContent((uiState as HistoryUiState.Success).response)
                is HistoryUiState.Error -> ErrorState((uiState as HistoryUiState.Error).message)
                HistoryUiState.Empty -> EmptyState()
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
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "기록된 감정 분석 결과가 없습니다.",
            color = Color.Gray,
            fontSize = 16.sp
        )
    }
}

@Composable
fun ErrorState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "오류 발생: $message",
            color = Color.Red,
            fontSize = 16.sp
        )
    }
}

@Composable
fun HistoryContent(response: EmotionHistoryResponse) {
    // 통계 요약 카드
    StatisticsCard(response = response)

    Spacer(modifier = Modifier.height(16.dp))

    // 감정 기록 목록
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(response.emotions) { record ->
            EmotionRecordItem(record = record)
        }
    }
}

// ----------------------------------------------------------------------
// --- 하위 UI 컴포넌트 ---
// ----------------------------------------------------------------------

@Composable
fun StatisticsCard(response: EmotionHistoryResponse) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatisticItem(value = response.totalRecords.toString(), label = "총 기록", valueColor = Color(0xFF5E85E0))
            StatisticItem(value = "${response.averageConfidence}%", label = "평균 신뢰도", valueColor = Color(0xFFB57EDC))
            StatisticItem(value = response.streak.toString(), label = "연속 기록", valueColor = Color(0xFFC76D92))
        }
    }
}

@Composable
fun StatisticItem(value: String, label: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = valueColor)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun EmotionRecordItem(record: EmotionRecordResponse) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.KOREA)
    val displayFormatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA)

    val dateText = try {
        LocalDate.parse(record.date, formatter).format(displayFormatter)
    } catch (e: DateTimeParseException) {
        record.date
    } catch (e: Exception) {
        record.date
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 이미지 썸네일 (회색 박스로 대체)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE0E0E0))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // 날짜 정보
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📅", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = dateText, fontSize = 14.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                // 감정 이름
                Text(text = record.emotion, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))

                // 신뢰도 바
                LinearProgressIndicator(
                    progress = record.confidence / 100f,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFFB57EDC),
                    trackColor = Color(0xFFE0E0E0)
                )
                Spacer(modifier = Modifier.height(4.dp))
                // 신뢰도 텍스트
                Text(text = "${record.confidence}% 신뢰도", fontSize = 12.sp, color = Color.Gray)
            }
        }
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
        EmotionRecordResponse(1, "2025-12-19", "기쁨", 95),
        EmotionRecordResponse(2, "2025-12-18", "평온", 70),
        EmotionRecordResponse(3, "2025-12-17", "만족", 88),
        EmotionRecordResponse(4, "2025-12-16", "슬픔", 55),
        EmotionRecordResponse(5, "2025-12-15", "분노", 62),
    )
)

/**
 * Preview: HistoryContent (통계 및 목록)만 미리보기
 */
@Preview(showBackground = true, name = "History Content Success")
@Composable
fun PreviewHistoryContent() {
    Column(modifier = Modifier.padding(24.dp).background(Color(0xFFF7F7F7))) {
        Text(text = "감정 기록", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        Text(text = "지금까지의 감정 분석 기록입니다", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 24.dp))
        HistoryContent(response = mockHistoryResponse)
    }
}

/**
 * Preview: 전체 화면 미리보기 (Mock 상태)
 *
 * NOTE: Hilt ViewModel을 사용하는 HistoryScreen 자체는 Preview가 어렵기 때문에,
 * 모방된 로딩 상태를 보여주거나, HistoryContent를 직접 호출하여 성공 상태를 보여줍니다.
 * 여기서는 성공 상태의 컨텐츠를 보여줍니다.
 */
@Preview(showBackground = true, name = "History Screen Full Preview")
@Composable
fun PreviewHistoryScreen() {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .background(Color(0xFFF7F7F7))
        ) {
            // --- 헤더 영역 ---
            Text(
                text = "감정 기록",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "지금까지의 감정 분석 기록입니다",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Mock Success State Content
            HistoryContent(response = mockHistoryResponse)
        }
    }
}