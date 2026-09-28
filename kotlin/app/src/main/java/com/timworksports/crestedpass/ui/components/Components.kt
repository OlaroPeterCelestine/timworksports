package com.timworksports.crestedpass.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.SportsMma
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.SportsTennis
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.timworksports.crestedpass.ui.media.imageRes
import com.timworksports.crestedpass.ui.theme.AlertRed
import com.timworksports.crestedpass.ui.theme.Navy
import com.timworksports.crestedpass.ui.theme.avatarColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import com.timworksports.crestedpass.ui.theme.DeepGreenBright
import com.timworksports.crestedpass.ui.theme.Gold
import com.timworksports.crestedpass.ui.theme.Sans

val CardRadius = 14.dp
val PillRadius = 40.dp

@Composable
fun BrandCard(
    modifier: Modifier = Modifier,
    background: Color = Color.Unspecified,
    radius: Dp = CardRadius,
    padding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(radius)
    val bg = if (background == Color.Unspecified) MaterialTheme.colorScheme.surface else background
    val line = MaterialTheme.colorScheme.outline
    Column(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .background(bg, shape)
            .border(1.dp, line, shape)
            .padding(padding),
        content = content
    )
}

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDark: Boolean = false
) {
    val background = when {
        selected -> Gold
        onDark -> Color.White.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surface
    }
    val content = when {
        selected -> Navy
        onDark -> Color.White.copy(alpha = 0.78f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val stroke = when {
        selected -> Gold
        onDark -> Color.Transparent
        else -> MaterialTheme.colorScheme.outline
    }
    Text(
        label,
        color = content,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        modifier = modifier
            .clip(CircleShape)
            .background(background)
            .border(1.dp, stroke, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    )
}

@Composable
fun GoldLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = DeepGreenBright,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = FontFamily.SansSerif
    )
}

@Composable
fun SerifTitle(text: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified, size: Int = 22) {
    Text(
        text = text,
        modifier = modifier,
        color = if (color == Color.Unspecified) MaterialTheme.colorScheme.onBackground else color,
        fontSize = size.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = Sans
    )
}

@Composable
fun Caption(text: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified) {
    Text(
        text = text,
        modifier = modifier,
        color = if (color == Color.Unspecified) MaterialTheme.colorScheme.onSurfaceVariant else color,
        fontSize = 13.sp,
        fontFamily = FontFamily.SansSerif
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gold: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (gold) Gold else MaterialTheme.colorScheme.primary,
            contentColor = if (gold) Navy else MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.outline,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.SansSerif)
    }
}

@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gold: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(PillRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (gold) Gold else MaterialTheme.colorScheme.primary,
            contentColor = if (gold) Navy else MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun TrailBar(collected: Int, total: Int, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "Trail progress",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                "$collected / $total stamps",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { if (total == 0) 0f else collected.toFloat() / total },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = Gold,
            trackColor = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun ScreenHeader(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    trailing: @Composable (RowScope.() -> Unit)? = null
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        if (onBack != null) {
            Icon(
                Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(end = 8.dp, top = 4.dp)
                    .size(22.dp)
                    .clickable(onClick = onBack)
            )
        }
        Column(Modifier.weight(1f)) {
            SerifTitle(title, size = 24)
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                Caption(subtitle)
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, content = trailing)
        }
    }
}

@Composable
fun AvatarMark(
    key: String,
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    ring: Boolean = false
) {
    val inner = if (ring) size - 8.dp else size
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        if (ring) {
            Box(
                Modifier
                    .size(size)
                    .border(2.dp, Gold, CircleShape)
            )
        }
        Box(
            Modifier
                .size(inner)
                .clip(CircleShape)
                .background(avatarColor(key)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                initials,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = (inner.value * 0.32f).sp
            )
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    value: String? = null,
    destructive: Boolean = false,
    showChevron: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val ink = if (destructive) AlertRed else MaterialTheme.colorScheme.onBackground
    Row(
        modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.background, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = ink, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(title, color = ink, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        if (value != null) {
            Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Spacer(Modifier.width(6.dp))
        }
        if (showChevron) {
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(36.dp)
                .background(colors.background, CircleShape)
                .border(1.dp, colors.outline, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = colors.onBackground, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(title, color = colors.onBackground, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = Gold,
                uncheckedThumbColor = colors.onSurfaceVariant,
                uncheckedTrackColor = colors.outline
            )
        )
    }
}

@Composable
fun LanguageToggle(language: String, onClick: () -> Unit) {
    Row(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            language,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.width(6.dp))
        Text("▾", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
    }
}

@Composable
fun StatusDot(color: Color) {
    Box(
        Modifier
            .size(8.dp)
            .background(color, CircleShape)
    )
}

@Composable
fun QuickAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.background, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SportCover(
    event: com.timworksports.crestedpass.data.model.FestivalEvent,
    modifier: Modifier = Modifier
) {
    val colors = sportCoverColors(event.sport)
    Box(modifier.clip(RoundedCornerShape(10.dp))) {
        Box(
            Modifier
                .matchParentSize()
                .background(Brush.linearGradient(colors))
        )
        Icon(
            imageVector = sportIcon(event.sport),
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.22f),
            modifier = Modifier
                .align(Alignment.Center)
                .size(42.dp)
        )
        Image(
            painter = painterResource(imageRes(event.imageName)),
            contentDescription = event.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

fun sportIcon(sport: String): ImageVector = when (sport.lowercase()) {
    "basketball" -> Icons.Outlined.SportsBasketball
    "tennis" -> Icons.Outlined.SportsTennis
    "boxing" -> Icons.Outlined.SportsMma
    "swimming" -> Icons.Outlined.Pool
    "athletics", "track" -> Icons.Outlined.DirectionsRun
    else -> Icons.Outlined.SportsSoccer
}

fun sportCoverColors(sport: String): List<Color> = when (sport.lowercase()) {
    "basketball" -> listOf(Color(0xFFB85114), Color(0xFF1E140F))
    "tennis" -> listOf(Color(0xFF2E7A47), Color(0xFF0F291F))
    "boxing" -> listOf(Color(0xFF8C1420), Color(0xFF1A0D0F))
    "swimming" -> listOf(Color(0xFF146192), Color(0xFF0A1F38))
    "athletics", "track" -> listOf(Color(0xFFB8851F), Color(0xFF291A0A))
    else -> listOf(Color(0xFF1A1A1A), Color.Black)
}
