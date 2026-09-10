package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Modern, realistic, and futuristic digital school header background for SDN 4 Putrajawa.
 * Features:
 * - High-tech cyber grid and digital perspective floor
 * - Sleek modern architectural silhouette of SDN 4 Putrajawa school building with glowing windows
 * - Glowing digital network nodes, cyber particles, and circuit fiber lines
 * - Elegant golden & emerald cyber badge for SDN 4 Putrajawa
 * - Deep navy & cyber cyan futuristic ambiance perfectly matching online community branding
 */
@Composable
fun DigitalSchoolHeaderBackground(
    modifier: Modifier = Modifier,
    dimOverlayAlpha: Float = 0.0f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val gridOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "grid"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawFuturisticDigitalSchool(size, pulseAlpha, gridOffset)
        }

        if (dimOverlayAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = dimOverlayAlpha))
            )
        }
    }
}

private fun DrawScope.drawFuturisticDigitalSchool(
    size: Size,
    pulseAlpha: Float,
    gridOffset: Float
) {
    val w = size.width
    val h = size.height

    // 1. Deep Cyber Night Sky Gradient with warm twilight horizon
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF030712), // Deepest dark slate
                Color(0xFF071426), // Deep cyber blue
                Color(0xFF0F2B48), // Midnight navy
                Color(0xFF1E3A8A), // Royal sapphire
                Color(0xFF854D0E).copy(alpha = 0.4f), // Warm twilight sunset touch
                Color(0xFF070F1E)  // Bottom grounding
            ),
            startY = 0f,
            endY = h
        ),
        size = size
    )

    // 1b. Distant Majestic Mountain Silhouettes (Gunung Cikuray / Guntur Garut)
    val mountainFar = Path().apply {
        moveTo(0f, h * 0.48f)
        cubicTo(w * 0.2f, h * 0.32f, w * 0.35f, h * 0.44f, w * 0.55f, h * 0.36f)
        cubicTo(w * 0.75f, h * 0.30f, w * 0.88f, h * 0.42f, w, h * 0.45f)
        lineTo(w, h * 0.68f)
        lineTo(0f, h * 0.68f)
        close()
    }
    drawPath(
        path = mountainFar,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF0E2442).copy(alpha = 0.75f), Color(0xFF0B192C)),
            startY = h * 0.30f,
            endY = h * 0.68f
        )
    )

    val mountainNear = Path().apply {
        moveTo(0f, h * 0.53f)
        cubicTo(w * 0.28f, h * 0.42f, w * 0.48f, h * 0.50f, w * 0.72f, h * 0.40f)
        cubicTo(w * 0.85f, h * 0.46f, w * 0.95f, h * 0.44f, w, h * 0.52f)
        lineTo(w, h * 0.68f)
        lineTo(0f, h * 0.68f)
        close()
    }
    drawPath(
        path = mountainNear,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF08182B).copy(alpha = 0.9f), Color(0xFF070F1E)),
            startY = h * 0.40f,
            endY = h * 0.68f
        )
    )

    // 2. Ambient Cyber Glow Orbs (Cyan & Emerald & Gold)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0x5538BDF8),
                Color(0x110284C7),
                Color.Transparent
            ),
            center = Offset(w * 0.5f, h * 0.45f),
            radius = w * 0.48f
        ),
        radius = w * 0.48f,
        center = Offset(w * 0.5f, h * 0.45f)
    )

    // Emerald School Accent Glow (left side)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0x3310B981),
                Color.Transparent
            ),
            center = Offset(w * 0.15f, h * 0.35f),
            radius = w * 0.35f
        ),
        radius = w * 0.35f,
        center = Offset(w * 0.15f, h * 0.35f)
    )

    // Golden Sunset Tech Glow (right side)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0x25F59E0B),
                Color.Transparent
            ),
            center = Offset(w * 0.85f, h * 0.35f),
            radius = w * 0.35f
        ),
        radius = w * 0.35f,
        center = Offset(w * 0.85f, h * 0.35f)
    )

    // 3. Cyber Digital Grid Floor / Horizon
    val horizonY = h * 0.68f
    val gridLines = 14
    for (i in 0..gridLines) {
        val y = horizonY + (h - horizonY) * (i.toFloat() / gridLines)
        val alpha = (0.12f + (i.toFloat() / gridLines) * 0.35f) * pulseAlpha
        drawLine(
            color = Color(0xFF38BDF8).copy(alpha = alpha),
            start = Offset(0f, y),
            end = Offset(w, y),
            strokeWidth = 1.2f
        )
    }

    // Perspective lines radiating to horizon center
    val perspectiveCount = 16
    for (i in 0..perspectiveCount) {
        val xBottom = (w / perspectiveCount) * i
        val xTop = w * 0.5f + (xBottom - w * 0.5f) * 0.18f
        drawLine(
            color = Color(0xFF38BDF8).copy(alpha = 0.18f * pulseAlpha),
            start = Offset(xTop, horizonY),
            end = Offset(xBottom, h),
            strokeWidth = 1f
        )
    }

    // 4. Modern Realistic Architectural School Building
    val bldgLeft = w * 0.08f
    val bldgRight = w * 0.92f
    val bldgTop = h * 0.32f
    val bldgHeight = horizonY - bldgTop

    // Main school central pavilion with modern terracotta roof & glass facade
    val roofPath = Path().apply {
        moveTo(w * 0.5f - w * 0.24f, bldgTop)
        lineTo(w * 0.5f - w * 0.14f, bldgTop - h * 0.13f)
        lineTo(w * 0.5f + w * 0.14f, bldgTop - h * 0.13f)
        lineTo(w * 0.5f + w * 0.24f, bldgTop)
        close()
    }
    drawPath(
        path = roofPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFC2410C), // Clay terracotta
                Color(0xFF9A3412),
                Color(0xFF431407)
            ),
            startY = bldgTop - h * 0.13f,
            endY = bldgTop
        )
    )
    drawPath(
        path = roofPath,
        color = Color(0xFFF59E0B).copy(alpha = 0.8f * pulseAlpha),
        style = Stroke(width = 1.8f)
    )

    // Side building wings (Kiri & Kanan SDN 4 Putrajawa)
    // Left Wing (White wall + Emerald Green accent base)
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFE2E8F0), Color(0xFF15803D)),
            startY = bldgTop,
            endY = horizonY
        ),
        topLeft = Offset(bldgLeft, bldgTop),
        size = Size(w * 0.26f, bldgHeight)
    )
    // Left Wing Clay Roof
    val leftRoof = Path().apply {
        moveTo(bldgLeft - 6f, bldgTop)
        lineTo(bldgLeft + 15f, bldgTop - h * 0.07f)
        lineTo(bldgLeft + w * 0.26f, bldgTop - h * 0.07f)
        lineTo(bldgLeft + w * 0.26f, bldgTop)
        close()
    }
    drawPath(
        path = leftRoof,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF9A3412), Color(0xFF7C2D12)),
            startY = bldgTop - h * 0.07f,
            endY = bldgTop
        )
    )
    drawPath(
        path = leftRoof,
        color = Color(0xFF10B981).copy(alpha = 0.7f * pulseAlpha),
        style = Stroke(width = 1.2f)
    )

    // Right Wing (White wall + Emerald Green accent base)
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFE2E8F0), Color(0xFF15803D)),
            startY = bldgTop,
            endY = horizonY
        ),
        topLeft = Offset(w * 0.66f, bldgTop),
        size = Size(w * 0.26f, bldgHeight)
    )
    // Right Wing Clay Roof
    val rightRoof = Path().apply {
        moveTo(w * 0.66f, bldgTop)
        lineTo(w * 0.66f, bldgTop - h * 0.07f)
        lineTo(bldgRight - 15f, bldgTop - h * 0.07f)
        lineTo(bldgRight + 6f, bldgTop)
        close()
    }
    drawPath(
        path = rightRoof,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF9A3412), Color(0xFF7C2D12)),
            startY = bldgTop - h * 0.07f,
            endY = bldgTop
        )
    )
    drawPath(
        path = rightRoof,
        color = Color(0xFF38BDF8).copy(alpha = 0.7f * pulseAlpha),
        style = Stroke(width = 1.2f)
    )

    // Center Grand Academic Building
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFF8FAFC), Color(0xFFCBD5E1), Color(0xFF065F46)),
            startY = bldgTop,
            endY = horizonY
        ),
        topLeft = Offset(w * 0.32f, bldgTop),
        size = Size(w * 0.36f, bldgHeight)
    )
    drawRect(
        color = Color(0xFFF59E0B).copy(alpha = 0.7f * pulseAlpha),
        topLeft = Offset(w * 0.32f, bldgTop),
        size = Size(w * 0.36f, bldgHeight),
        style = Stroke(width = 1.5f)
    )

    // Modern glowing windows & classroom interior lighting
    val windowCols = 6
    val windowRows = 2
    for (r in 0 until windowRows) {
        val winY = bldgTop + 14f + r * (bldgHeight * 0.42f)
        for (c in 0 until windowCols) {
            val winX = bldgLeft + 12f + c * (w * 0.78f / windowCols)
            if (winX + 16f < bldgRight) {
                val isWarm = (c + r) % 2 == 0
                val winColor = if (isWarm) Color(0xFFFDE047) else Color(0xFF38BDF8)
                drawRoundRect(
                    color = winColor.copy(alpha = 0.85f),
                    topLeft = Offset(winX, winY),
                    size = Size(18f, 14f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
                )
                // Window frame
                drawRoundRect(
                    color = Color(0xFF0F172A),
                    topLeft = Offset(winX, winY),
                    size = Size(18f, 14f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f),
                    style = Stroke(width = 1f)
                )
            }
        }
    }

    // Indonesian Flag Pole & Merah Putih in center
    val poleX = w * 0.5f
    val poleBaseY = horizonY
    val poleTopY = bldgTop - h * 0.16f
    drawLine(
        color = Color(0xFFE2E8F0),
        start = Offset(poleX, poleBaseY),
        end = Offset(poleX, poleTopY),
        strokeWidth = 2.5f
    )
    drawCircle(
        color = Color(0xFFFBBF24),
        radius = 3.5f,
        center = Offset(poleX, poleTopY)
    )
    // Fluttering Red & White Flag
    val flagW = w * 0.07f
    val flagH = h * 0.045f
    drawRect(
        color = Color(0xFFDC2626),
        topLeft = Offset(poleX + 1.5f, poleTopY + 3f),
        size = Size(flagW, flagH)
    )
    drawRect(
        color = Color.White,
        topLeft = Offset(poleX + 1.5f, poleTopY + 3f + flagH),
        size = Size(flagW, flagH)
    )

    // School Signboard Banner (SDN 4 PUTRAJAWA)
    val bannerW = w * 0.30f
    val bannerH = 20f
    val bannerX = w * 0.5f - bannerW / 2
    val bannerY = bldgTop - 12f
    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(Color(0xFF047857), Color(0xFF065F46))
        ),
        topLeft = Offset(bannerX, bannerY),
        size = Size(bannerW, bannerH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
    )
    drawRoundRect(
        color = Color(0xFFFBBF24),
        topLeft = Offset(bannerX, bannerY),
        size = Size(bannerW, bannerH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
        style = Stroke(width = 1.2f)
    )

    // Grand Entrance Glass Archway
    val archW = w * 0.12f
    val archH = bldgHeight * 0.52f
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF60A5FA).copy(alpha = 0.7f * pulseAlpha),
                Color(0xFF2563EB).copy(alpha = 0.9f)
            )
        ),
        topLeft = Offset(w * 0.5f - archW / 2, horizonY - archH),
        size = Size(archW, archH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
    )

    // 5. Cyber Circuit Lines & Digital Nodes (Internet / Online Community Theme)
    val nodes = listOf(
        Offset(w * 0.12f, h * 0.22f),
        Offset(w * 0.25f, h * 0.16f),
        Offset(w * 0.38f, h * 0.20f),
        Offset(w * 0.62f, h * 0.20f),
        Offset(w * 0.75f, h * 0.16f),
        Offset(w * 0.88f, h * 0.22f),
        Offset(w * 0.5f, h * 0.14f)
    )

    for (i in 0 until nodes.size - 1) {
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF38BDF8).copy(alpha = 0.5f * pulseAlpha),
                    Color(0xFF34D399).copy(alpha = 0.5f * pulseAlpha)
                ),
                start = nodes[i],
                end = nodes[i + 1]
            ),
            start = nodes[i],
            end = nodes[i + 1],
            strokeWidth = 1.2f
        )
    }

    nodes.forEachIndexed { idx, pt ->
        val nodeColor = when (idx % 3) {
            0 -> Color(0xFF38BDF8)
            1 -> Color(0xFFFDE047)
            else -> Color(0xFF34D399)
        }
        // Glowing halo
        drawCircle(
            color = nodeColor.copy(alpha = 0.35f * pulseAlpha),
            radius = 6.5f,
            center = pt
        )
        // Core node
        drawCircle(
            color = Color.White,
            radius = 3f,
            center = pt
        )
    }

    // 6. Floating Tech Data Bits / Sparkling Star Nodes
    val bitPoints = listOf(
        Offset(w * 0.18f, h * 0.08f),
        Offset(w * 0.82f, h * 0.09f),
        Offset(w * 0.06f, h * 0.45f),
        Offset(w * 0.94f, h * 0.45f),
        Offset(w * 0.28f, h * 0.38f),
        Offset(w * 0.72f, h * 0.38f),
        Offset(w * 0.45f, h * 0.07f),
        Offset(w * 0.55f, h * 0.07f)
    )
    bitPoints.forEachIndexed { i, p ->
        val bitAlpha = ((sin(i * 1.5 + gridOffset * 0.1) + 1.0) * 0.4 + 0.2).toFloat()
        drawCircle(
            color = Color(0xFF67E8F9).copy(alpha = bitAlpha.coerceIn(0.1f, 0.9f)),
            radius = 2f,
            center = p
        )
    }

    // 7. Base Modern Horizon Glowing Line
    drawLine(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                Color(0xFF38BDF8).copy(alpha = 0.8f * pulseAlpha),
                Color(0xFF34D399).copy(alpha = 0.9f * pulseAlpha),
                Color(0xFF38BDF8).copy(alpha = 0.8f * pulseAlpha),
                Color.Transparent
            )
        ),
        start = Offset(0f, horizonY),
        end = Offset(w, horizonY),
        strokeWidth = 2f
    )
}
