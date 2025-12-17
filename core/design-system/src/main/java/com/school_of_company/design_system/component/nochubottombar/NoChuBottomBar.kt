package com.school_of_company.design_system.component.nochubottombar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview as GwangsanPreviews
import androidx.compose.ui.unit.dp
import com.school_of_company.design_system.R
import com.school_of_company.design_system.theme.GwangSanTheme
import com.school_of_company.design_system.theme.color.GwangSanColor

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
){
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
        ) ,
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
        R.drawable.home ,
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
                            tint = GwangSanColor.gray500
                        )
                    },
                    selectedIcon = {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = item,
                            tint = GwangSanColor.purple
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
}


@GwangsanPreviews
@Composable
fun NoChuNavigationHomePreview() {
    NavigationContent(
        selectedIndex = 0,
        onItemSelected = {}
    )
}

@GwangsanPreviews
@Composable
fun NoChuNavigationCameraPreview() {
    NavigationContent(
        selectedIndex = 1,
        onItemSelected = {}
    )
}

@GwangsanPreviews
@Composable
fun NoChuNavigationInteractivePreview() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    NavigationContent(
        selectedIndex = 2,
        onItemSelected = {}
    )
}