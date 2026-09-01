package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.UserRole
import java.io.File

fun getAvatarIconVector(iconKey: String): ImageVector {
    return when (iconKey) {
        "teacher" -> Icons.Default.School
        "star" -> Icons.Default.Star
        "palette" -> Icons.Default.Palette
        "sports" -> Icons.Default.SportsSoccer
        "book" -> Icons.Default.MenuBook
        "camp" -> Icons.Default.Forest
        "rocket" -> Icons.Default.RocketLaunch
        "music" -> Icons.Default.MusicNote
        "camera" -> Icons.Default.PhotoCamera
        "smile" -> Icons.Default.SentimentSatisfiedAlt
        "heart" -> Icons.Default.Favorite
        "lightbulb" -> Icons.Default.Lightbulb
        else -> Icons.Default.Person
    }
}

val AVATAR_OPTIONS = listOf(
    "teacher" to "Guru / Topi",
    "star" to "Bintang Ceria",
    "palette" to "Pelukis / Seni",
    "sports" to "Olahragawan",
    "book" to "Kutu Buku",
    "camp" to "Pramuka / Alam",
    "rocket" to "Astronot Sains",
    "music" to "Pemusik",
    "camera" to "Fotografer",
    "smile" to "Senyum Ceria",
    "heart" to "Penuh Kasih",
    "lightbulb" to "Kreatif"
)

val COLOR_OPTIONS = listOf(
    0xFF1877F2L to "Biru Fourbook",
    0xFF059669L to "Hijau Alam",
    0xFFD97706L to "Kuning Emas",
    0xFFDC2626L to "Merah Semangat",
    0xFF9333EAL to "Ungu Kreatif",
    0xFFEC4899L to "Merah Muda",
    0xFF0891B2L to "Biru Laut",
    0xFFEA580CL to "Oranye Aktif"
)

@Composable
fun MemberAvatar(
    fullName: String = "",
    name: String = fullName,
    avatarColor: Long = 0xFF1877F2,
    avatarIcon: String = "teacher",
    customPhotoUri: String = "",
    showOnlineDot: Boolean = false,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    val displayName = if (fullName.isNotBlank()) fullName else name
    val bg = Color(avatarColor)
    val context = LocalContext.current

    val hasCustomPhoto = customPhotoUri.isNotBlank() && run {
        val f = File(customPhotoUri)
        f.exists() || customPhotoUri.startsWith("content://") || customPhotoUri.startsWith("http")
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (hasCustomPhoto) {
            val imageModel = if (File(customPhotoUri).exists()) {
                File(customPhotoUri)
            } else {
                customPhotoUri
            }

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageModel)
                    .crossfade(true)
                    .build(),
                contentDescription = displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(1.5.dp, Color(0xFF1877F2).copy(alpha = 0.3f), CircleShape)
            )
        } else if (avatarIcon == "teacher" || fullName.contains("Teten", ignoreCase = true) || name.contains("Teten", ignoreCase = true)) {
            // Official formal passport photo portrait of Pak Teten Kurniawan (Red background, suit & tie)
            TetenPassportAvatarGraphic(
                size = size,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(1.5.dp, Color(0xFFDC2626).copy(alpha = 0.6f), CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(bg)
                    .border(1.5.dp, Color.White.copy(alpha = 0.85f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val vector = getAvatarIconVector(avatarIcon)
                Icon(
                    imageVector = vector,
                    contentDescription = displayName,
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.56f)
                )
            }
        }

        if (showOnlineDot) {
            Box(
                modifier = Modifier
                    .size(size * 0.32f)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color(0xFF42B72A))
                    .border(1.5.dp, Color.White, CircleShape)
            )
        }
    }
}

