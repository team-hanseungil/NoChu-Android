package com.school_of_company.signin.view

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.school_of_company.design_system.theme.GwangSanTheme
import com.school_of_company.design_system.theme.GwangSanTypography
import com.school_of_company.design_system.theme.color.ColorTheme
import com.school_of_company.model.auth.request.PlaylistResponseModel
import com.school_of_company.model.auth.request.TrackModel
import com.school_of_company.signin.viewmodel.SignInViewModel
import com.school_of_company.signin.viewmodel.uistate.MusicRR

@Composable
fun MusicScreen(
    viewModel: SignInViewModel = viewModel(),
    memberId: Long,
    onBackClicked: () -> Unit = {}
) {
    val uiState by viewModel.musicRRState.collectAsState()

    LaunchedEffect(memberId) {
        viewModel.musicRR(memberId)
    }

    GwangSanTheme { colors, typography ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.gray100)
        ) {
            lPlaylistDetailContent(
                colors = colors,
                typography = typography,
                playlistId = 0L,
                uiState = uiState,
                onBackClicked = onBackClicked
            )
        }
    }
}

@Composable
fun lPlaylistDetailContent(
    colors: ColorTheme,
    typography: GwangSanTypography,
    playlistId: Long,
    uiState: MusicRR,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current

    fun playTrack(track: TrackModel) {
        track.previewUrl?.let { url ->
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                    setDataAndType(Uri.parse(url), "audio/*")
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "음악을 재생할 수 없습니다",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } ?: Toast.makeText(
            context,
            "미리듣기를 제공하지 않는 곡입니다",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun playAll(tracks: List<TrackModel>) {
        if (tracks.isNotEmpty()) {
            playTrack(tracks[0])
        } else {
            Toast.makeText(
                context,
                "재생할 곡이 없습니다",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    when (uiState) {
        MusicRR.Loading,
        MusicRR.Idle -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.purple)
            }
        }

        is MusicRR.Success -> {
            val detail = uiState.data

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    lDetailHeaderSection(
                        detail = detail,
                        colors = colors,
                        typography = typography,
                        onBackClicked = onBackClicked
                    )
                }

                item {
                    lPlayAllButton(
                        colors = colors,
                        typography = typography,
                        onPlayAll = { playAll(detail.tracks) }
                    )
                }

                itemsIndexed(detail.tracks) { index, track ->
                    lTrackItem(
                        index = index + 1,
                        track = track,
                        colors = colors,
                        typography = typography,
                        onTrackClick = { playTrack(track) }
                    )
                }
            }
        }

        is MusicRR.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "상세 정보 로딩 실패",
                        color = colors.error,
                        style = typography.body3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.exception.message ?: "",
                        color = colors.gray700,
                        style = typography.body5
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
fun lDetailHeaderSection(
    detail: PlaylistResponseModel,
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
            totalSeconds += minutes * 60 + seconds
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
fun lPlayAllButton(
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
fun lTrackItem(
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
