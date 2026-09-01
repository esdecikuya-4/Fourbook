package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

data class VideoPreset(
    val key: String,
    val title: String,
    val description: String,
    val durationSec: Int,
    val categoryName: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val iconName: String
)

object VideoPresetsData {
    val presets = listOf(
        VideoPreset(
            key = "video_science",
            title = "Praktik Rangkaian Listrik IPA",
            description = "Percobaan saklar, lampu seri & paralel siswa kelompok 1",
            durationSec = 45,
            categoryName = "Belajar",
            primaryColor = Color(0xFF0F172A),
            secondaryColor = Color(0xFFF59E0B),
            iconName = "science"
        ),
        VideoPreset(
            key = "video_futsal",
            title = "Cuplikan Gol Latihan Futsal",
            description = "Aksi tendangan gol dan dribble seru sparing futsal siswa SDN 4",
            durationSec = 30,
            categoryName = "Olahraga",
            primaryColor = Color(0xFF064E3B),
            secondaryColor = Color(0xFF10B981),
            iconName = "sports"
        ),
        VideoPreset(
            key = "video_pramuka",
            title = "Latihan Morse & Semaphore",
            description = "Kekompakan Regu Elang mengirim pesan sandi morse",
            durationSec = 60,
            categoryName = "Pramuka",
            primaryColor = Color(0xFF78350F),
            secondaryColor = Color(0xFFFBBF24),
            iconName = "camp"
        ),
        VideoPreset(
            key = "video_art",
            title = "Tutorial Melukis Kolase",
            description = "Langkah menempel origami motif burung cendrawasih",
            durationSec = 40,
            categoryName = "Karya Seni",
            primaryColor = Color(0xFF581C87),
            secondaryColor = Color(0xFFEC4899),
            iconName = "palette"
        ),
        VideoPreset(
            key = "video_anthem",
            title = "Menyanyikan Mars SDN 4",
            description = "Paduan suara siswa SDN 4 Putrajawa menyanyikan lagu kebanggaan",
            durationSec = 50,
            categoryName = "Kegiatan",
            primaryColor = Color(0xFF1E3A8A),
            secondaryColor = Color(0xFF38BDF8),
            iconName = "music"
        )
    )

    fun getPreset(key: String): VideoPreset? = presets.find { it.key == key }
}

@Composable
fun PresetVideoCanvasAnimation(
    presetKey: String,
    isPlaying: Boolean,
    progressFraction: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "video_anim")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        when (presetKey) {
            "video_science" -> drawScienceScene(w, h, isPlaying, progressFraction, pulse)
            "video_futsal" -> drawFutsalScene(w, h, isPlaying, progressFraction, wavePhase)
            "video_pramuka" -> drawPramukaScene(w, h, isPlaying, progressFraction, pulse)
            "video_art" -> drawArtScene(w, h, isPlaying, progressFraction, wavePhase)
            else -> drawChoirScene(w, h, isPlaying, progressFraction, wavePhase, pulse)
        }
    }
}

private fun DrawScope.drawScienceScene(w: Float, h: Float, isPlaying: Boolean, progress: Float, pulse: Float) {
    // Dark Lab Background
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF090D16))
        ),
        size = Size(w, h)
    )

    // Circuit board grid
    val gridColor = Color(0x2238BDF8)
    for (x in 0..w.toInt() step 60) {
        drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), h), 1.5f)
    }
    for (y in 0..h.toInt() step 60) {
        drawLine(gridColor, Offset(0f, y.toFloat()), Offset(w, y.toFloat()), 1.5f)
    }

    val cx = w / 2f
    val cy = h / 2f - 10f

    // Battery on left
    val batteryX = cx - 140f
    val batteryY = cy + 40f
    drawRoundRect(
        color = Color(0xFF3B82F6),
        topLeft = Offset(batteryX, batteryY),
        size = Size(70f, 40f),
        cornerRadius = CornerRadius(8f, 8f)
    )
    drawRect(
        color = Color(0xFF94A3B8),
        topLeft = Offset(batteryX + 70f, batteryY + 12f),
        size = Size(10f, 16f)
    )

    // Light bulb in center
    val bulbY = cy - 30f
    val currentPulse = if (isPlaying) pulse else 1f
    val glowColor = Color(0xFFFBBF24).copy(alpha = if (isPlaying) 0.6f else 0.2f)

    // Glow aura
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFDE047).copy(alpha = 0.8f), glowColor, Color.Transparent),
            center = Offset(cx, bulbY),
            radius = 90f * currentPulse
        ),
        radius = 90f * currentPulse,
        center = Offset(cx, bulbY)
    )

    // Bulb glass
    drawCircle(
        color = if (isPlaying) Color(0xFFFEF08A) else Color(0xFF94A3B8),
        radius = 36f,
        center = Offset(cx, bulbY)
    )
    // Filament
    drawArc(
        color = Color(0xFFD97706),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx - 16f, bulbY - 16f),
        size = Size(32f, 32f),
        style = Stroke(width = 4f)
    )
    // Bulb Base
    drawRoundRect(
        color = Color(0xFF64748B),
        topLeft = Offset(cx - 14f, bulbY + 32f),
        size = Size(28f, 20f),
        cornerRadius = CornerRadius(4f, 4f)
    )

    // Switch on right
    val switchX = cx + 90f
    val switchY = cy + 40f
    drawRoundRect(
        color = Color(0xFFEF4444),
        topLeft = Offset(switchX, switchY),
        size = Size(60f, 30f),
        cornerRadius = CornerRadius(6f, 6f)
    )
    // Switch lever
    val leverAngle = if (isPlaying) 0f else -35f
    val leverColor = if (isPlaying) Color(0xFF22C55E) else Color(0xFFF59E0B)
    drawLine(
        color = leverColor,
        start = Offset(switchX + 15f, switchY + 15f),
        end = Offset(switchX + 45f, switchY + 15f + (if (!isPlaying) -15f else 0f)),
        strokeWidth = 6f,
        cap = StrokeCap.Round
    )

    // Wire lines connecting circuit
    val wireColor = if (isPlaying) Color(0xFF38BDF8) else Color(0xFF475569)
    val wireStroke = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), progress * 50f))

    val wirePath = Path().apply {
        moveTo(batteryX + 75f, batteryY + 20f)
        lineTo(switchX, switchY + 15f)
        moveTo(switchX + 60f, switchY + 15f)
        lineTo(cx + 100f, cy + 90f)
        lineTo(cx, bulbY + 52f)
        moveTo(batteryX, batteryY + 20f)
        lineTo(cx - 100f, cy + 90f)
        lineTo(cx, bulbY + 52f)
    }
    drawPath(wirePath, color = wireColor, style = wireStroke)
}