@Composable
fun RoleBadge(
    roleString: String = "",
    role: String = roleString,
    modifier: Modifier = Modifier
) {
    val targetRole = if (role.isNotBlank()) role else roleString
    val (label, bg, fg) = when (targetRole) {
        UserRole.WALI_KELAS.name, "Wali Kelas (Guru)", "Wali Kelas (Admin)", "Wali Kelas", "Guru" -> Triple("Guru", Color(0xFFDCFCE7), Color(0xFF166534))
        UserRole.KETUA_KELAS.name, "Ketua Kelas" -> Triple("Ketua Kelas", Color(0xFFFEF3C7), Color(0xFF92400E))
        else -> Triple("Siswa", Color(0xFFE7F3FF), Color(0xFF0C56BE))
    }

    Surface(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
        color = bg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (label) {
                "Wali Kelas" -> Icons.Default.Verified
                "Ketua Kelas" -> Icons.Default.Stars
                else -> Icons.Default.School
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                color = fg,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Custom Canvas Avatar representing Pak Teten Kurniawan (Pas Foto Resmi):
 * Formal Indonesian passport photo with rich red background, dark navy/black suit, white shirt, and silk tie.
 */
@Composable
fun TetenPassportAvatarGraphic(
    size: Dp,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val w = this.size.width
        val h = this.size.height

        // 1. Background: Crimson Indonesian Passport Photo Red (Latar Merah Resmi)
        drawRect(
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                colors = listOf(Color(0xFFDC2626), Color(0xFFB91C1C), Color(0xFF991B1B))
            )
        )

        // Subtle gradient glow behind the head
        drawCircle(
            color = Color(0xFFEF4444).copy(alpha = 0.5f),
            radius = w * 0.45f,
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.4f)
        )

        // 2. Formal Dark Navy/Black Suit Coat (Jas Resmi)
        val suitPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.05f, h)
            lineTo(w * 0.05f, h * 0.72f)
            cubicTo(w * 0.15f, h * 0.56f, w * 0.30f, h * 0.52f, w * 0.38f, h * 0.52f)
            lineTo(w * 0.62f, h * 0.52f)
            cubicTo(w * 0.70f, h * 0.52f, w * 0.85f, h * 0.56f, w * 0.95f, h * 0.72f)
            lineTo(w * 0.95f, h)
            close()
        }
        drawPath(suitPath, color = Color(0xFF0F172A)) // Deep Midnight Navy / Black

        // Suit Left & Right Lapels
        val leftLapel = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.36f, h * 0.52f)
            lineTo(w * 0.28f, h * 0.70f)
            lineTo(w * 0.44f, h * 0.88f)
            lineTo(w * 0.44f, h * 0.66f)
            close()
        }
        drawPath(leftLapel, color = Color(0xFF1E293B))

        val rightLapel = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.64f, h * 0.52f)
            lineTo(w * 0.72f, h * 0.70f)
            lineTo(w * 0.56f, h * 0.88f)
            lineTo(w * 0.56f, h * 0.66f)
            close()
        }
        drawPath(rightLapel, color = Color(0xFF1E293B))

        // 3. Crisp White Collared Shirt (Kemeja Putih)
        val shirtPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.40f, h * 0.50f)
            lineTo(w * 0.60f, h * 0.50f)
            lineTo(w * 0.56f, h * 0.76f)
            lineTo(w * 0.44f, h * 0.76f)
            close()
        }
        drawPath(shirtPath, color = Color.White)

        // Shirt Collar Points
        val leftCollar = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.38f, h * 0.50f)
            lineTo(w * 0.46f, h * 0.58f)
            lineTo(w * 0.44f, h * 0.50f)
            close()
        }
        drawPath(leftCollar, color = Color(0xFFF1F5F9))

        val rightCollar = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.62f, h * 0.50f)
            lineTo(w * 0.54f, h * 0.58f)
            lineTo(w * 0.56f, h * 0.50f)
            close()
        }
        drawPath(rightCollar, color = Color(0xFFF1F5F9))

        // 4. Formal Silk Necktie (Dasi Elegan)
        // Knot
        val tieKnot = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.46f, h * 0.52f)
            lineTo(w * 0.54f, h * 0.52f)
            lineTo(w * 0.53f, h * 0.59f)
            lineTo(w * 0.47f, h * 0.59f)
            close()
        }
        drawPath(tieKnot, color = Color(0xFFCBD5E1))

        // Tie Body
        val tieBody = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.47f, h * 0.59f)
            lineTo(w * 0.53f, h * 0.59f)
            lineTo(w * 0.55f, h * 0.84f)
            lineTo(w * 0.50f, h * 0.90f)
            lineTo(w * 0.45f, h * 0.84f)
            close()
        }
        drawPath(tieBody, color = Color(0xFFE2E8F0)) // Elegant Silver / Lavender Light Silk

        // Diagonal Tie Texture Stripes
        drawLine(
            color = Color(0xFF94A3B8),
            start = androidx.compose.ui.geometry.Offset(w * 0.47f, h * 0.65f),
            end = androidx.compose.ui.geometry.Offset(w * 0.53f, h * 0.69f),
            strokeWidth = w * 0.02f
        )
        drawLine(
            color = Color(0xFF94A3B8),
            start = androidx.compose.ui.geometry.Offset(w * 0.46f, h * 0.74f),
            end = androidx.compose.ui.geometry.Offset(w * 0.54f, h * 0.78f),
            strokeWidth = w * 0.02f
        )

        // 5. Neck
        drawRect(
            color = Color(0xFFF4BE9B), // Indonesian warm skin tone
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.42f, h * 0.38f),
            size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.15f)
        )

        // 6. Head & Face
        // Ears
        drawOval(
            color = Color(0xFFF4BE9B),
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.28f, h * 0.25f),
            size = androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.12f)
        )
        drawOval(
            color = Color(0xFFF4BE9B),
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.64f, h * 0.25f),
            size = androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.12f)
        )

        // Face Oval
        drawOval(
            color = Color(0xFFFADBC8), // Smooth warm skin
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.31f, h * 0.15f),
            size = androidx.compose.ui.geometry.Size(w * 0.38f, h * 0.33f)
        )

        // 7. Neat Black Hair (Rambut Rapi Belah Samping)
        val hairPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.30f, h * 0.24f)
            cubicTo(w * 0.27f, h * 0.14f, w * 0.36f, h * 0.07f, w * 0.50f, h * 0.07f)
            cubicTo(w * 0.64f, h * 0.07f, w * 0.73f, h * 0.14f, w * 0.70f, h * 0.24f)
            cubicTo(w * 0.66f, h * 0.18f, w * 0.58f, h * 0.14f, w * 0.50f, h * 0.15f)
            cubicTo(w * 0.40f, h * 0.14f, w * 0.34f, h * 0.18f, w * 0.30f, h * 0.24f)
            close()
        }
        drawPath(hairPath, color = Color(0xFF18181B)) // Jet Black

        // 8. Eyebrows
        drawLine(
            color = Color(0xFF27272A),
            start = androidx.compose.ui.geometry.Offset(w * 0.37f, h * 0.23f),
            end = androidx.compose.ui.geometry.Offset(w * 0.45f, h * 0.22f),
            strokeWidth = w * 0.035f
        )
        drawLine(
            color = Color(0xFF27272A),
            start = androidx.compose.ui.geometry.Offset(w * 0.55f, h * 0.22f),
            end = androidx.compose.ui.geometry.Offset(w * 0.63f, h * 0.23f),
            strokeWidth = w * 0.035f
        )

        // 9. Eyes
        drawCircle(
            color = Color(0xFF18181B),
            radius = w * 0.024f,
            center = androidx.compose.ui.geometry.Offset(w * 0.41f, h * 0.27f)
        )
        drawCircle(
            color = Color(0xFF18181B),
            radius = w * 0.024f,
            center = androidx.compose.ui.geometry.Offset(w * 0.59f, h * 0.27f)
        )
        // Eye catchlights
        drawCircle(
            color = Color.White,
            radius = w * 0.008f,
            center = androidx.compose.ui.geometry.Offset(w * 0.405f, h * 0.265f)
        )
        drawCircle(
            color = Color.White,
            radius = w * 0.008f,
            center = androidx.compose.ui.geometry.Offset(w * 0.585f, h * 0.265f)
        )

        // 10. Nose
        drawLine(
            color = Color(0xFFE2A77A),
            start = androidx.compose.ui.geometry.Offset(w * 0.50f, h * 0.27f),
            end = androidx.compose.ui.geometry.Offset(w * 0.50f, h * 0.33f),
            strokeWidth = w * 0.02f
        )

        // 11. Pleasant, Confident Friendly Smile
        drawArc(
            color = Color(0xFF991B1B),
            startAngle = 15f,
            sweepAngle = 150f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.43f, h * 0.36f),
            size = androidx.compose.ui.geometry.Size(w * 0.14f, h * 0.06f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = w * 0.025f)
        )
    }
}

/** Backward compatibility alias */
@Composable
fun TetenSpeakerAvatarGraphic(
    size: Dp,
    modifier: Modifier = Modifier
) {
    TetenPassportAvatarGraphic(size = size, modifier = modifier)
}

