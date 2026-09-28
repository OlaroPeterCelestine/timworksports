package com.timworksports.crestedpass.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timworksports.crestedpass.ui.components.Caption

private data class Notice(val title: String, val detail: String, val whenLabel: String)

private val notices = listOf(
    Notice("Challenge", "Sarah Nakato accepted your athletics challenge.", "2m"),
    Notice("Club", "Mandela Track Club posted a morning session.", "1h"),
    Notice("Coach", "Amina Okello held Tuesday 09:00 for you.", "3h"),
    Notice("Recovery", "Watch recovery is 82. You are ready to train.", "Today")
)

@Composable
fun NotificationsScreen() {
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        notices.forEach { notice ->
            Column(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                Text(notice.title, color = colors.onBackground, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Caption(notice.detail)
                Spacer(Modifier.height(4.dp))
                Text(notice.whenLabel, color = colors.onSurfaceVariant, fontSize = 12.sp)
            }
            HorizontalDivider(color = colors.outline)
        }
    }
}
