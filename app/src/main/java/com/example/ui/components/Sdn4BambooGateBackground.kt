package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Authentic visual illustration of the traditional Sundanese Bamboo Gate of SDN 4 Putrajawa:
 * - Thatched Palm Roof (Atap Ijuk / Rumbia Alang-Alang)
 * - Wooden signboard reading:
 *     "Selamat Datang"
 *     "Di"
 *     "SDN 4 PUTRAJAWA"
 * - Sturdy multi-pole bamboo columns & diagonal lattice braces (Anyaman Bambu)
 * - Left & Right Bamboo Fences (Pagar Bambu)
 * - Inside Courtyard view: Green classrooms with terracotta roofs & Indonesian Flag (Merah Putih)
 */
@Composable
fun Sdn4BambooGateBackground(
    modifier: Modifier = Modifier,
    dimOverlayAlpha: Float = 0.25f
) {
    val textMeasurer = rememberTextMeasurer()

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBambooGateScene(size, textMeasurer)
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

/**
 * Draws the complete SDN 4 Putrajawa Bamboo Gate scenery based on the official school photo
 */
fun DrawScope.drawBambooGateScene(size: Size, textMeasurer: TextMeasurer) {
    val width = size.width
    val height = size.height

    // -------------------------------------------------------------
    // 1. SKY & SUNLIGHT (Langit Cerah & Cahaya Matahari)
    // -------------------------------------------------------------
    val skyHeight = height * 0.55f
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF38BDF8), // Light vibrant cerulean sky
                Color(0xFF7DD3FC),
                Color(0xFFBAE6FD),
                Color(0xFFE0F2FE)
            ),
            startY = 0f,
            endY = skyHeight
        ),
        size = Size(width, skyHeight)
    )

    // Soft cumulus clouds
    drawCircle(color = Color.White.copy(alpha = 0.45f), radius = height * 0.09f, center = Offset(width * 0.25f, height * 0.18f))
    drawCircle(color = Color.White.copy(alpha = 0.55f), radius = height * 0.12f, center = Offset(width * 0.35f, height * 0.16f))
    drawCircle(color = Color.White.copy(alpha = 0.40f), radius = height * 0.08f, center = Offset(width * 0.45f, height * 0.19f))
    drawCircle(color = Color.White.copy(alpha = 0.50f), radius = height * 0.10f, center = Offset(width * 0.68f, height * 0.14f))
    drawCircle(color = Color.White.copy(alpha = 0.45f), radius = height * 0.08f, center = Offset(width * 0.76f, height * 0.16f))

    // -------------------------------------------------------------
    // 2. BACKGROUND TREE CANOPY (Pohon-Pohon Rindang di Belakang Sekolah)
    // -------------------------------------------------------------
    for (i in 0..14) {
        val treeX = width * (i / 14f) + (i % 2) * (width * 0.02f)
        val treeY = height * 0.38f + (if (i % 2 == 0) -6f else 4f)
        val treeRadius = height * (0.13f + (i % 3) * 0.02f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    if (i % 2 == 0) Color(0xFF15803D) else Color(0xFF166534),
                    if (i % 2 == 0) Color(0xFF166534) else Color(0xFF14532D)
                ),
                center = Offset(treeX, treeY - 8f),
                radius = treeRadius
            ),
            radius = treeRadius,
            center = Offset(treeX, treeY)
        )
    }

    // -------------------------------------------------------------
    // 3. COURTYARD GROUND & PATHWAY (Jalan Masuk Paving & Halaman)
    // -------------------------------------------------------------
    val groundY = height * 0.52f
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFE2E8F0), // Clean stone walkway
                Color(0xFFCBD5E1),
                Color(0xFF94A3B8),
                Color(0xFF64748B)
            ),
            startY = groundY,
            endY = height
        ),
        topLeft = Offset(0f, groundY),
        size = Size(width, height - groundY)
    )

    // Perspective stone texture lines on pathway
    for (i in 1..9) {
        val y = groundY + (height - groundY) * (i / 10f)
        drawLine(
            color = Color(0xFF475569).copy(alpha = 0.25f),
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f
        )
    }

    // -------------------------------------------------------------
    // 4. INNER SCHOOL BUILDINGS (Gedung Sekolah Hijau Genteng Merah)
    // -------------------------------------------------------------
    // Center Classroom Building
    val centerBldgW = width * 0.44f
    val centerBldgLeft = (width - centerBldgW) / 2f
    val centerBldgBaseY = height * 0.60f
    val centerBldgH = height * 0.16f

    // Wall (Green SD SDN 4)
    drawRect(
        color = Color(0xFF84CC16), // Bright lively lime-green school wall
        topLeft = Offset(centerBldgLeft, centerBldgBaseY - centerBldgH),
        size = Size(centerBldgW, centerBldgH)
    )
    drawRect(
        color = Color(0xFF4D7C0F), // Bottom darker green dado
        topLeft = Offset(centerBldgLeft, centerBldgBaseY - centerBldgH * 0.35f),
        size = Size(centerBldgW, centerBldgH * 0.35f)
    )

    // Center Building Terracotta Roof
    val centerRoofPeakY = centerBldgBaseY - centerBldgH - height * 0.12f
    val centerRoofEaveY = centerBldgBaseY - centerBldgH
    val centerRoof = Path().apply {
        moveTo(centerBldgLeft - width * 0.03f, centerRoofEaveY)
        lineTo(width / 2f, centerRoofPeakY)
        lineTo(centerBldgLeft + centerBldgW + width * 0.03f, centerRoofEaveY)
        close()
    }
    drawPath(
        path = centerRoof,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFEA580C), Color(0xFFC2410C), Color(0xFF9A3412)),
            startY = centerRoofPeakY,
            endY = centerRoofEaveY
        )
    )

    // Windows & Door on center building
    drawRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(width / 2f - 10f, centerBldgBaseY - centerBldgH * 0.75f),
        size = Size(20f, centerBldgH * 0.75f)
    )
    for (w in listOf(centerBldgLeft + 14f, centerBldgLeft + centerBldgW - 40f)) {
        drawRect(
            color = Color(0xFF38BDF8),
            topLeft = Offset(w, centerBldgBaseY - centerBldgH * 0.65f),
            size = Size(26f, centerBldgH * 0.40f)
        )
        drawRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(w, centerBldgBaseY - centerBldgH * 0.65f),
            size = Size(26f, centerBldgH * 0.40f),
            style = Stroke(width = 1.2f)
        )
    }

    // Left Wing Building (Perspektif Sayap Kiri)
    val leftWingPath = Path().apply {
        moveTo(0f, height * 0.36f)
        lineTo(centerBldgLeft, centerBldgBaseY - centerBldgH)
        lineTo(centerBldgLeft, centerBldgBaseY)
        lineTo(0f, height * 0.68f)
        close()
    }
    drawPath(
        path = leftWingPath,
        color = Color(0xFF65A30D)
    )
    // Left Wing Terracotta Roof
    val leftRoofPath = Path().apply {
        moveTo(0f, height * 0.28f)
        lineTo(centerBldgLeft, centerBldgBaseY - centerBldgH - height * 0.08f)
        lineTo(centerBldgLeft, centerBldgBaseY - centerBldgH)
        lineTo(0f, height * 0.38f)
        close()
    }
    drawPath(
        path = leftRoofPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFC2410C), Color(0xFF9A3412)),
            startY = height * 0.28f,
            endY = height * 0.42f
        )
    )

    // Right Wing Building (Perspektif Sayap Kanan)
    val rightWingPath = Path().apply {
        moveTo(width, height * 0.36f)
        lineTo(centerBldgLeft + centerBldgW, centerBldgBaseY - centerBldgH)
        lineTo(centerBldgLeft + centerBldgW, centerBldgBaseY)
        lineTo(width, height * 0.68f)
        close()
    }
    drawPath(
        path = rightWingPath,
        color = Color(0xFF65A30D)
    )
    // Right Wing Terracotta Roof
    val rightRoofPath = Path().apply {
        moveTo(width, height * 0.28f)
        lineTo(centerBldgLeft + centerBldgW, centerBldgBaseY - centerBldgH - height * 0.08f)
        lineTo(centerBldgLeft + centerBldgW, centerBldgBaseY - centerBldgH)
        lineTo(width, height * 0.38f)
        close()
    }
    drawPath(
        path = rightRoofPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFC2410C), Color(0xFF9A3412)),
            startY = height * 0.28f,
            endY = height * 0.42f
        )
    )

    // -------------------------------------------------------------
    // 5. INDONESIAN FLAG ON POLE (Bendera Merah Putih di Lapangan)
    // -------------------------------------------------------------
    val flagPoleX = width / 2f
    val flagPoleTopY = height * 0.42f
    val flagPoleBottomY = height * 0.58f

    // Flagpole
    drawLine(
        color = Color(0xFFCBD5E1),
        start = Offset(flagPoleX, flagPoleBottomY),
        end = Offset(flagPoleX, flagPoleTopY),
        strokeWidth = 2f
    )
    drawCircle(color = Color(0xFFFBBF24), radius = 2.5f, center = Offset(flagPoleX, flagPoleTopY))

    // Fluttering Red and White Flag
    val flagW = width * 0.045f
    val flagH = height * 0.035f
    val flagRed = Path().apply {
        moveTo(flagPoleX, flagPoleTopY + 2f)
        quadraticBezierTo(flagPoleX + flagW * 0.5f, flagPoleTopY, flagPoleX + flagW, flagPoleTopY + 2f)
        lineTo(flagPoleX + flagW, flagPoleTopY + flagH / 2f + 2f)
        quadraticBezierTo(flagPoleX + flagW * 0.5f, flagPoleTopY + flagH / 2f, flagPoleX, flagPoleTopY + flagH / 2f + 2f)
        close()
    }
    drawPath(path = flagRed, color = Color(0xFFDC2626))
    val flagWhite = Path().apply {
        moveTo(flagPoleX, flagPoleTopY + flagH / 2f + 2f)
        quadraticBezierTo(flagPoleX + flagW * 0.5f, flagPoleTopY + flagH / 2f, flagPoleX + flagW, flagPoleTopY + flagH / 2f + 2f)
        lineTo(flagPoleX + flagW, flagPoleTopY + flagH + 2f)
        quadraticBezierTo(flagPoleX + flagW * 0.5f, flagPoleTopY + flagH, flagPoleX, flagPoleTopY + flagH + 2f)
        close()
    }
    drawPath(path = flagWhite, color = Color.White)

    // Potted plants along the inner pathway
    for (i in 0..4) {
        val leftPotX = width * (0.28f + i * 0.04f)
        val leftPotY = height * (0.64f - i * 0.02f)
        drawCircle(color = Color(0xFF15803D), radius = 6f, center = Offset(leftPotX, leftPotY))
        drawCircle(color = Color(0xFFF59E0B), radius = 2.5f, center = Offset(leftPotX, leftPotY - 3f))

        val rightPotX = width * (0.72f - i * 0.04f)
        val rightPotY = height * (0.64f - i * 0.02f)
        drawCircle(color = Color(0xFF15803D), radius = 6f, center = Offset(rightPotX, rightPotY))
        drawCircle(color = Color(0xFFEF4444), radius = 2.5f, center = Offset(rightPotX, rightPotY - 3f))
    }

    // -------------------------------------------------------------
    // 6. TRADITIONAL BAMBOO FENCES (Pagar Bambu Kiri & Kanan)
    // -------------------------------------------------------------
    val fenceLeftW = width * 0.22f
    val fenceRightW = width * 0.22f
    val fenceTopY = height * 0.56f
    val fenceBottomY = height

    // Left Fence
    drawBambooFence(
        startX = 0f,
        endX = fenceLeftW,
        topY = fenceTopY,
        bottomY = fenceBottomY
    )

    // Right Fence
    drawBambooFence(
        startX = width - fenceRightW,
        endX = width,
        topY = fenceTopY,
        bottomY = fenceBottomY
    )

    // -------------------------------------------------------------
    // 7. GRAND BAMBOO GATE PILLARS (Tiang Utama Gapura Bambu Kiri & Kanan)
    // -------------------------------------------------------------
    val pillarLeftX = fenceLeftW
    val pillarRightX = width - fenceRightW
    val pillarW = width * 0.09f
    val pillarTopY = height * 0.20f
    val pillarBottomY = height

    // Left Giant Bamboo Column Bundle (Tiang Bambu Kiri)
    drawBambooColumn(
        startX = pillarLeftX,
        width = pillarW,
        topY = pillarTopY,
        bottomY = pillarBottomY
    )

    // Right Giant Bamboo Column Bundle (Tiang Bambu Kanan)
    drawBambooColumn(
        startX = pillarRightX - pillarW,
        width = pillarW,
        topY = pillarTopY,
        bottomY = pillarBottomY
    )

    // Diagonal Cross-Lattice Bamboo Braces (Anyaman Silang Penguat Bambu)
    val innerLeft = pillarLeftX + pillarW
    val innerRight = pillarRightX - pillarW
    val crossYTop = height * 0.26f
    val crossYBottom = height * 0.42f

    drawBambooLattice(
        left = innerLeft,
        right = innerRight,
        top = crossYTop,
        bottom = crossYBottom
    )

    // -------------------------------------------------------------
    // 8. BAMBOO ARCHWAY & WOODEN SIGNBOARDS (Papan Nama Kayu & Gapura)
    // -------------------------------------------------------------
    // Top Horizontal Supporting Bamboo Beams
    drawBambooBeam(innerLeft - 8f, innerRight + 8f, height * 0.15f, 10f)
    drawBambooBeam(innerLeft - 12f, innerRight + 12f, height * 0.25f, 12f)
    drawBambooBeam(innerLeft - 8f, innerRight + 8f, height * 0.38f, 10f)

    // Wooden Signboard 1: "Selamat Datang"
    val sign1W = (innerRight - innerLeft) * 0.70f
    val sign1Left = (width - sign1W) / 2f
    val sign1Top = height * 0.16f
    val sign1H = height * 0.085f

    drawWoodenBoard(
        left = sign1Left,
        top = sign1Top,
        width = sign1W,
        height = sign1H
    )

    // Text: "Selamat Datang" (Cursive Golden Yellow)
    val text1 = textMeasurer.measure(
        AnnotatedString("Selamat Datang"),
        style = TextStyle(
            color = Color(0xFFFFFBEB),
            fontSize = (height * 0.048f).coerceIn(12f, 26f).sp,
            fontWeight = FontWeight.ExtraBold,
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
            shadow = Shadow(
                color = Color(0xFF1E0E03),
                offset = Offset(1.5f, 2f),
                blurRadius = 4f
            )
        )
    )
    drawText(
        textLayoutResult = text1,
        topLeft = Offset((width - text1.size.width) / 2f, sign1Top + (sign1H - text1.size.height) / 2f)
    )

    // Wooden Signboard 2: "Di" (Small Central Badge)
    val sign2W = sign1W * 0.25f
    val sign2Left = (width - sign2W) / 2f
    val sign2Top = sign1Top + sign1H + 2f
    val sign2H = height * 0.035f

    drawWoodenBoard(
        left = sign2Left,
        top = sign2Top,
        width = sign2W,
        height = sign2H
    )

    val text2 = textMeasurer.measure(
        AnnotatedString("Di"),
        style = TextStyle(
            color = Color(0xFFFFFBEB),
            fontSize = (height * 0.024f).coerceIn(10f, 16f).sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            shadow = Shadow(
                color = Color(0xFF1E0E03),
                offset = Offset(1f, 1.5f),
                blurRadius = 2f
            )
        )
    )
    drawText(
        textLayoutResult = text2,
        topLeft = Offset((width - text2.size.width) / 2f, sign2Top + (sign2H - text2.size.height) / 2f)
    )

    // Wooden Signboard 3: "SDN 4 PUTRAJAWA" (Main Golden 3D Board)
    val sign3W = (innerRight - innerLeft) * 0.86f
    val sign3Left = (width - sign3W) / 2f
    val sign3Top = sign2Top + sign2H + 2f
    val sign3H = height * 0.088f

    drawWoodenBoard(
        left = sign3Left,
        top = sign3Top,
        width = sign3W,
        height = sign3H,
        isMain = true
    )

    val text3 = textMeasurer.measure(
        AnnotatedString("SDN 4 PUTRAJAWA"),
        style = TextStyle(
            color = Color(0xFFFFFDE7),
            fontSize = (height * 0.050f).coerceIn(13f, 32f).sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.3.sp,
            shadow = Shadow(
                color = Color(0xFF1E0E03),
                offset = Offset(2f, 2.5f),
                blurRadius = 5f
            )
        )
    )
    drawText(
        textLayoutResult = text3,
        topLeft = Offset((width - text3.size.width) / 2f, sign3Top + (sign3H - text3.size.height) / 2f)
    )

    // -------------------------------------------------------------
    // 9. THATCHED PALM ROOF (Atap Ijuk / Rumbia Gapura Bambu)
    // -------------------------------------------------------------
    val roofLeft = pillarLeftX - width * 0.08f
    val roofRight = pillarRightX + width * 0.08f
    val roofPeakY = 0f
    val roofEaveY = height * 0.16f

    // Main Thatched Roof Shape
    val thatchPath = Path().apply {
        moveTo(roofLeft, roofEaveY)
        lineTo(roofLeft + width * 0.08f, roofPeakY)
        lineTo(roofRight - width * 0.08f, roofPeakY)
        lineTo(roofRight, roofEaveY)
        close()
    }

    drawPath(
        path = thatchPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF5C3317), // Deep palm thatch fiber
                Color(0xFF78350F), // Warm amber brown
                Color(0xFF92400E),
                Color(0xFFB45309)
            ),
            startY = roofPeakY,
            endY = roofEaveY
        )
    )

    // Thatched Straw Textures (Serat-Serat Jerami Ijuk)
    for (i in 0..35) {
        val xRatio = i / 35f
        val startX = roofLeft + width * 0.08f + (roofRight - roofLeft - width * 0.16f) * xRatio
        val endX = roofLeft + (roofRight - roofLeft) * xRatio + (if (i % 2 == 0) -3f else 4f)
        drawLine(
            color = if (i % 2 == 0) Color(0xFF3E1F07) else Color(0xFFD97706),
            start = Offset(startX, roofPeakY),
            end = Offset(endX, roofEaveY + (i % 3) * 3f),
            strokeWidth = 1.2f
        )
    }

    // Bamboo Ridge Cap on Roof Peak (Wuwungan Bambu di Puncak Atap)
    drawBambooBeam(roofLeft + width * 0.07f, roofRight - width * 0.07f, roofPeakY + 4f, 8f)
}

