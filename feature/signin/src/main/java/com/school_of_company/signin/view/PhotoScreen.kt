package com.school_of_company.nochumain

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.school_of_company.design_system.theme.GwangSanTheme
import com.school_of_company.design_system.R
import com.school_of_company.design_system.theme.color.GwangSanColor
import com.school_of_company.signin.viewmodel.SignInViewModel
import com.school_of_company.signin.viewmodel.uistate.PostFaceUiState
import com.school_of_company.presentation.history.HistoryScreen

// ------------------------------------------------------
// 1. Navigation Route 상수 정의 (변경 없음)
// ------------------------------------------------------
private const val HomeRoute = "home_route"
private const val PhotoRoute = "photo_route" // 현재 화면
private const val AnalysisRoute = "analysis_route"
private const val MusicRoute = "music_route"
private const val HistoryRoute = "history_route" // 기록 화면 라우트

@Composable
fun PhotoScreen( // ⬅️ 컴포저블 이름 변경
    memberId: Long,
    viewModel: SignInViewModel = hiltViewModel()
) {
    // ------------------------------------------------------
    // 2. NavController와 현재 경로 추적 (Mock Navigation을 위해)
    // ------------------------------------------------------
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = LocalContext.current

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val uiState by viewModel.postFaceUiState.collectAsState()

    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
            if (uri != null) viewModel.resetPostFaceState()
        }

    // 초기 시작 화면을 PhotoRoute로 설정
    LaunchedEffect(Unit) {
        if (currentRoute == null) {
            navController.navigate(PhotoRoute)
        }
    }


    Scaffold(
        bottomBar = {
            // ------------------------------------------------------
            // 3. NavigationContent에 NavController 전달 및 Route 기반 selectedState 계산
            // ------------------------------------------------------
            NavigationContent(
                currentRoute = currentRoute, // 현재 Route 전달
                onItemSelected = { route ->
                    // 클릭 시 해당 Route로 Navigation 수행
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            // 바텀바 Navigation 표준 설정
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        // ------------------------------------------------------
        // 4. NavHost를 사용하여 화면 전환 구현 (임시)
        // ------------------------------------------------------
        NavHost(
            navController = navController,
            startDestination = PhotoRoute, // 임시 시작 지점
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            // PhotoScreen (현재 화면)
            composable(PhotoRoute) {
                PhotoUploadContent(
                    modifier = Modifier.fillMaxSize(),
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
                    }
                )
            }
            // 나머지 탭의 임시 화면들
            composable(HomeRoute) { TempScreen(name = "홈") }
            composable(AnalysisRoute) { TempScreen(name = "분석") }
            composable(MusicRoute) { TempScreen(name = "음악") }

            // 기록 탭에 HistoryScreen 연결
            composable(HistoryRoute) {
                HistoryScreen()
            }
        }
    }
}

// ------------------------------------------------------
// 임시 화면 정의 (변경 없음)
// ------------------------------------------------------
@Composable
fun TempScreen(name: String) {
    GwangSanTheme { colors, typography ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.gray100),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$name 화면 (임시)",
                style = typography.titleLarge,
                color = colors.black
            )
        }
    }
}

// ======================================================
// Navigation Bar Components (NoChuNavigationBar)
// (변경 없음)
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
    currentRoute: String?,
    onItemSelected: (String) -> Unit
) {
    val items = listOf("홈", "사진", "분석", "음악", "기록")
    val icons = listOf(
        R.drawable.home,
        R.drawable.camera_icon,
        R.drawable.chartbar_icon,
        R.drawable.music_icon,
        R.drawable.history_icon,
    )
    // Route와 아이템을 매핑합니다.
    val routes = listOf(
        HomeRoute, PhotoRoute, AnalysisRoute, MusicRoute, HistoryRoute
    )

    GwangSanTheme { colors, typography ->
        NoChuNavigationBar {
            items.forEachIndexed { index, item ->
                val route = routes[index]
                // 현재 Route와 일치하는지 확인하여 선택 상태 결정
                val isSelected = currentRoute == route
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
                    // 클릭 시 해당 Route를 상위로 전달합니다.
                    onClick = { onItemSelected(route) },
                )
            }
        }
    }
}


// ======================================================
// Content (UI + 콜백)
// (변경 없음)
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
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
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
                            text =  "감정 분석하기",
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
// Preview
// (변경 없음)
// ======================================================
@Preview(showBackground = true)
@Composable
fun FullPhotoUploadScreenPreview() {
    GwangSanTheme { _, _ ->
        PhotoUploadContent(
            selectedImageUri = null,
            uiState = PostFaceUiState.Idle,
            onPickImage = {},
            onPostClick = {}
        )
    }
}