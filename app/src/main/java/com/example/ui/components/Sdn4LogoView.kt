package com.example.ui.components

import android.graphics.Color as AndroidColor
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Premium Custom Circular Vector Shield Logo for SDN 4 PUTRAJAWA
 * Featuring:
 * - Circular Arched Text "★ SDN 4 PUTRAJAWA ★" along the top curve
 * - Circular Bottom Text "CIBATU • GARUT" along the bottom curve
 * - Multi-ring Gold & Bronze embossed metallic outer rim with gear/beaded notches
 * - Deep Royal Navy & Blue sunburst inner canvas
 * - Golden Academic Toga / Graduation Cap & Open Book (Buku Terbuka & Toga)
 * - Golden Laurel Wreath (Padi & Daun Emas) + 5 Golden Stars
 */
@Composable
fun Sdn4SchoolLogo(
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    showGlow: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_sheen")
    val shimmerPhase = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = if (showGlow) 6.dp else 2.dp,
                shape = CircleShape,
                spotColor = Color(0xFF1E3A8A).copy(alpha = 0.5f),
                ambientColor = Color(0xFFD97706).copy(alpha = 0.35f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = this.size.width
            val height = this.size.height
            val center = Offset(width / 2f, height / 2f)
            val radius = width / 2f

            // ---------------------------------------------------------
            // 1. OUTER METALLIC GOLDEN BEVELED RIM (Lingkaran Emas Mewah)
            // ---------------------------------------------------------
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFEF08A), // Bright gold highlight
                        Color(0xFFF59E0B), // Rich amber gold
                        Color(0xFFB45309), // Bronze shadow
                        Color(0xFF78350F)
                    ),
                    center = Offset(center.x - radius * 0.25f, center.y - radius * 0.25f),
                    radius = radius * 1.2f
                ),
                radius = radius
            )

            // Outer Thin Accent Ring
            drawCircle(
                color = Color(0xFFFEF08A).copy(alpha = 0.9f),
                radius = radius - 1.5f,
                style = Stroke(width = (width * 0.018f).coerceAtLeast(1f))
            )

            // Decorative Beaded Gold Studs around circumference
            val studCount = 24
            val studDist = radius * 0.94f
            val studRadius = (width * 0.016f).coerceIn(1f, 3.5f)
            for (i in 0 until studCount) {
                val angle = (i * 360f / studCount) * (Math.PI / 180f).toFloat()
                val sx = center.x + cos(angle) * studDist
                val sy = center.y + sin(angle) * studDist
                drawCircle(
                    color = Color(0xFFFEF9C3),
                    radius = studRadius,
                    center = Offset(sx, sy)
                )
            }

            // ---------------------------------------------------------
            // 2. CIRCULAR TEXT BANNER TRACK (Jalur Melingkar Teks Biru Gelap)
            // ---------------------------------------------------------
            val textBandOuterR = radius * 0.90f
            val textBandInnerR = radius * 0.65f

            // Outer border of text track
            drawCircle(
                color = Color(0xFF78350F),
                radius = textBandOuterR,
                style = Stroke(width = (width * 0.02f).coerceAtLeast(1.2f))
            )

            // Navy Blue Ring Track Background for Circular Text
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E3A8A),
                        Color(0xFF0F172A),
                        Color(0xFF090D16)
                    ),
                    center = center,
                    radius = textBandOuterR
                ),
                radius = textBandOuterR
            )

            // Inner Gold Border separating Text Track and Center Core
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFFFEF08A),
                        Color(0xFFF59E0B),
                        Color(0xFFFEF08A),
                        Color(0xFFD97706),
                        Color(0xFFFEF08A)
                    ),
                    center = center
                ),
                radius = textBandInnerR,
                style = Stroke(width = (width * 0.032f).coerceAtLeast(1.5f))
            )

            // ---------------------------------------------------------
            // 3. CIRCULAR ARCHED TEXT: "SDN 4 PUTRAJAWA"
            // ---------------------------------------------------------
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas

                // Top Text: "★ SDN 4 PUTRAJAWA ★" (Arched Along Upper Arc)
                val topText = if (size < 40.dp) "SDN 4 PUTRAJAWA" else "★ SDN 4 PUTRAJAWA ★"
                val topTextSizePx = (width * 0.088f).coerceIn(7.5f, 26f)

                val topPaint = AndroidPaint().apply {
                    color = AndroidColor.parseColor("#FEF08A") // Bright Gold
                    textSize = topTextSizePx
                    isAntiAlias = true
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = AndroidPaint.Align.CENTER
                    setShadowLayer(3f, 1f, 1.5f, AndroidColor.parseColor("#451A03"))
                }

                val textPathRadius = (textBandOuterR + textBandInnerR) / 2f + (width * 0.015f)
                val topPath = AndroidPath().apply {
                    val rect = RectF(
                        center.x - textPathRadius,
                        center.y - textPathRadius,
                        center.x + textPathRadius,
                        center.y + textPathRadius
                    )
                    // Sweep from 195 deg to -15 deg (over the top)
                    addArc(rect, 195f, 150f)
                }
                nativeCanvas.drawTextOnPath(topText, topPath, 0f, 0f, topPaint)

                // Bottom Subtitle Text: "CIBATU • GARUT" (Arched Along Bottom Arc if size permits)
                if (size >= 44.dp) {
                    val bottomText = "CIBATU • GARUT"
                    val bottomTextSizePx = (width * 0.062f).coerceIn(6f, 16f)

                    val bottomPaint = AndroidPaint().apply {
                        color = AndroidColor.parseColor("#FDE68A")
                        textSize = bottomTextSizePx
                        isAntiAlias = true
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        textAlign = AndroidPaint.Align.CENTER
                        setShadowLayer(2f, 0.5f, 1f, AndroidColor.parseColor("#451A03"))
                    }

                    val bottomPathRadius = (textBandOuterR + textBandInnerR) / 2f + (width * 0.035f)
                    val bottomPath = AndroidPath().apply {
                        val rect = RectF(
                            center.x - bottomPathRadius,
                            center.y - bottomPathRadius,
                            center.x + bottomPathRadius,
                            center.y + bottomPathRadius
                        )
                        // Arc along bottom
                        addArc(rect, 25f, 130f)
                    }
                    nativeCanvas.drawTextOnPath(bottomText, bottomPath, 0f, 0f, bottomPaint)
                }
            }

            // ---------------------------------------------------------
            // 4. INNER CENTER EMBLEM CORE (Lingkaran Inti Royal Shield)
            // ---------------------------------------------------------
            val coreR = textBandInnerR - (width * 0.015f)

            // Deep Indigo / Royal Blue Gradient Fill
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2563EB), // Lively royal blue center
                        Color(0xFF1D4ED8),
                        Color(0xFF1E3A8A),
                        Color(0xFF0F172A)
                    ),
                    center = Offset(center.x, center.y - coreR * 0.2f),
                    radius = coreR
                ),
                radius = coreR
            )

            // Sunburst Light Rays behind Center Emblem
            val rayCount = 12
            for (i in 0 until rayCount) {
                val angle = (i * 360f / rayCount) * (Math.PI / 180f).toFloat()
                val r1 = coreR * 0.25f
                val r2 = coreR * 0.88f
                drawLine(
                    color = Color(0xFF60A5FA).copy(alpha = 0.25f),
                    start = Offset(center.x + cos(angle) * r1, center.y + sin(angle) * r1),
                    end = Offset(center.x + cos(angle) * r2, center.y + sin(angle) * r2),
                    strokeWidth = (width * 0.02f).coerceAtLeast(1f),
                    cap = StrokeCap.Round
                )
            }

            // ---------------------------------------------------------
            // 5. GOLDEN LAUREL WREATH (Untaian Padi / Daun Emas Kiri & Kanan)
            // ---------------------------------------------------------
            val wreathRadius = coreR * 0.72f
            val leafCount = 5
            for (i in 0 until leafCount) {
                // Left branch
                val leftAngle = (120f + i * 16f) * (Math.PI / 180f).toFloat()
                val lx = center.x + cos(leftAngle) * wreathRadius
                val ly = center.y + sin(leftAngle) * wreathRadius
                drawCircle(
                    color = Color(0xFFFBBF24),
                    radius = (coreR * 0.09f).coerceIn(1f, 3.5f),
                    center = Offset(lx, ly)
                )

                // Right branch
                val rightAngle = (60f - i * 16f) * (Math.PI / 180f).toFloat()
                val rx = center.x + cos(rightAngle) * wreathRadius
                val ry = center.y + sin(rightAngle) * wreathRadius
                drawCircle(
                    color = Color(0xFFFBBF24),
                    radius = (coreR * 0.09f).coerceIn(1f, 3.5f),
                    center = Offset(rx, ry)
                )
            }

            // ---------------------------------------------------------
            // 6. CENTER ACADEMIC SYMBOL: TOGA CAP & OPEN BOOK (Toga & Buku)
            // ---------------------------------------------------------
            val iconScale = coreR * 0.85f

            // A. Golden Graduation Toga Cap (Top of Center)
            val togaCenterY = center.y - coreR * 0.22f
            val togaW = iconScale * 0.72f
            val togaH = iconScale * 0.36f

            // Diamond Cap Top
            val capDiamond = Path().apply {
                moveTo(center.x, togaCenterY - togaH * 0.5f)
                lineTo(center.x + togaW * 0.5f, togaCenterY)
                lineTo(center.x, togaCenterY + togaH * 0.5f)
                lineTo(center.x - togaW * 0.5f, togaCenterY)
                close()
            }
            drawPath(
                path = capDiamond,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFEF08A), Color(0xFFF59E0B), Color(0xFFD97706)),
                    startY = togaCenterY - togaH * 0.5f,
                    endY = togaCenterY + togaH * 0.5f
                )
            )
            // Cap outline
            drawPath(
                path = capDiamond,
                color = Color(0xFF78350F),
                style = Stroke(width = (width * 0.015f).coerceAtLeast(1f))
            )

            // Skullcap Base
            val capBase = Path().apply {
                moveTo(center.x - togaW * 0.24f, togaCenterY + togaH * 0.25f)
                quadraticBezierTo(center.x, togaCenterY + togaH * 0.85f, center.x + togaW * 0.24f, togaCenterY + togaH * 0.25f)
                close()
            }
            drawPath(
                path = capBase,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFF59E0B), Color(0xFFB45309)),
                    startY = togaCenterY,
                    endY = togaCenterY + togaH
                )
            )

            // Yellow Tassel hanging on left
            drawLine(
                color = Color(0xFFFEF08A),
                start = Offset(center.x, togaCenterY),
                end = Offset(center.x - togaW * 0.45f, togaCenterY + togaH * 0.7f),
                strokeWidth = (width * 0.02f).coerceAtLeast(1.2f),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = Color(0xFFFEF08A),
                radius = (coreR * 0.07f).coerceIn(1f, 2.8f),
                center = Offset(center.x - togaW * 0.45f, togaCenterY + togaH * 0.7f)
            )

            // B. Golden Open Book (Buku Terbuka di Bawah Toga)
            val bookTopY = togaCenterY + togaH * 0.48f
            val bookW = iconScale * 0.68f
            val bookH = iconScale * 0.32f

            // Left Page
            val leftPage = Path().apply {
                moveTo(center.x, bookTopY + 2f)
                cubicTo(
                    center.x - bookW * 0.25f, bookTopY - 2f,
                    center.x - bookW * 0.45f, bookTopY + 2f,
                    center.x - bookW * 0.5f, bookTopY + 4f
                )
                lineTo(center.x - bookW * 0.5f, bookTopY + bookH)
                cubicTo(
                    center.x - bookW * 0.45f, bookTopY + bookH - 2f,
                    center.x - bookW * 0.25f, bookTopY + bookH - 4f,
                    center.x, bookTopY + bookH - 1f
                )
                close()
            }
            drawPath(
                path = leftPage,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFEF08A), Color(0xFFFFFFFF)),
                    startX = center.x - bookW * 0.5f,
                    endX = center.x
                )
            )
            drawPath(
                path = leftPage,
                color = Color(0xFFB45309),
                style = Stroke(width = (width * 0.012f).coerceAtLeast(0.8f))
            )

            // Right Page
            val rightPage = Path().apply {
                moveTo(center.x, bookTopY + 2f)
                cubicTo(
                    center.x + bookW * 0.25f, bookTopY - 2f,
                    center.x + bookW * 0.45f, bookTopY + 2f,
                    center.x + bookW * 0.5f, bookTopY + 4f
                )
                lineTo(center.x + bookW * 0.5f, bookTopY + bookH)
                cubicTo(
                    center.x + bookW * 0.45f, bookTopY + bookH - 2f,
                    center.x + bookW * 0.25f, bookTopY + bookH - 4f,
                    center.x, bookTopY + bookH - 1f
                )
                close()
            }
            drawPath(
                path = rightPage,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFFEF08A)),
                    startX = center.x,
                    endX = center.x + bookW * 0.5f
                )
            )
            drawPath(
                path = rightPage,
                color = Color(0xFFB45309),
                style = Stroke(width = (width * 0.012f).coerceAtLeast(0.8f))
            )

            // Book Spine Line
            drawLine(
                color = Color(0xFF78350F),
                start = Offset(center.x, bookTopY),
                end = Offset(center.x, bookTopY + bookH),
                strokeWidth = (width * 0.016f).coerceAtLeast(1.2f)
            )

            // Page text lines simulation (garis tulisan pada buku)
            if (size >= 48.dp) {
                for (line in 1..2) {
                    val ly = bookTopY + bookH * (0.35f + line * 0.22f)
                    // Left page line
                    drawLine(
                        color = Color(0xFF92400E).copy(alpha = 0.6f),
                        start = Offset(center.x - bookW * 0.42f, ly),
                        end = Offset(center.x - bookW * 0.12f, ly),
                        strokeWidth = 1f
                    )
                    // Right page line
                    drawLine(
                        color = Color(0xFF92400E).copy(alpha = 0.6f),
                        start = Offset(center.x + bookW * 0.12f, ly),
                        end = Offset(center.x + bookW * 0.42f, ly),
                        strokeWidth = 1f
                    )
                }
            }

            // C. 3 Mini Gold Stars under the book / inside badge
            val starCount = 3
            val starY = bookTopY + bookH + (coreR * 0.14f)
            for (s in 0 until starCount) {
                val sx = center.x + (s - 1) * (coreR * 0.28f)
                val starR = if (s == 1) coreR * 0.09f else coreR * 0.07f
                drawCircle(
                    color = Color(0xFFFEF08A),
                    radius = starR.coerceIn(1f, 3.5f),
                    center = Offset(sx, starY)
                )
            }
        }
    }
}

/**
 * Centered Header Branding Badge with Title "Gallery Kelas 4" and Logo "SDN 4 PUTRAJAWA"
 */
@Composable
fun SchoolHeaderCrestBanner(
    modifier: Modifier = Modifier,
    onLogoClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = PolishHeaderSurface,
        shadowElevation = 3.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Emblem Row (Centered Title and School Name)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // School Logo Left Accent (Enlarged & Circular Arched Text)
                Sdn4SchoolLogo(
                    size = 54.dp,
                    showGlow = true,
                    modifier = Modifier.padding(end = 12.dp)
                )

                // Centered Typography
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // School Pill Tag
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryPurple.copy(alpha = 0.12f),
                        modifier = Modifier.padding(bottom = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "SDN 4 PUTRAJAWA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryPurple,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Main Centered Title
                    Text(
                        text = "Fourbook SDN 4 Putrajawa",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PolishTextPrimary,
                        letterSpacing = (-0.2).sp
                    )
                }

                // Balanced Right Crest Badge
                Surface(
                    shape = CircleShape,
                    color = PrimaryContainerPurple,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "4",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = OnPrimaryContainerPurple
                        )
                    }
                }
            }
        }
    }
}