private fun DrawScope.drawFutsalScene(w: Float, h: Float, isPlaying: Boolean, progress: Float, phase: Float) {
    // Green Pitch Background
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF064E3B), Color(0xFF047857), Color(0xFF065F46))
        ),
        size = Size(w, h)
    )

    // Pitch Lines
    val lineCol = Color(0x55FFFFFF)
    drawRoundRect(
        color = lineCol,
        topLeft = Offset(20f, 20f),
        size = Size(w - 40f, h - 40f),
        cornerRadius = CornerRadius(16f, 16f),
        style = Stroke(width = 3f)
    )
    drawLine(lineCol, Offset(w / 2f, 20f), Offset(w / 2f, h - 20f), strokeWidth = 3f)
    drawCircle(color = lineCol, radius = 45f, center = Offset(w / 2f, h / 2f), style = Stroke(width = 3f))

    // Goal Post on Right
    drawRect(
        color = Color.White,
        topLeft = Offset(w - 50f, h / 2f - 40f),
        size = Size(30f, 80f),
        style = Stroke(width = 4f)
    )

    // Ball movement calculation
    val ballX = if (isPlaying) {
        val cycle = (progress * 3f) % 1f
        w * 0.2f + (w * 0.65f) * cycle
    } else {
        w * 0.45f
    }
    val ballY = h / 2f + sin((ballX / 50f) + Math.toRadians(phase.toDouble()).toFloat()) * 25f

    // Ball Trail
    if (isPlaying) {
        drawLine(
            brush = Brush.horizontalGradient(
                listOf(Color.Transparent, Color(0x88F59E0B), Color(0xFFFBBF24)),
                startX = ballX - 80f,
                endX = ballX
            ),
            start = Offset(ballX - 80f, ballY),
            end = Offset(ballX, ballY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
    }

    // Ball
    drawCircle(color = Color.White, radius = 18f, center = Offset(ballX, ballY))
    drawCircle(color = Color.Black, radius = 6f, center = Offset(ballX, ballY))
    drawCircle(color = Color(0xFF1E293B), radius = 18f, center = Offset(ballX, ballY), style = Stroke(width = 2f))
}

private fun DrawScope.drawPramukaScene(w: Float, h: Float, isPlaying: Boolean, progress: Float, pulse: Float) {
    // Campfire & Night Forest
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF1E1B4B), Color(0xFF311042), Color(0xFF18181B))
        ),
        size = Size(w, h)
    )

    // Stars
    val starColor = Color(0xAAFEF08A)
    drawCircle(starColor, 3f, Offset(w * 0.15f, h * 0.2f))
    drawCircle(starColor, 2f, Offset(w * 0.35f, h * 0.15f))
    drawCircle(starColor, 4f, Offset(w * 0.75f, h * 0.25f))
    drawCircle(starColor, 2.5f, Offset(w * 0.85f, h * 0.12f))

    // Tent
    val tentPath = Path().apply {
        moveTo(w * 0.2f, h * 0.75f)
        lineTo(w * 0.35f, h * 0.35f)
        lineTo(w * 0.5f, h * 0.75f)
        close()
    }
    drawPath(tentPath, color = Color(0xFFD97706))
    // Tent opening
    val tentInner = Path().apply {
        moveTo(w * 0.3f, h * 0.75f)
        lineTo(w * 0.35f, h * 0.45f)
        lineTo(w * 0.4f, h * 0.75f)
        close()
    }
    drawPath(tentInner, color = Color(0xFF78350F))

    // Campfire in Center-Right
    val fireX = w * 0.7f
    val fireY = h * 0.65f

    // Fire Glow
    val firePulse = if (isPlaying) pulse else 1f
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color(0xFFF97316).copy(alpha = 0.7f), Color(0xFFEF4444).copy(alpha = 0.3f), Color.Transparent),
            center = Offset(fireX, fireY),
            radius = 75f * firePulse
        ),
        radius = 75f * firePulse,
        center = Offset(fireX, fireY)
    )

    // Wood Logs
    drawLine(Color(0xFF522E10), Offset(fireX - 35f, fireY + 25f), Offset(fireX + 35f, fireY + 15f), strokeWidth = 10f, cap = StrokeCap.Round)
    drawLine(Color(0xFF3F2107), Offset(fireX - 30f, fireY + 15f), Offset(fireX + 30f, fireY + 25f), strokeWidth = 10f, cap = StrokeCap.Round)

    // Flame
    val flameHeight = 45f * (if (isPlaying) firePulse else 1f)
    val flamePath = Path().apply {
        moveTo(fireX - 22f, fireY + 15f)
        quadraticTo(fireX - 25f, fireY - 10f, fireX, fireY - flameHeight)
        quadraticTo(fireX + 25f, fireY - 10f, fireX + 22f, fireY + 15f)
        close()
    }
    drawPath(flamePath, brush = Brush.verticalGradient(listOf(Color(0xFFFEF08A), Color(0xFFF59E0B), Color(0xFFDC2626))))
}