/**
 * Draws a multi-pole bamboo column (Tiang Rangka Bambu Kuning/Cokelat Mengkilap)
 */
private fun DrawScope.drawBambooColumn(startX: Float, width: Float, topY: Float, bottomY: Float) {
    val poleCount = 4
    val poleW = width / poleCount

    for (p in 0 until poleCount) {
        val poleX = startX + p * poleW

        // Bamboo cylinder gradient
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF92400E), // Shadow side
                    Color(0xFFD97706), // Warm bamboo
                    Color(0xFFFBBF24), // Bamboo sheen highlight
                    Color(0xFFB45309)  // Edge shadow
                ),
                startX = poleX,
                endX = poleX + poleW
            ),
            topLeft = Offset(poleX, topY),
            size = Size(poleW, bottomY - topY)
        )

        // Bamboo node rings (Ruas-Ruas Bambu)
        val nodeCount = 10
        val nodeStep = (bottomY - topY) / nodeCount
        for (n in 1..nodeCount) {
            val nodeY = topY + n * nodeStep + (p % 2) * 4f
            // Dark ring groove
            drawLine(
                color = Color(0xFF451A03),
                start = Offset(poleX, nodeY),
                end = Offset(poleX + poleW, nodeY),
                strokeWidth = 2.5f
            )
            // Light highlight ring
            drawLine(
                color = Color(0xFFFEF08A).copy(alpha = 0.8f),
                start = Offset(poleX, nodeY - 1.5f),
                end = Offset(poleX + poleW, nodeY - 1.5f),
                strokeWidth = 1.2f
            )
        }
    }

    // Black rattan/rope ties (Tali Ijuk Pengikat Tiang Bambu)
    val tiePositions = listOf(0.15f, 0.45f, 0.75f)
    for (ratio in tiePositions) {
        val tieY = topY + (bottomY - topY) * ratio
        drawRect(
            color = Color(0xFF1E1B18),
            topLeft = Offset(startX - 2f, tieY),
            size = Size(width + 4f, 8f)
        )
        // Rope texture
        for (t in 0..3) {
            drawLine(
                color = Color(0xFF451A03),
                start = Offset(startX - 2f, tieY + t * 2f),
                end = Offset(startX + width + 2f, tieY + t * 2f),
                strokeWidth = 1f
            )
        }
    }
}

