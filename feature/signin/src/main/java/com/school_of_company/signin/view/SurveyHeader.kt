package com.school_of_company.signin.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.school_of_company.design_system.component.toast.makeToast
import com.school_of_company.signin.viewmodel.SignInViewModel
import com.school_of_company.signin.viewmodel.uistate.PostSuveyUiState

// ---------- 색상 정의 ----------
private val PurpleMain = Color(0xFFB8AFFB)
private val PurpleDark = Color(0xFF7C6FF0)
private val ChipBackground = Color(0xFFF1EFFB)
private val CardBackground = Color(0xFFFFFFFF)
private val ScreenBackground = Color(0xFFF5F4FA)

// ---------- 데이터 ----------
private val genres = listOf("K-pop", "팝", "힙합", "R&B", "발라드", "인디", "락", "EDM", "로파이·재즈")

// value(영어) - title(질문 문구) - subtitle(예시, nullable)
private val sadMoodOptions = listOf(
    Triple("immerse", "그 감정에 맞는 노래를 들으며 몰입한다", "예: 슬프면 슬픈 노래"),
    Triple("opposite", "정반대 분위기의 노래로 기분을 전환한다", "예: 슬프면 신나는 노래"),
    Triple("calm", "차분하고 편안한 노래로 마음을 가라앉힌다", null)
)

// value(영어) - title(질문 문구)
private val happyMoodOptions = listOf(
    "amplify" to "그 텐션을 더 끌어올리는 노래를 듣는다",
    "relax" to "잔잔하게 여운을 즐기는 노래를 듣는다"
)

// ---------- Route: 뷰모델 연결 + 네비게이션 콜백 ----------
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SurveyRoute(
    onBackClick: () -> Unit,
    onSurveyComplete: () -> Unit,
    onErrorToast: (throwable: Throwable?, message: Int?) -> Unit,
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val postSurveyUiState by viewModel.postSuveyUiState.collectAsStateWithLifecycle()

    var selectedGenres by remember { mutableStateOf(setOf<String>()) }
    var artistInput by remember { mutableStateOf("") }
    var selectedSadOption by remember { mutableStateOf<String?>(null) }
    var selectedHappyOption by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(postSurveyUiState) {
        when (postSurveyUiState) {
            is PostSuveyUiState.Idle -> Unit
            is PostSuveyUiState.Loading -> Unit
            is PostSuveyUiState.Success -> {
                makeToast(context, "설문 제출 완료")
                onSurveyComplete()
            }
            is PostSuveyUiState.Error -> {
                val e = (postSurveyUiState as PostSuveyUiState.Error).exception
                onErrorToast(e, null)
            }
        }
    }

    SurveyScreen(
        selectedGenres = selectedGenres,
        onGenreToggle = { genre ->
            selectedGenres = when {
                selectedGenres.contains(genre) -> selectedGenres - genre
                selectedGenres.size < 3 -> selectedGenres + genre
                else -> selectedGenres
            }
        },
        artistInput = artistInput,
        onArtistInputChange = { artistInput = it },
        selectedSadOption = selectedSadOption,
        onSadOptionSelect = { selectedSadOption = it },
        selectedHappyOption = selectedHappyOption,
        onHappyOptionSelect = { selectedHappyOption = it },
        onCompleteClick = {
            viewModel.postSurvey(
                genres = selectedGenres.toList(),
                artists = artistInput.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                sadMoodOption = selectedSadOption.orEmpty(),
                happyMoodOption = selectedHappyOption.orEmpty()
            )
        }
    )
}

// ---------- Screen: 순수 UI ----------
@Composable
private fun SurveyScreen(
    selectedGenres: Set<String>,
    onGenreToggle: (String) -> Unit,
    artistInput: String,
    onArtistInputChange: (String) -> Unit,
    selectedSadOption: String?,
    onSadOptionSelect: (String) -> Unit,
    selectedHappyOption: String?,
    onHappyOptionSelect: (String) -> Unit,
    onCompleteClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground),
        contentPadding = PaddingValues(bottom = 40.dp) // 버튼 아래 패드 내비게이션 바 등을 고려한 안전 여백
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SurveyHeader()
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SurveyCard {
                GenreQuestion(
                    selectedGenres = selectedGenres,
                    onGenreToggle = onGenreToggle
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            SurveyCard {
                ArtistQuestion(
                    value = artistInput,
                    onValueChange = onArtistInputChange
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            SurveyCard {
                Text(
                    "기분이 안 좋을 때 나는 보통...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                sadMoodOptions.forEachIndexed { index, (value, title, subtitle) ->
                    RadioOptionCard(
                        title = title,
                        subtitle = subtitle,
                        selected = selectedSadOption == value,
                        onClick = { onSadOptionSelect(value) }
                    )
                    if (index != sadMoodOptions.lastIndex) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            SurveyCard {
                Text(
                    "반대로 기분이 좋을 때 나는 보통...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                happyMoodOptions.forEachIndexed { index, (value, title) ->
                    RadioOptionCard(
                        title = title,
                        subtitle = null,
                        selected = selectedHappyOption == value,
                        onClick = { onHappyOptionSelect(value) }
                    )
                    if (index != happyMoodOptions.lastIndex) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp)) // 마지막 카드와 완료 버튼 사이 여백
        }

        // ★ 완료 버튼을 스크롤 내부 아이템으로 안착시켜 화면 짤림 방지
        item {
            Button(
                onClick = onCompleteClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurpleDark)
            ) {
                Text("완료하고 시작하기", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// ---------- 헤더 ----------
@Composable
private fun SurveyHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Call,
            contentDescription = null,
            modifier = Modifier.size(70.dp),
            tint = PurpleMain
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "음악 취향 설문",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "더 정확한 추천을 위해 알려주세요",
            color = Color.Gray,
            fontSize = 17.sp
        )
    }
}

// ---------- 공통 카드 ----------
@Composable
private fun SurveyCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(CardBackground, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        content()
    }
}

// ---------- 장르 선택 질문 ----------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnScope.GenreQuestion(
    selectedGenres: Set<String>,
    onGenreToggle: (String) -> Unit
) {
    Text("평소 즐겨 듣는 장르는?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        "복수선택, 최대 3개 (${selectedGenres.size}/3)",
        color = Color.Gray,
        fontSize = 14.sp
    )
    Spacer(modifier = Modifier.height(14.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        genres.forEach { genre ->
            GenreChip(
                text = genre,
                selected = selectedGenres.contains(genre),
                onClick = { onGenreToggle(genre) }
            )
        }
    }
}

@Composable
private fun GenreChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) PurpleMain else ChipBackground,
                shape = CircleShape
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Black,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 15.sp
        )
    }
}

// ---------- 아티스트 입력 질문 ----------
@Composable
private fun ColumnScope.ArtistQuestion(value: String, onValueChange: (String) -> Unit) {
    Text("좋아하는 아티스트를 알려주세요", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    Spacer(modifier = Modifier.height(4.dp))
    Text("선택, 1~3명 (쉼표로 구분)", color = Color.Gray, fontSize = 14.sp)
    Spacer(modifier = Modifier.height(14.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("예: 아이유, 잔나비", color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    )
}

// ---------- 라디오 옵션 카드 ----------
@Composable
private fun RadioOptionCard(
    title: String,
    subtitle: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                color = if (selected) PurpleDark else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = PurpleDark)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                color = Color.Black
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}