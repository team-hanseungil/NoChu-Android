package com.school_of_company.signin.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.school_of_company.design_system.R
import com.school_of_company.design_system.component.nochubottombar.NoChuNavigationBar
import com.school_of_company.design_system.component.nochubottombar.NoChuNavigationBarItem
import com.school_of_company.design_system.theme.GwangSanTheme
import com.school_of_company.design_system.theme.GwangSanTypography
import com.school_of_company.design_system.theme.color.ColorTheme
import com.school_of_company.model.music.response.PlaylistDetailModel
import com.school_of_company.model.music.response.PlaylistListModel
import com.school_of_company.model.music.response.PlaylistModel
import com.school_of_company.model.music.response.TrackModel
import com.school_of_company.signin.viewmodel.SignInViewModel
import com.school_of_company.signin.viewmodel.uistate.MusicUiState
import com.school_of_company.signin.viewmodel.uistate.PlaylistDetailUiState

/**
 * 음악 화면 통합 Wrapper: 목록과 상세 화면 전환을 담당하며, 모든 Compose 컴포넌트를 포함합니다.
 */
@Composable
fun MusicScreen(
    viewModel: SignInViewModel = viewModel(),
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    memberId: Long = 1L,
    onNavigateToDetails: (PlaylistModel) -> Unit = {}
) {
    var selectedPlaylistId by remember { mutableLongStateOf(0L) }
    val listUiState by viewModel.musicUiState.collectAsState()
    val detailUiState by viewModel.playlistDetailUiState.collectAsState()

    LaunchedEffect(memberId) {
        if (listUiState is MusicUiState.Idle) {
            viewModel.fetchPlaylists(memberId)
        }
    }

    LaunchedEffect(selectedPlaylistId) {
        if (selectedPlaylistId != 0L) {
            viewModel.fetchPlaylistDetail(selectedPlaylistId)
        }
    }

    GwangSanTheme { colors, typography ->
        Scaffold(
            bottomBar = {
                if (selectedPlaylistId == 0L) {
                    NoChuBottombarContent(selectedIndex, onItemSelected, colors, typography)
                }
            },
            containerColor = colors.gray100
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                if (selectedPlaylistId == 0L) {
                    MusicScreenContent(
                        colors = colors,
                        typography = typography,
                        uiState = listUiState,
                        onNavigateToDetails = onNavigateToDetails
                    )
                } else {
                    PlaylistDetailContent(
                        colors = colors,
                        typography = typography,
                        playlistId = selectedPlaylistId,
                        uiState = detailUiState,
                        onBackClicked = { selectedPlaylistId = 0L }
                    )
                }
            }
        }
    }
}