/**
 * Draws horizontal bamboo beam
 */
private fun DrawScope.drawBambooBeam(startX: Float, endX: Float, y: Float, thickness: Float) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFEF08A),
                Color(0xFFD97706),
                Color(0xFF78350F)
            ),
            startY = y - thickness / 2f,
            endY = y + thickness / 2f
        ),
        topLeft = Offset(startX, y - thickness / 2f),
        size = Size(endX - startX, thickness)
    )

    // Ring nodes on horizontal beam
    val length = endX - startX
    var rx = startX + 20f
    while (rx < endX - 10f) {
        drawLine(
            color = Color(0xFF451A03),
            start = Offset(rx, y - thickness / 2f),
            end = Offset(rx, y + thickness / 2f),
            strokeWidth = 2f
        )
        rx += 32f
    }
}

/**
 * Draws wooden signboard with rustic texture & borders
 */
private fun DrawScope.drawWoodenBoard(left: Float, top: Float, width: Float, height: Float, isMain: Boolean = false) {
    // Outer shadow
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.5f),
        topLeft = Offset(left + 2f, top + 3f),
        size = Size(width, height),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Wood plank fill
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF451A03), // Dark stained teak / jati
                Color(0xFF5C2C16),
                Color(0xFF381A08)
            ),
            startY = top,
            endY = top + height
        ),
        topLeft = Offset(left, top),
        size = Size(width, height),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Wooden border frame
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFD97706), Color(0xFF78350F)),
            startY = top,
            endY = top + height
        ),
        topLeft = Offset(left, top),
        size = Size(width, height),
        cornerRadius = CornerRadius(6f, 6f),
        style = Stroke(width = if (isMain) 2.5f else 1.8f)
    )

    // Wood grain lines
    for (g in 1..3) {
        val gy = top + height * (g / 4f)
        drawLine(
            color = Color(0xFF261005).copy(alpha = 0.6f),
            start = Offset(left + 6f, gy),
            end = Offset(left + width - 6f, gy),
            strokeWidth = 1f
        )
    }
}

