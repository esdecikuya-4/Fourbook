package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.*

/**
 * Custom School Header with Elementary School Building background,
 * Billboard reading "Selamat Datang Di SDN 4 PUTRAJAWA", and
 * the "fourbook" logo placed EXACTLY in the center.
 */
@Composable
fun Sdn4SchoolHeaderBanner(
    currentUser: UserEntity?,
    onAvatarClick: () -> Unit,
    onAdminMenuClick: () -> Unit = {},
    isSearchActive: Boolean = false,
    onSearchToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF0F172A),
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp)
        ) {
            // 1. Futuristic, Realistic Digital School Header Background (SDN 4 Putrajawa)
            DigitalSchoolHeaderBackground(
                modifier = Modifier.fillMaxSize(),
                dimOverlayAlpha = 0.05f
            )

            // Subtle top atmospheric gradient for status bar & top button clarity
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Bottom atmospheric gradient for seamless feed transition
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.45f)
                            )
                        )
                    )
            )

            // Top Bar with User Avatar (Left) and Actions (Search + Admin/Switch) (Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: User quick profile button
                if (currentUser != null) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.9f),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .size(38.dp)
                            .clickable { onAvatarClick() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            MemberAvatar(
                                fullName = currentUser.fullName,
                                avatarColor = currentUser.avatarColor,
                                avatarIcon = currentUser.avatarIcon,
                                customPhotoUri = currentUser.customPhotoUri,
                                size = 34.dp
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.size(38.dp))
                }

                // Right Actions: Search Icon Button + Admin/Switch Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Clean Search Toggle Button (no giant text)
                    Surface(
                        shape = CircleShape,
                        color = if (isSearchActive) Color.White else Color.White.copy(alpha = 0.25f),
                        modifier = Modifier
                            .size(34.dp)
                            .clickable { onSearchToggle() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Cari",
                                tint = if (isSearchActive) FbBluePrimary else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Admin Portal Button (or Switch Button)
                    if (currentUser?.role == UserRole.WALI_KELAS.name) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF059669),
                            shadowElevation = 3.dp,
                            modifier = Modifier.clickable { onAdminMenuClick() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Menu Admin",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Menu Admin",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.25f),
                            modifier = Modifier
                                .size(34.dp)
                                .clickable { onAvatarClick() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Ganti Akun",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Center-Aligned Header: "Fourbook" with School Emblem and Online Community Subtitle
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 46.dp, bottom = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Official Circular SDN 4 Putrajawa Emblem Logo
                        Sdn4SchoolLogo(
                            size = 38.dp,
                            showGlow = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // "Fourbook"
                        Text(
                            text = "Fourbook",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp,
                            style = androidx.compose.ui.text.TextStyle(
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black.copy(alpha = 0.85f),
                                    offset = Offset(2f, 3f),
                                    blurRadius = 8f
                                )
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Subtitle: SDN 4 PUTRAJAWA ONLINE COMMUNITY
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.55f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFFDE047).copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "SDN 4 PUTRAJAWA ONLINE COMMUNITY",
                            color = Color(0xFFFDE047),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 2.5.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Draws a photorealistic-inspired architectural illustration of a classic Indonesian Elementary School (SD Negeri):
 * - Terracotta tiled hip roof (Genteng Kodok Merah)
 * - White and emerald green painted walls with selasar / corridor pillars
 * - Symmetrical classroom windows with transoms
 * - Wide paved ceremonial courtyard (Halaman Upacara) with boundary curbs & potted palms
 * - Indonesian Red and White Flag on stainless steel flagpole
 * - Front ornate iron school gate (Gerbang Besi SDN 4) with brick pillars
 */
private fun DrawScope.drawRealisticIndonesianSchool(size: Size) {
    val width = size.width
    val height = size.height

    // 1. Sky Gradient with Morning Sun Glow
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0F3D7C), // Deep blue zenith
                Color(0xFF2563EB), // Sky blue
                Color(0xFF60A5FA), // Light cerulean
                Color(0xFFBAE6FD)  // Horizon haze
            ),
            startY = 0f,
            endY = height * 0.58f
        ),
        size = Size(width, height * 0.58f)
    )

    // Sun with realistic soft halo
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFFBEB).copy(alpha = 0.9f),
                Color(0xFFFDE68A).copy(alpha = 0.5f),
                Color(0xFFFBBF24).copy(alpha = 0.15f),
                Color.Transparent
            ),
            center = Offset(width * 0.82f, height * 0.22f),
            radius = height * 0.35f
        ),
        center = Offset(width * 0.82f, height * 0.22f),
        radius = height * 0.35f
    )

    // Soft morning cumulus clouds
    drawCircle(color = Color.White.copy(alpha = 0.25f), radius = height * 0.14f, center = Offset(width * 0.22f, height * 0.20f))
    drawCircle(color = Color.White.copy(alpha = 0.30f), radius = height * 0.18f, center = Offset(width * 0.32f, height * 0.18f))
    drawCircle(color = Color.White.copy(alpha = 0.25f), radius = height * 0.13f, center = Offset(width * 0.42f, height * 0.21f))

    drawCircle(color = Color.White.copy(alpha = 0.20f), radius = height * 0.12f, center = Offset(width * 0.70f, height * 0.16f))
    drawCircle(color = Color.White.copy(alpha = 0.25f), radius = height * 0.15f, center = Offset(width * 0.77f, height * 0.14f))

    // 2. Distant Hills and Background Tropical Tree Canopy
    // Mountain silhouette
    val mountainPath = Path().apply {
        moveTo(0f, height * 0.45f)
        cubicTo(width * 0.25f, height * 0.36f, width * 0.45f, height * 0.42f, width * 0.65f, height * 0.34f)
        cubicTo(width * 0.8f, height * 0.30f, width * 0.92f, height * 0.38f, width, height * 0.40f)
        lineTo(width, height * 0.58f)
        lineTo(0f, height * 0.58f)
        close()
    }
    drawPath(
        path = mountainPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF1E3A8A).copy(alpha = 0.45f), Color(0xFF1E40AF).copy(alpha = 0.65f)),
            startY = height * 0.30f,
            endY = height * 0.58f
        )
    )

    // Background lush trees behind school building
    for (i in 0..12) {
        val treeCenterX = width * (i / 12f) + (i % 3) * (width * 0.02f)
        val treeCenterY = height * 0.46f + (if (i % 2 == 0) -4f else 3f)
        val treeR = height * (0.10f + (i % 4) * 0.02f)
        drawCircle(
            color = if (i % 2 == 0) Color(0xFF14532D) else Color(0xFF15803D),
            radius = treeR,
            center = Offset(treeCenterX, treeCenterY)
        )
    }

    // 3. School Main Building Complex (Indonesian SD Negeri Pavilion)
    val bldgBaseY = height * 0.64f
    val bldgHeight = height * 0.26f
    val bldgWidth = width * 0.88f
    val bldgLeft = (width - bldgWidth) / 2f

    // Main classroom walls (Warm cream/white)
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFEFCE8), Color(0xFFF1F5F9)),
            startY = bldgBaseY - bldgHeight,
            endY = bldgBaseY
        ),
        topLeft = Offset(bldgLeft, bldgBaseY - bldgHeight),
        size = Size(bldgWidth, bldgHeight)
    )

    // Lower wainscot wall (Characteristic Indonesian School Dark Green Dado: Bagian Bawah Dinding Hijau)
    drawRect(
        color = Color(0xFF166534),
        topLeft = Offset(bldgLeft, bldgBaseY - bldgHeight * 0.38f),
        size = Size(bldgWidth, bldgHeight * 0.38f)
    )

    // Red Terracotta Tile Corridor / Selasar Platform
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF9A3412), Color(0xFF7C2D12)),
            startY = bldgBaseY - 3f,
            endY = bldgBaseY + height * 0.04f
        ),
        topLeft = Offset(bldgLeft - width * 0.02f, bldgBaseY - 3f),
        size = Size(bldgWidth + width * 0.04f, height * 0.04f)
    )

    // Symmetrical Classroom Doors and Windows with Ventilation Louvers
    val roomCount = 8
    val winWidth = (bldgWidth / roomCount) * 0.58f
    val winHeight = bldgHeight * 0.42f
    for (i in 0 until roomCount) {
        val posX = bldgLeft + (bldgWidth / roomCount) * i + ((bldgWidth / roomCount) - winWidth) / 2f
        val posY = bldgBaseY - bldgHeight * 0.88f

        if (i == roomCount / 2) {
            // Main Entrance Door (Pintu Utama Kantor / Kelas)
            drawRect(
                color = Color(0xFF78350F),
                topLeft = Offset(posX - 4f, posY),
                size = Size(winWidth + 8f, bldgHeight * 0.88f)
            )
            // Door Panels
            drawRect(
                color = Color(0xFF92400E),
                topLeft = Offset(posX - 2f, posY + 2f),
                size = Size((winWidth + 4f) / 2f - 2f, bldgHeight * 0.84f)
            )
            drawRect(
                color = Color(0xFF92400E),
                topLeft = Offset(posX + (winWidth + 4f) / 2f + 1f, posY + 2f),
                size = Size((winWidth + 4f) / 2f - 2f, bldgHeight * 0.84f)
            )
        } else {
            // Classroom Glass Windows with divided glass panes
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
                    startY = posY,
                    endY = posY + winHeight
                ),
                topLeft = Offset(posX, posY),
                size = Size(winWidth, winHeight),
                cornerRadius = CornerRadius(2f, 2f)
            )
            // Window Frames & Mullions
            drawRoundRect(
                color = Color(0xFF0F172A),
                topLeft = Offset(posX, posY),
                size = Size(winWidth, winHeight),
                cornerRadius = CornerRadius(2f, 2f),
                style = Stroke(width = 1.5f)
            )
            // Glass Reflection & Divisors
            drawLine(
                color = Color(0xFF0F172A),
                start = Offset(posX + winWidth / 2f, posY),
                end = Offset(posX + winWidth / 2f, posY + winHeight),
                strokeWidth = 1f
            )
            // Top ventilation louver (kisi angin)
            drawRect(
                color = Color(0xFF166534),
                topLeft = Offset(posX, posY - height * 0.022f),
                size = Size(winWidth, height * 0.018f)
            )
        }

        // Structural Veranda Pillar (Tiang Selasar Hijau-Putih)
        val pillarX = bldgLeft + (bldgWidth / roomCount) * i
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0), Color(0xFFCBD5E1)),
                startX = pillarX - 2f,
                endX = pillarX + 4f
            ),
            topLeft = Offset(pillarX - 2f, bldgBaseY - bldgHeight),
            size = Size(6f, bldgHeight)
        )
        // Pillar green base
        drawRect(
            color = Color(0xFF15803D),
            topLeft = Offset(pillarX - 3f, bldgBaseY - bldgHeight * 0.38f),
            size = Size(8f, bldgHeight * 0.38f)
        )
    }

    // 4. Terracotta Hip Roof (Atap Genteng Khas Indonesia)
    val roofOverhang = width * 0.05f
    val roofPeakY = bldgBaseY - bldgHeight - height * 0.16f
    val roofEaveY = bldgBaseY - bldgHeight

    // Main Roof Trapeze / Hip
    val roofPath = Path().apply {
        moveTo(bldgLeft - roofOverhang, roofEaveY)
        lineTo(bldgLeft + width * 0.15f, roofPeakY)
        lineTo(bldgLeft + bldgWidth - width * 0.15f, roofPeakY)
        lineTo(bldgLeft + bldgWidth + roofOverhang, roofEaveY)
        close()
    }
    drawPath(
        path = roofPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFC2410C), // Bright clay terracotta
                Color(0xFF9A3412), // Deep burnt orange
                Color(0xFF7C2D12)  // Dark tile shadow
            ),
            startY = roofPeakY,
            endY = roofEaveY
        )
    )

    // Roof Shingle Horizontal Lines (Tekstur Barisan Genteng)
    for (step in 1..7) {
        val y = roofPeakY + (roofEaveY - roofPeakY) * (step / 7f)
        val xStart = bldgLeft - roofOverhang + (width * 0.15f + roofOverhang) * (1f - step / 7f)
        val xEnd = bldgLeft + bldgWidth + roofOverhang - (width * 0.15f + roofOverhang) * (1f - step / 7f)
        drawLine(
            color = Color(0xFF431407).copy(alpha = 0.5f),
            start = Offset(xStart, y),
            end = Offset(xEnd, y),
            strokeWidth = 1f
        )
    }

    // Center Pediment / Gable (Bagian Depan Tengah Gedung SD)
    val centerGableLeft = width * 0.40f
    val centerGableRight = width * 0.60f
    val centerGablePeakY = roofPeakY - height * 0.05f
    val centerGable = Path().apply {
        moveTo(centerGableLeft, roofEaveY)
        lineTo(width / 2f, centerGablePeakY)
        lineTo(centerGableRight, roofEaveY)
        close()
    }
    drawPath(
        path = centerGable,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFEA580C), Color(0xFF9A3412)),
            startY = centerGablePeakY,
            endY = roofEaveY
        )
    )
    // School Crest / Plang Logo SD di segitiga atap
    drawCircle(
        color = Color(0xFFFEF08A),
        radius = 8f,
        center = Offset(width / 2f, centerGablePeakY + height * 0.045f)
    )
    drawCircle(
        color = Color(0xFF1E3A8A),
        radius = 8f,
        center = Offset(width / 2f, centerGablePeakY + height * 0.045f),
        style = Stroke(width = 1.5f)
    )

    // 5. School Yard / Ceremonial Courtyard (Halaman Upacara Paving)
    val yardTopY = bldgBaseY + height * 0.035f
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF94A3B8), // Cement paving light
                Color(0xFF64748B), // Slate gray courtyard
                Color(0xFF475569)  // Foreground shadow
            ),
            startY = yardTopY,
            endY = height
        ),
        topLeft = Offset(0f, yardTopY),
        size = Size(width, height - yardTopY)
    )

    // Paving block grid lines in perspective
    for (i in 0..16) {
        val lineX = width * (i / 16f)
        drawLine(
            color = Color(0xFFCBD5E1).copy(alpha = 0.25f),
            start = Offset(lineX, yardTopY),
            end = Offset(lineX * 1.15f - width * 0.075f, height),
            strokeWidth = 0.8f
        )
    }
    for (j in 1..5) {
        val lineY = yardTopY + (height - yardTopY) * (j / 5f)
        drawLine(
            color = Color(0xFFCBD5E1).copy(alpha = 0.2f),
            start = Offset(0f, lineY),
            end = Offset(width, lineY),
            strokeWidth = 0.8f
        )
    }

    // White painted ceremony lines on the yard (Garis Lapangan Upacara)
    drawRect(
        color = Color.White.copy(alpha = 0.45f),
        topLeft = Offset(width * 0.28f, yardTopY + height * 0.08f),
        size = Size(width * 0.44f, height * 0.12f),
        style = Stroke(width = 1.5f)
    )

    // Garden curbs & potted plants along the veranda (Pot Bunga Pucuk Merah & Sansevieria)
    for (k in 1..7) {
        val potX = bldgLeft + (bldgWidth / 8f) * k
        val potY = yardTopY + 2f
        // Pot
        drawRoundRect(
            color = Color(0xFF78350F),
            topLeft = Offset(potX - 6f, potY),
            size = Size(12f, 8f),
            cornerRadius = CornerRadius(1f, 1f)
        )
        // Foliage & Red Top (Pucuk Merah)
        drawCircle(color = Color(0xFF15803D), radius = 6f, center = Offset(potX, potY - 3f))
        drawCircle(color = Color(0xFFDC2626), radius = 3.5f, center = Offset(potX, potY - 6f))
    }

    // 6. Stainless Steel Flagpole & Indonesian Flag (Tiang Bendera Merah Putih)
    val poleX = width * 0.16f
    val poleBaseY = height * 0.84f
    val poleTopY = height * 0.22f

    // Flagpole Concrete Base (Pondasi Tiang Bertingkat)
    drawRect(color = Color(0xFFE2E8F0), topLeft = Offset(poleX - 10f, poleBaseY - 4f), size = Size(20f, 6f))
    drawRect(color = Color(0xFF94A3B8), topLeft = Offset(poleX - 6f, poleBaseY - 9f), size = Size(12f, 5f))

    // Metallic Stainless Steel Pole
    drawLine(
        brush = Brush.horizontalGradient(
            colors = listOf(Color(0xFFF1F5F9), Color(0xFFCBD5E1), Color(0xFF64748B)),
            startX = poleX - 2f,
            endX = poleX + 2f
        ),
        start = Offset(poleX, poleBaseY - 9f),
        end = Offset(poleX, poleTopY),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )
    // Gold finial sphere on top
    drawCircle(color = Color(0xFFFBBF24), radius = 3.5f, center = Offset(poleX, poleTopY))

    // Fluttering Indonesian Red and White Flag (Bendera Merah Putih Berkibar)
    val flagW = width * 0.085f
    val flagH = height * 0.075f
    val flagTop = poleTopY + 3f

    // Red Half with wavy shape
    val flagRed = Path().apply {
        moveTo(poleX, flagTop)
        quadraticBezierTo(poleX + flagW * 0.5f, flagTop - 3f, poleX + flagW, flagTop + 1f)
        lineTo(poleX + flagW, flagTop + flagH / 2f + 1f)
        quadraticBezierTo(poleX + flagW * 0.5f, flagTop + flagH / 2f - 3f, poleX, flagTop + flagH / 2f)
        close()
    }
    drawPath(path = flagRed, color = Color(0xFFDC2626))

    // White Half with wavy shape
    val flagWhite = Path().apply {
        moveTo(poleX, flagTop + flagH / 2f)
        quadraticBezierTo(poleX + flagW * 0.5f, flagTop + flagH / 2f - 3f, poleX + flagW, flagTop + flagH / 2f + 1f)
        lineTo(poleX + flagW, flagTop + flagH + 1f)
        quadraticBezierTo(poleX + flagW * 0.5f, flagTop + flagH - 3f, poleX, flagTop + flagH)
        close()
    }
    drawPath(path = flagWhite, color = Color.White)
    // Flag outline shadow
    drawPath(path = flagRed, color = Color.Black.copy(alpha = 0.2f), style = Stroke(width = 0.8f))
    drawPath(path = flagWhite, color = Color.Black.copy(alpha = 0.2f), style = Stroke(width = 0.8f))

    // 7. Front School Gate & Fence Pillars (Gerbang & Pagar SDN 4 Putrajawa)
    val gatePillarW = width * 0.045f
    val gatePillarH = height * 0.25f
    val gateBottomY = height

    // Left Gate Pillar (Pilar Gerbang Kiri Batu Alam)
    val leftPillarX = width * 0.04f
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(Color(0xFF334155), Color(0xFF64748B), Color(0xFF1E293B)),
            startX = leftPillarX,
            endX = leftPillarX + gatePillarW
        ),
        topLeft = Offset(leftPillarX, gateBottomY - gatePillarH),
        size = Size(gatePillarW, gatePillarH)
    )
    // Pillar Cap Pyramid (Topi Pilar)
    val leftCap = Path().apply {
        moveTo(leftPillarX - 2f, gateBottomY - gatePillarH)
        lineTo(leftPillarX + gatePillarW / 2f, gateBottomY - gatePillarH - 6f)
        lineTo(leftPillarX + gatePillarW + 2f, gateBottomY - gatePillarH)
        close()
    }
    drawPath(path = leftCap, color = Color(0xFF0F172A))

    // Right Gate Pillar (Pilar Gerbang Kanan)
    val rightPillarX = width * 0.915f
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(Color(0xFF334155), Color(0xFF64748B), Color(0xFF1E293B)),
            startX = rightPillarX,
            endX = rightPillarX + gatePillarW
        ),
        topLeft = Offset(rightPillarX, gateBottomY - gatePillarH),
        size = Size(gatePillarW, gatePillarH)
    )
    val rightCap = Path().apply {
        moveTo(rightPillarX - 2f, gateBottomY - gatePillarH)
        lineTo(rightPillarX + gatePillarW / 2f, gateBottomY - gatePillarH - 6f)
        lineTo(rightPillarX + gatePillarW + 2f, gateBottomY - gatePillarH)
        close()
    }
    drawPath(path = rightCap, color = Color(0xFF0F172A))

    // Wrought Iron Fence Bars (Pagar Teralis Besi Hijau & Hitam dengan Ujung Tombak)
    val ironGateH = gatePillarH * 0.72f
    val ironGateTop = gateBottomY - ironGateH
    // Left Wing Fence
    for (barX in (leftPillarX + gatePillarW).toInt()..(leftPillarX + width * 0.12f).toInt() step 12) {
        drawLine(
            color = Color(0xFF15803D),
            start = Offset(barX.toFloat(), gateBottomY),
            end = Offset(barX.toFloat(), ironGateTop),
            strokeWidth = 2f
        )
        // Spearhead finial
        drawLine(
            color = Color(0xFFFBBF24),
            start = Offset(barX.toFloat(), ironGateTop),
            end = Offset(barX.toFloat(), ironGateTop - 4f),
            strokeWidth = 2.5f
        )
    }
    // Right Wing Fence
    for (barX in (rightPillarX - width * 0.12f).toInt()..rightPillarX.toInt() step 12) {
        drawLine(
            color = Color(0xFF15803D),
            start = Offset(barX.toFloat(), gateBottomY),
            end = Offset(barX.toFloat(), ironGateTop),
            strokeWidth = 2f
        )
        drawLine(
            color = Color(0xFFFBBF24),
            start = Offset(barX.toFloat(), ironGateTop),
            end = Offset(barX.toFloat(), ironGateTop - 4f),
            strokeWidth = 2.5f
        )
    }
    // Horizontal gate support rails
    drawLine(color = Color(0xFF0F172A), start = Offset(0f, ironGateTop + 4f), end = Offset(width, ironGateTop + 4f), strokeWidth = 2f)
    drawLine(color = Color(0xFF0F172A), start = Offset(0f, gateBottomY - 8f), end = Offset(width, gateBottomY - 8f), strokeWidth = 2f)
}