// ====================================================================
// --- 1. Music Screen List Components (목록 화면) ---
// ====================================================================

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

        when (uiState) {
            MusicUiState.Loading, MusicUiState.Idle -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MusicUiState.Success -> {
                val playlists = uiState.playlistList.playlists

                if (playlists.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "추천 플레이리스트가 아직 없습니다.",
                            color = colors.gray700,
                            style = typography.body3
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(playlists.size) { index ->
                            PlaylistListItem(
                                playlist = playlists[index],
                                colors = colors,
                                typography = typography,
                                onClick = { onNavigateToDetails(playlists[index]) }
                            )
                        }
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

@Composable
fun PlaylistListItem(
    playlist: PlaylistModel,
    colors: ColorTheme,
    typography: GwangSanTypography,
    onClick: () -> Unit
) {
    val iconColors = getEmotionGradientColors(playlist.emotion, colors)

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
                painter = painterResource(id = R.drawable.music_icon),
                contentDescription = "다음",
                tint = colors.gray500,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun PlaylistIcon(colors: List<Color>, designColors: ColorTheme) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.linearGradient(colors = colors)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.music_icon),
            contentDescription = null,
            tint = designColors.white,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun getEmotionGradientColors(emotion: String, colors: ColorTheme): List<Color> {
    return when (emotion.uppercase()) {
        "HAPPY" -> listOf(Color(0xFFFFB63C), Color(0xFFFF8B00))
        "SURPRISE" -> listOf(Color(0xFFE47CFF), Color(0xFFC700FF))
        "ANGER" -> listOf(colors.error, Color(0xFFCC0000))
        "ANXIETY" -> listOf(Color(0xFFD3D3D3), Color(0xFF708090))
        "HURT" -> listOf(Color(0xFFFFC0CB), Color(0xFFFF69B4))
        "SAD" -> listOf(Color(0xFF4EEBFB), Color(0xFF00B7D6))
        else -> listOf(colors.purple, Color(0xFF6E42F7))
    }
}

@Composable
private fun NoChuBottombarContent(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    colors: ColorTheme,
    typography: GwangSanTypography
) {
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

// ====================================================================
// --- 2. Music Detail Components (상세 화면) ---
// ====================================================================

@Composable
fun PlaylistDetailContent(
    colors: ColorTheme,
    typography: GwangSanTypography,
    playlistId: Long,
    uiState: PlaylistDetailUiState,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current

    // 개별 트랙 재생 함수
    fun playTrack(track: TrackModel) {
        track.previewUrl?.let { url ->
            try {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                    data = android.net.Uri.parse(url)
                    setDataAndType(android.net.Uri.parse(url), "audio/*")
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    context,
                    "음악을 재생할 수 없습니다",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        } ?: android.widget.Toast.makeText(
            context,
            "미리듣기를 제공하지 않는 곡입니다",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    // 전체 재생 함수 (첫 번째 곡부터 재생)
    fun playAll(tracks: List<TrackModel>) {
        if (tracks.isNotEmpty()) {
            playTrack(tracks[0])
        } else {
            android.widget.Toast.makeText(
                context,
                "재생할 곡이 없습니다",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    when (uiState) {
        PlaylistDetailUiState.Loading, PlaylistDetailUiState.Idle -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.purple)
            }
        }
        is PlaylistDetailUiState.Success -> {
            val detail = uiState.playlistDetail

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    DetailHeaderSection(
                        detail = detail,
                        colors = colors,
                        typography = typography,
                        onBackClicked = onBackClicked
                    )
                }

                item {
                    PlayAllButton(
                        colors = colors,
                        typography = typography,
                        onPlayAll = { playAll(detail.tracks) }
                    )
                }

                itemsIndexed(detail.tracks) { index, track ->
                    TrackItem(
                        index = index + 1,
                        track = track,
                        colors = colors,
                        typography = typography,
                        onTrackClick = { playTrack(track) }
                    )
                }
            }
        }
        is PlaylistDetailUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "상세 정보 로딩 실패",
                        color = colors.error,
                        style = typography.body3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onBackClicked) {
                        Text("돌아가기", color = colors.purple)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailHeaderSection(
    detail: PlaylistDetailModel,
    colors: ColorTheme,
    typography: GwangSanTypography,
    onBackClicked: () -> Unit
) {
    val durationText = formatTotalDuration(detail.tracks)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFA726),
                        Color(0xFFFF5252)
                    )
                )
            )
    ) {
        IconButton(
            onClick = onBackClicked,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(detail.imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.music_icon),
                error = painterResource(R.drawable.music_icon),
                contentDescription = "플레이리스트 커버",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = detail.title,
                style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                fontSize = 24.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${detail.tracks.size}곡 · $durationText",
                style = typography.body4,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }
    }
}

private fun formatTotalDuration(tracks: List<TrackModel>): String {
    var totalSeconds = 0

    tracks.forEach { track ->
        val parts = track.duration.split(":")
        if (parts.size == 2) {
            val minutes = parts[0].toIntOrNull() ?: 0
            val seconds = parts[1].toIntOrNull() ?: 0
            totalSeconds += (minutes * 60) + seconds
        }
    }

    val totalMinutes = (totalSeconds + 30) / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        hours > 0 && minutes > 0 -> "${hours}시간 ${minutes}분"
        hours > 0 -> "${hours}시간"
        else -> "${minutes}분"
    }
}

@Composable
fun PlayAllButton(
    colors: ColorTheme,
    typography: GwangSanTypography,
    onPlayAll: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.gray100)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFF5E60CE))
                .clickable { onPlayAll() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "전체 재생",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun TrackItem(
    index: Int,
    track: TrackModel,
    colors: ColorTheme,
    typography: GwangSanTypography,
    onTrackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.gray100)
            .clickable { onTrackClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = index.toString(),
            style = typography.body3.copy(fontWeight = FontWeight.Medium),
            color = colors.gray700,
            modifier = Modifier.width(32.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(track.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(id = R.drawable.music_icon),
            error = painterResource(id = R.drawable.music_icon),
            contentDescription = "앨범 커버",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(6.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                style = typography.body2.copy(fontWeight = FontWeight.SemiBold),
                color = colors.black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = track.artists.joinToString(", "),
                style = typography.body5,
                color = colors.gray700,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = track.duration,
            style = typography.caption,
            color = colors.gray500,
            fontSize = 13.sp
        )
    }
}

// ====================================================================
// --- 3. Preview Components ---
// ====================================================================

@Preview(showBackground = true, name = "Playlist Detail Screen Preview")
@Composable
fun PreviewPlaylistDetailScreen() {
    val mockDetail = PlaylistDetailModel(
        id = 1L,
        title = "행복한 당신을 위한 플레이리스트",
        imageUrl = "https://example.com/happy_cover.jpg",
        tracks = listOf(
            TrackModel(listOf("Pharrell Williams"), "Happy", "https://example.com/album1.jpg", "https://example.com/preview1.mp3", "3:53"),
            TrackModel(listOf("Dua Lipa"), "Good Vibes", "https://example.com/album2.jpg", "https://example.com/preview2.mp3", "3:42"),
            TrackModel(listOf("Bruno Mars"), "Uptown Funk", "https://example.com/album3.jpg", "https://example.com/preview3.mp3", "4:30"),
            TrackModel(listOf("Ed Sheeran"), "Shape of You", "https://example.com/album4.jpg", "https://example.com/preview4.mp3", "3:53"),
            TrackModel(listOf("Mark Ronson"), "Feel Right", "https://example.com/album5.jpg", "https://example.com/preview5.mp3", "4:23"),
        )
    )

    GwangSanTheme { colors, typography ->
        Box(modifier = Modifier.fillMaxSize()) {
            PlaylistDetailContent(
                colors = colors,
                typography = typography,
                playlistId = 1L,
                uiState = PlaylistDetailUiState.Success(mockDetail),
                onBackClicked = {}
            )
        }
    }
}