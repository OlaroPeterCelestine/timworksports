package com.timworksports.crestedpass.ui.ar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timworksports.crestedpass.ui.components.Caption
import com.timworksports.crestedpass.ui.components.GoldLabel
import com.timworksports.crestedpass.ui.components.SerifTitle
import com.timworksports.crestedpass.ui.theme.Gold
import com.timworksports.crestedpass.ui.theme.Navy
import com.timworksports.crestedpass.ui.theme.SurfaceWhite

@Composable
fun ArLensScreen() {
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Navy.copy(alpha = 0.92f), Navy)
                )
            )
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Navy.copy(alpha = 0.35f))
        )
        Column(
            Modifier
                .align(Alignment.Center)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GoldLabel("AR lens")
            Spacer(Modifier.height(12.dp))
            SerifTitle("Coming soon", color = SurfaceWhite, size = 32)
            Spacer(Modifier.height(8.dp))
            Caption("Camera preview is a placeholder in v1. No AR session is started.", color = Gold)
            Spacer(Modifier.height(24.dp))
            Text("Scan · Crested Pass", color = SurfaceWhite.copy(alpha = 0.7f), fontSize = 13.sp)
        }
    }
}
