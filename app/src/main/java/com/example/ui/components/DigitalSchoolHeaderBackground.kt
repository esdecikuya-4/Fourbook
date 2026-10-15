package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R

/**
 * Realistic 3D Modern Architectural Header Background for SDN 4 Putrajawa (Fourbook).
 *
 * Renders the realistic 3D architectural digital visualization of SDN 4 Putrajawa smart campus:
 * - Ultra-crisp 3D rendering with modern glass curtain walls, warm illuminated classrooms
 * - Cantilevered eaves with cyan architectural LED light strips
 * - Indonesian Merah Putih national flag waving proudly
 * - Majestic Garut volcanic mountain backdrop under golden dawn sky
 * - Paved courtyard with perspective lines and wet specular reflections
 * - Subtle live animated shimmer and atmospheric lighting overlays
 */
@Composable
fun DigitalSchoolHeaderBackground(
    modifier: Modifier = Modifier,
    dimOverlayAlpha: Float = 0.0f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "modern_3d_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "light_pulse"
    )
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -0.4f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glass_shimmer"
    )

    Box(modifier = modifier) {
        // 1. High-Resolution Realistic 3D Modern Architectural Campus Header
        Image(
            painter = painterResource(id = R.drawable.img_sdn4_header_3d),
            contentDescription = "SDN 4 Putrajawa 3D Campus Header",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Animated Smart Architectural Ambient Overlay (Cyan LED pulse & glass reflection sweep)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Cyan & Amber architectural LED pulse highlight
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF38BDF8).copy(alpha = 0.12f * pulseAlpha),
                        Color(0xFF0284C7).copy(alpha = 0.05f * pulseAlpha),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.45f),
                    radius = w * 0.45f
                )
            )

            // Dynamic diagonal glass sheen sweep across modern facade
            val sheenX = w * shimmerOffset
            val sheenWidth = w * 0.22f
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.10f),
                        Color(0xFFBAE6FD).copy(alpha = 0.16f),
                        Color.Transparent
                    ),
                    start = Offset(sheenX - sheenWidth, 0f),
                    end = Offset(sheenX + sheenWidth, h)
                )
            )
        }

        // 3. Optional Dim Overlay for contrast adjustment
        if (dimOverlayAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = dimOverlayAlpha))
            )
        }
    }
}
