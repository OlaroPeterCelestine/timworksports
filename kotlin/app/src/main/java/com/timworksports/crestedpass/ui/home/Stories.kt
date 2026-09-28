package com.timworksports.crestedpass.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timworksports.crestedpass.data.model.ReelClip
import com.timworksports.crestedpass.data.model.Shot
import com.timworksports.crestedpass.ui.media.imageRes
import com.timworksports.crestedpass.ui.theme.AlertRed
import com.timworksports.crestedpass.ui.theme.Gold
import java.text.NumberFormat
import java.util.Locale

data class StoryCircle(
    val id: String,
    val name: String,
    val initials: String,
    val imageName: String,
    val reelIndex: Int,
    val isYou: Boolean = false
)

fun storyCircles(userName: String, reels: List<ReelClip>): List<StoryCircle> {
    val yours = reels.indexOfFirst { it.author == userName }.let { if (it >= 0) it else 0 }
    val youImage = reels.getOrNull(yours)?.imageName ?: "reel_stands"
    val you = StoryCircle(
        id = "you",
        name = "Your story",
        initials = userName.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }.take(2).joinToString(""),
        imageName = youImage,
        reelIndex = yours,
        isYou = true
    )
    val others = LinkedHashMap<String, StoryCircle>()
    reels.forEachIndexed { index, reel ->
        if (reel.author != userName && !others.containsKey(reel.author)) {
            others[reel.author] = StoryCircle(
                id = reel.author,
                name = reel.author.substringBefore(" "),
                initials = reel.initials,
                imageName = reel.imageName,
                reelIndex = index
            )
        }
    }
    return listOf(you) + others.values
}

@Composable
fun StoryCirclesRow(
    userName: String,
    reels: List<ReelClip>,
    seen: Set<String>,
    onOpen: (Int, String) -> Unit
) {
    val stories = remember(userName, reels) { storyCircles(userName, reels) }
    LazyRow(
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(stories, key = { it.id }) { story ->
            StoryAvatar(
                story = story,
                seen = story.id in seen,
                onClick = { onOpen(story.reelIndex, story.id) }
            )
        }
    }
}

@Composable
private fun StoryAvatar(
    story: StoryCircle,
    seen: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(78.dp)) {
            Box(
                Modifier
                    .size(78.dp)
                    .border(
                        width = if (seen || story.isYou) 1.5.dp else 2.5.dp,
                        brush = if (seen || story.isYou) {
                            Brush.linearGradient(listOf(colors.outline, colors.outline))
                        } else {
                            Brush.linearGradient(listOf(Gold, Color(0xFFE8C547), AlertRed, Gold))
                        },
                        shape = CircleShape
                    )
            )
            Box(
                Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(colors.background)
            )
            Image(
                painter = painterResource(imageRes(story.imageName)),
                contentDescription = story.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            if (story.isYou) {
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Gold)
                        .border(2.dp, colors.background, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            story.name,
            color = colors.onBackground,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ShotPost(
    shot: Shot,
    onLike: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(imageRes(shot.imageName)),
                contentDescription = shot.author,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(shot.author, color = colors.onBackground, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(shot.handle, color = colors.onSurfaceVariant, fontSize = 12.sp)
            }
        }
        Image(
            painter = painterResource(imageRes(shot.imageName)),
            contentDescription = shot.caption,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (shot.liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Like",
                tint = if (shot.liked) AlertRed else colors.onBackground,
                modifier = Modifier
                    .size(26.dp)
                    .clickable(onClick = onLike)
            )
            Spacer(Modifier.width(16.dp))
            Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment", tint = colors.onBackground, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = "Share", tint = colors.onBackground, modifier = Modifier.size(24.dp))
            Spacer(Modifier.weight(1f))
            Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Save", tint = colors.onBackground, modifier = Modifier.size(24.dp))
        }
        Text(
            "${NumberFormat.getIntegerInstance(Locale.US).format(shot.likes)} likes",
            color = colors.onBackground,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 14.dp)
        )
        Row(Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
            Text(shot.author, color = colors.onBackground, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(Modifier.width(6.dp))
            Text(shot.caption, color = colors.onBackground, fontSize = 14.sp)
        }
        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = colors.outline)
    }
}