private fun DrawScope.drawArtScene(w: Float, h: Float, isPlaying: Boolean, progress: Float, phase: Float) {
    // Studio Palette Background
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF4C1D95), Color(0xFF701A75), Color(0xFF831843))
        ),
        size = Size(w, h)
    )

    // Canvas Frame
    val cx = w / 2f
    val cy = h / 2f
    drawRoundRect(
        color = Color(0xFFFFFBEB),
        topLeft = Offset(cx - 120f, cy - 75f),
        size = Size(240f, 150f),
        cornerRadius = CornerRadius(12f, 12f)
    )

    // Colorful splash on canvas
    val colors = listOf(Color(0xFFEF4444), Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF3B82F6), Color(0xFF8B5CF6))
    colors.forEachIndexed { idx, color ->
        val offsetAngle = Math.toRadians((idx * 72f + (if (isPlaying) phase else 0f)).toDouble())
        val splashX = cx + (cos(offsetAngle) * 45f).toFloat()
        val splashY = cy + (sin(offsetAngle) * 30f).toFloat()
        drawCircle(color = color.copy(alpha = 0.85f), radius = 18f, center = Offset(splashX, splashY))
    }

    // Paintbrush
    val brushAngle = if (isPlaying) sin(Math.toRadians(phase.toDouble()).toFloat()) * 20f else 0f
    val brushTipX = cx + 40f
    val brushTipY = cy + 20f
    drawLine(
        color = Color(0xFFD97706),
        start = Offset(brushTipX + 80f, brushTipY - 80f),
        end = Offset(brushTipX, brushTipY),
        strokeWidth = 10f,
        cap = StrokeCap.Round
    )
    drawCircle(Color(0xFFEC4899), 10f, Offset(brushTipX, brushTipY))
}

private fun DrawScope.drawChoirScene(w: Float, h: Float, isPlaying: Boolean, progress: Float, phase: Float, pulse: Float) {
    // Musical Hall Gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF172554))
        ),
        size = Size(w, h)
    )

    // Musical Equalizer Bars
    val barCount = 14
    val barWidth = (w * 0.7f) / barCount
    val startX = w * 0.15f
    val baseY = h * 0.72f

    for (i in 0 until barCount) {
        val heightMultiplier = if (isPlaying) {
            val offset = (i * 30 + phase) % 360
            (sin(Math.toRadians(offset.toDouble())).toFloat().coerceIn(-1f, 1f) + 1.2f) / 2.2f
        } else {
            0.35f
        }
        val barH = (h * 0.45f) * heightMultiplier

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFFC084FC))
            ),
            topLeft = Offset(startX + (i * barWidth) + 4f, baseY - barH),
            size = Size(barWidth - 8f, barH),
            cornerRadius = CornerRadius(4f, 4f)
        )
    }

    // Music note floating
    val noteX = w * 0.5f + (sin(Math.toRadians(phase.toDouble())).toFloat() * 60f)
    val noteY = h * 0.25f
    drawCircle(Color(0xFFFEF08A), 12f * pulse, Offset(noteX, noteY))
    drawLine(Color(0xFFFEF08A), Offset(noteX + 10f, noteY), Offset(noteX + 10f, noteY - 35f), strokeWidth = 5f, cap = StrokeCap.Round)
    drawLine(Color(0xFFFEF08A), Offset(noteX + 10f, noteY - 35f), Offset(noteX + 28f, noteY - 28f), strokeWidth = 5f, cap = StrokeCap.Round)
}
