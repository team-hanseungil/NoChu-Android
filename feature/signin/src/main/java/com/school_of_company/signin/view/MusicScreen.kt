package com.school_of_company.presentation.music

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.school_of_company.design_system.R
import com.school_of_company.design_system.component.nochubottombar.NoChuNavigationBar
import com.school_of_company.design_system.component.nochubottombar.NoChuNavigationBarItem
import com.school_of_company.design_system.theme.GwangSanTheme
import com.school_of_company.design_system.theme.GwangSanTypography
import com.school_of_company.design_system.theme.color.ColorTheme
import com.school_of_company.signin.viewmodel.SignInViewModel
import com.school_of_company.signin.viewmodel.uistate.MusicUiState
import com.school_of_company.model.music.response.PlaylistListModel
import com.school_of_company.model.music.response.PlaylistModel

/**
 * 음악 추천 화면의 메인 콘텐츠입니다. (Scaffold 제외)
 * MusicUiState를 받아 실제 데이터를 표시합니다.
 * @param onNavigateToDetails 플레이리스트 클릭 시 상세 화면으로 이동하는 콜백
 */
@Composable
fun MusicScreenContent(
    colors: ColorTheme,
    typography: GwangSanTypography,
    uiState: MusicUiState,
    onNavigateToDetails: (PlaylistModel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.gray100)
    ) {
        // --- 헤더 영역 ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            Text(
                text = "음악 추천",
                style = typography.titleLarge,
                color = colors.black,
            )
            Text(
                text = "감정에 맞는 플레이리스트를 선택하세요",
                style = typography.body5,
                color = colors.gray700,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // --- 상태에 따른 본문 표시 ---
        when (uiState) {
            MusicUiState.Loading, MusicUiState.Idle -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MusicUiState.Success -> {
                val playlists = uiState.playlistList.playlists
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(playlists) { playlist ->
                        PlaylistListItem(
                            playlist = playlist,
                            colors = colors,
                            typography = typography,
                            onClick = { onNavigateToDetails(playlist) }
                        )
                    }
                }
            }
            is MusicUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("플레이리스트 로딩 실패: ${uiState.exception.message}", color = colors.error)
                }
            }
        }
    }
}

/**
 * 개별 플레이리스트 항목 컴포넌트입니다.
 */
@Composable
fun PlaylistListItem(
    playlist: PlaylistModel,
    colors: ColorTheme,
    typography: GwangSanTypography,
    onClick: () -> Unit
) {
    // Emotion에 따른 임시 색상 부여
    val iconColors = when (playlist.emotion.uppercase()) {
        "HAPPY" -> listOf(Color(0xFFFFB63C), Color(0xFFFF8B00))
        "SAD" -> listOf(Color(0xFF4EEBFB), Color(0xFF00B7D6))
        "ANGER" -> listOf(colors.error, Color(0xFFCC0000))
        else -> listOf(colors.purple, Color(0xFF6E42F7))
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.white),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlaylistIcon(iconColors, colors)
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.title,
                    style = typography.body3,
                    color = colors.black
                )
                Text(
                    text = "${playlist.trackCount}곡 (${playlist.emotion})",
                    style = typography.body5,
                    color = colors.gray700
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "다음 화면으로 이동",
                tint = colors.gray500,
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}

/**
 * 플레이리스트 아이콘 컴포넌트
 * R.drawable.music_icon 리소스를 사용하도록 수정
 */
@Composable
fun PlaylistIcon(colors: List<Color>, designColors: ColorTheme) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.linearGradient(colors = colors)),
        contentAlignment = Alignment.Center
    ) {
        // Icons.Filled.Home 대신 R.drawable.music_icon 사용
        Icon(
            painter = painterResource(id = R.drawable.music_icon),
            contentDescription = null,
            tint = designColors.white,
            modifier = Modifier.size(40.dp)
        )
    }
}


@Composable
fun MusicScreen(
    // SignInViewModel을 주입받음
    viewModel: SignInViewModel = viewModel(),
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    onNavigateToDetails: (PlaylistModel) -> Unit = {},
    memberId: Long = 1L,
) {
    val uiState by viewModel.musicUiState.collectAsState()

    LaunchedEffect(memberId) {
        if (uiState is MusicUiState.Idle) {
            viewModel.fetchPlaylists(memberId)
        }
    }

    GwangSanTheme { colors, typography ->
        Scaffold(
            bottomBar = {
                NoChuBottombarContent(
                    selectedIndex = selectedIndex,
                    onItemSelected = onItemSelected,
                    colors = colors,
                    typography = typography
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                MusicScreenContent(
                    colors = colors,
                    typography = typography,
                    uiState = uiState,
                    onNavigateToDetails = onNavigateToDetails
                )
            }
        }
    }
}


@Composable
private fun NoChuBottombarContent(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    colors: ColorTheme,
    typography: GwangSanTypography
) {
    // 바텀바 로직은 이전과 동일
    val items = listOf("홈", "사진", "분석", "음악", "기록")

    val icons = listOf(
        R.drawable.home,
        R.drawable.camera_icon,
        R.drawable.chartbar_icon,
        R.drawable.music_icon,
        R.drawable.history_icon,
    )

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
                        style = typography.label
                    )
                },
                selected = isSelected,
                onClick = { onItemSelected(index) },
            )
        }
    }
}

// ----------------------------------------------------------------------
// --- Preview 컴포넌트 ---
// ----------------------------------------------------------------------

@Preview(showBackground = true, name = "Music Screen Success State Preview")
@Composable
fun PreviewMusicScreenSuccessState() {
    val mockPlaylists = PlaylistListModel(
        playlists = listOf(
            PlaylistModel(1, "행복한 당신을 위한", "HAPPY", null, 12, "2025-01-01"),
            PlaylistModel(2, "평온한 마음", "CALM", null, 10, "2025-01-02"),
            PlaylistModel(3, "위로가 되는 음악", "SAD", null, 15, "2025-01-03")
        )
    )

    GwangSanTheme { colors, typography ->
        Scaffold(
            bottomBar = {
                NoChuBottombarContent(selectedIndex = 3, onItemSelected = {}, colors = colors, typography = typography)
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                MusicScreenContent(
                    colors = colors,
                    typography = typography,
                    uiState = MusicUiState.Success(mockPlaylists),
                    onNavigateToDetails = {}
                )
            }
        }
    }
}