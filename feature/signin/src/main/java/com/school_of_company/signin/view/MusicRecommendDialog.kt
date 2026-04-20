package com.school_of_company.signin.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.school_of_company.design_system.theme.GwangSanTheme

@Composable
fun MusicRecommendDialog(
    onDismiss: () -> Unit,
    onConfirm: (String?) -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    GwangSanTheme { colors, typography ->
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colors.white,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "음악 추천",
                            style = typography.titleSmall,
                            color = colors.black
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "닫기",
                                tint = colors.gray600
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "원하는 음악 스타일이나 기분을 적어주세요",
                        style = typography.body2,
                        color = colors.gray600
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.gray100)
                            .padding(14.dp),
                        textStyle = typography.body2.copy(color = colors.black),
                        decorationBox = { innerTextField ->
                            if (inputText.isEmpty()) {
                                Text(
                                    text = "예: 신나는 음악, 잔잔한 발라드...",
                                    style = typography.body2,
                                    color = colors.gray400
                                )
                            }
                            innerTextField()
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { onConfirm(inputText.trim().ifBlank { null }) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.subPOPule,
                            contentColor = colors.white
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            text = "추천받기",
                            style = typography.body1.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}