/**
 * Draws diagonal criss-cross bamboo lattice (Anyaman Bambu Tradisional)
 */
private fun DrawScope.drawBambooLattice(left: Float, right: Float, top: Float, bottom: Float) {
    val step = 20f

    // Diagonal forward slashes \
    var x = left - (bottom - top)
    while (x < right) {
        drawLine(
            color = Color(0xFFD97706).copy(alpha = 0.7f),
            start = Offset(x, top),
            end = Offset(x + (bottom - top), bottom),
            strokeWidth = 2.5f
        )
        x += step
    }

    // Diagonal back slashes /
    x = left
    while (x < right + (bottom - top)) {
        drawLine(
            color = Color(0xFFB45309).copy(alpha = 0.65f),
            start = Offset(x, top),
            end = Offset(x - (bottom - top), bottom),
            strokeWidth = 2.5f
        )
        x += step
    }
}

/**
 * Draws bamboo fences on the sides (Pagar Bambu SDN 4 Putrajawa)
 */
private fun DrawScope.drawBambooFence(startX: Float, endX: Float, topY: Float, bottomY: Float) {
    val width = endX - startX

    // Vertical Bamboo Slats (Bambu-Bambu Vertikal Rapat)
    val slatCount = (width / 8f).toInt().coerceAtLeast(4)
    val slatW = width / slatCount

    for (s in 0 until slatCount) {
        val sx = startX + s * slatW
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF92400E),
                    Color(0xFFD97706),
                    Color(0xFFFBBF24),
                    Color(0xFF78350F)
                ),
                startX = sx,
                endX = sx + slatW - 1f
            ),
            topLeft = Offset(sx, topY + (s % 3) * 3f),
            size = Size(slatW - 1f, bottomY - topY)
        )
    }

    // Horizontal cross rails
    drawBambooBeam(startX, endX, topY + (bottomY - topY) * 0.25f, 6f)
    drawBambooBeam(startX, endX, topY + (bottomY - topY) * 0.65f, 6f)
}
