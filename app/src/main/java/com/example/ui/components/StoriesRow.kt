package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.viewmodel.StoryItem

@Composable
fun StoriesRow(
    currentUser: UserEntity?,
    stories: List<StoryItem>,
    onCreateStoryClick: () -> Unit,
    onStoryClick: (StoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cerita Warga SDN 4",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Lihat Semua",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // "+ Buat Cerita" Item
                item {
                    CreateStoryCard(
                        currentUser = currentUser,
                        onClick = onCreateStoryClick
                    )
                }

                // Story Items
                items(stories, key = { it.id }) { story ->
                    StoryCardItem(
                        story = story,
                        onClick = { onStoryClick(story) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateStoryCard(
    currentUser: UserEntity?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(105.dp)
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Upper area with Avatar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                MemberAvatar(
                    fullName = currentUser?.fullName ?: "Siswa",
                    avatarColor = currentUser?.avatarColor ?: 0xFF4F46E5,
                    avatarIcon = currentUser?.avatarIcon ?: "star",
                    size = 48.dp
                )
            }

            // Bottom area with blue plus icon & label
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .offset(y = (-14).dp)
                            .size(28.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Buat Cerita",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "Buat Cerita",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.offset(y = (-6).dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StoryCardItem(
    story: StoryItem,
    onClick: () -> Unit
) {
    val gradientBrush = getGradientForKey(story.bgGradientKey)

    Card(
        modifier = Modifier
            .width(105.dp)
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(8.dp)
        ) {
            // Top Story Author Avatar with Ring
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(34.dp)
                    .border(2.dp, Color(0xFF38BDF8), CircleShape)
                    .padding(2.dp)
            ) {
                MemberAvatar(
                    fullName = story.userName,
                    avatarColor = story.avatarColor,
                    avatarIcon = story.avatarIcon,
                    size = 28.dp
                )
            }

            // Center story preview text
            Text(
                text = story.storyText,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 28.dp, bottom = 20.dp)
            )

            // Bottom Name
            Text(
                text = story.userName,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

fun getGradientForKey(key: String): Brush {
    return when (key) {
        "gradient_sunset" -> Brush.verticalGradient(listOf(Color(0xFFF97316), Color(0xFFEF4444), Color(0xFFDB2777)))
        "gradient_ocean" -> Brush.verticalGradient(listOf(Color(0xFF0284C7), Color(0xFF2563EB), Color(0xFF4F46E5)))
        "gradient_emerald" -> Brush.verticalGradient(listOf(Color(0xFF059669), Color(0xFF0D9488), Color(0xFF047857)))
        "gradient_purple" -> Brush.verticalGradient(listOf(Color(0xFF7C3AED), Color(0xFF9333EA), Color(0xFFC026D3)))
        "gradient_fire" -> Brush.verticalGradient(listOf(Color(0xFFEA580C), Color(0xFFDC2626), Color(0xFF991B1B)))
        "gradient_blue_purple" -> Brush.verticalGradient(listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6), Color(0xFFEC4899)))
        else -> Brush.verticalGradient(listOf(Color(0xFF4F46E5), Color(0xFF7C3AED), Color(0xFF9333EA)))
    }
}
