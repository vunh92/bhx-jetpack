package com.vunh.jetpack.bhx.presentation.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vunh.jetpack.bhx.presentation.common.HeaderSection
import androidx.compose.foundation.BorderStroke
import androidx.hilt.navigation.compose.hiltViewModel
import com.vunh.jetpack.bhx.R

@Composable
fun ScannerScreen(
    onBack: () -> Unit,
    onMenuClick: () -> Unit,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HeaderSection(isHome = false, onMenuClick = onMenuClick)

        // Title Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF1A1A1A)
                )
            }
            Text(
                text = uiState.title,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E3A59)
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = uiState.hint,
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // OTP-like squares
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(uiState.codeLength) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(50.dp, 60.dp)
                            .border(1.dp, Color(0xFFDDE1E6), RoundedCornerShape(8.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Scanner Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.scanner_camera_hint),
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    // QR Box Overlay
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                    ) {
                        QRFrame()
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Choose from gallery button
            Surface(
                modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF2E3A59)),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = Color(0xFF2E3A59),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = uiState.galleryActionLabel,
                        color = Color(0xFF2E3A59),
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun QRFrame() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val thickness = 15f
        val length = 80f
        val radius = 20f

        // Top-left corner
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(0f, 0f),
            size = Size(length, thickness),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(0f, 0f),
            size = Size(thickness, length),
            cornerRadius = CornerRadius(radius, radius)
        )

        // Top-right corner
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(width - length, 0f),
            size = Size(length, thickness),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(width - thickness, 0f),
            size = Size(thickness, length),
            cornerRadius = CornerRadius(radius, radius)
        )

        // Bottom-left corner
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(0f, height - thickness),
            size = Size(length, thickness),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(0f, height - length),
            size = Size(thickness, length),
            cornerRadius = CornerRadius(radius, radius)
        )

        // Bottom-right corner
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(width - length, height - thickness),
            size = Size(length, thickness),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(width - thickness, height - length),
            size = Size(thickness, length),
            cornerRadius = CornerRadius(radius, radius)
        )
    }
}
