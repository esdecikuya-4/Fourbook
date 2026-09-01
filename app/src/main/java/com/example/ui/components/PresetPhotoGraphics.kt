package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.io.File

@Composable
fun PresetPhotoCanvas(
    presetKey: String,
    modifier: Modifier = Modifier
) {
    PresetIllustration(presetKey = presetKey, modifier = modifier)
}

@Composable
fun LocalImageDisplay(
    filePath: String,
    modifier: Modifier = Modifier,
    contentDescription: String = "Foto Kegiatan"
) {
    PhotoDisplay(
        imageUri = filePath,
        presetKey = "preset_event",
        contentDescription = contentDescription,
        modifier = modifier
    )
}

@Composable
fun PhotoDisplay(
    imageUri: String,
    presetKey: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE2E8F0)),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri.isNotBlank()) {
            val file = File(imageUri)
            val model = if (file.exists()) file else imageUri
            AsyncImage(
                model = model,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        } else {
            PresetIllustration(presetKey = presetKey, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun PresetIllustration(
    presetKey: String,
    modifier: Modifier = Modifier
) {
    when (presetKey) {
        "preset_science" -> ScienceIllustration(modifier)
        "preset_art" -> ArtIllustration(modifier)
        "preset_sports" -> SportsIllustration(modifier)
        "preset_scout" -> ScoutIllustration(modifier)
        "preset_trophy" -> TrophyIllustration(modifier)
        "preset_event" -> EventIllustration(modifier)
        else -> GeneralClassIllustration(modifier)
    }
}

@Composable
fun ScienceIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1E3A8A), Color(0xFF2563EB), Color(0xFF0284C7))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            // Draw beaker / test tube circles and lab particles
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = w * 0.35f,
                center = Offset(w * 0.8f, h * 0.2f)
            )
            drawCircle(
                color = Color(0xFF67E8F9).copy(alpha = 0.25f),
                radius = w * 0.2f,
                center = Offset(w * 0.2f, h * 0.8f)
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Science,
                contentDescription = null,
                tint = Color(0xFFFDE047),
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "🔬 Praktik Sains & IPA",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Eksperimen Siswa SDN 4",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun ArtIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF581C87), Color(0xFF9333EA), Color(0xFFEC4899))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            // Colorful splash rings
            drawCircle(
                color = Color(0xFFF472B6).copy(alpha = 0.3f),
                radius = w * 0.3f,
                center = Offset(w * 0.25f, h * 0.3f)
            )
            drawCircle(
                color = Color(0xFFFBBF24).copy(alpha = 0.25f),
                radius = w * 0.25f,
                center = Offset(w * 0.75f, h * 0.7f)
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                tint = Color(0xFFFDE047),
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "🎨 Seni Rupa & Prakarya",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Kreativitas Siswa",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun SportsIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF9A3412), Color(0xFFEA580C), Color(0xFFF97316))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = w * 0.35f,
                center = Offset(w * 0.5f, h * 0.5f)
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SportsSoccer,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "⚽ Olahraga & Senam",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Sehat & Gembira",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun ScoutIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF14532D), Color(0xFF16A34A), Color(0xFF65A30D))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            // Hill / mountain silhouettes
            val path = Path().apply {
                moveTo(0f, h)
                lineTo(w * 0.4f, h * 0.6f)
                lineTo(w * 0.8f, h * 0.75f)
                lineTo(w, h * 0.5f)
                lineTo(w, h)
                close()
            }
            drawPath(path = path, color = Color(0xFF0F3D20).copy(alpha = 0.4f))
        }
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Forest,
                contentDescription = null,
                tint = Color(0xFFFEF08A),
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "🏕️ Pramuka Siaga",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "SDN 4 Putrajawa",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun TrophyIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF78350F), Color(0xFFD97706), Color(0xFFFBBF24))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(
                color = Color.White.copy(alpha = 0.2f),
                radius = w * 0.3f,
                center = Offset(w * 0.5f, h * 0.45f)
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "🏆 Prestasi & Juara",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Bangga SDN 4 Putrajawa",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun EventIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF831843), Color(0xFFDB2777), Color(0xFFF472B6))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(
                color = Color(0xFFFDE047).copy(alpha = 0.25f),
                radius = w * 0.25f,
                center = Offset(w * 0.2f, h * 0.25f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.2f),
                radius = w * 0.35f,
                center = Offset(w * 0.8f, h * 0.8f)
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Celebration,
                contentDescription = null,
                tint = Color(0xFFFEF08A),
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "🎉 Acara & Kebersamaan",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Momen Berkesan",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun GeneralClassIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6), Color(0xFF60A5FA))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "📸 Galeri SDN 4",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "SDN 4 Putrajawa",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp
            )
        }
    }
}

val PRESET_TEMPLATES = listOf(
    PresetTemplateInfo("preset_science", "Praktik IPA & Sains", "Eksperimen di kelas", Icons.Default.Science, Color(0xFF2563EB)),
    PresetTemplateInfo("preset_art", "Karya Seni & Prakarya", "Gambar, origami & kolase", Icons.Default.Palette, Color(0xFF9333EA)),
    PresetTemplateInfo("preset_sports", "Olahraga & Senam", "Kebugaran dan sepak bola", Icons.Default.SportsSoccer, Color(0xFFEA580C)),
    PresetTemplateInfo("preset_scout", "Pramuka Siaga", "Kegiatan pramuka & kemah", Icons.Default.Forest, Color(0xFF16A34A)),
    PresetTemplateInfo("preset_trophy", "Prestasi & Lomba", "Piala, piagam & juara", Icons.Default.EmojiEvents, Color(0xFFD97706)),
    PresetTemplateInfo("preset_event", "Acara & Pentas Seni", "Syukuran & ulang tahun", Icons.Default.Celebration, Color(0xFFDB2777))
)

data class PresetTemplateInfo(
    val key: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)
