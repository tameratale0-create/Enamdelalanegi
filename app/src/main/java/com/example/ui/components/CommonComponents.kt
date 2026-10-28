package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EscrowStatus
import com.example.model.ListingCategory
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.model.VerificationStatus
import com.example.ui.theme.CbePurple
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.TelebirrBlue
import com.example.ui.theme.TrustBlue

@Composable
fun DelalaTopAppBar(
    isOnline: Boolean,
    isAmharic: Boolean,
    userProfile: UserProfile,
    unreadNotifsCount: Int = 0,
    onToggleOnline: () -> Unit,
    onToggleLanguage: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = EmeraldDark,
        tonalElevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Name & Tagline
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("brand_header")
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(GoldSecondary, EmeraldPrimary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Delala Negn Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (isAmharic) "ደላላ ነኝ" else "Delala Negn",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (isAmharic) "ዘመናዊ የሽያጭና ኪራይ መድረክ" else "Smart Broker & Escrow",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }

                // Action Controls: Online/Offline toggle, Language, Notifications, Profile
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Online / Offline toggle pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isOnline) Color(0xFF14532D) else Color(0xFF7F1D1D))
                            .clickable { onToggleOnline() }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("online_offline_toggle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                                contentDescription = if (isOnline) "Online" else "Offline",
                                tint = if (isOnline) Color(0xFF86EFAC) else Color(0xFFFCA5A5),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isOnline) {
                                    if (isAmharic) "ቀጥታ" else "Online"
                                } else {
                                    if (isAmharic) "ኦፍላይን" else "Offline"
                                },
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Notification Bell Button
                    NotificationBellButton(
                        unreadCount = unreadNotifsCount,
                        onClick = onNotificationClick
                    )

                    // Language toggle pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable { onToggleLanguage() }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("lang_toggle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isAmharic) "አማ" else "EN",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // User Profile chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(GoldSecondary)
                            .clickable { onProfileClick() }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("profile_badge"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = userProfile.role.amharicName.take(5),
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfflineBanner(
    isAmharic: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFFEF3C7),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                tint = Color(0xFFB45309),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isAmharic)
                    "የከመስመር ውጭ (Offline) ሁነታ ነቅቷል፡ ዳታቤዙ በስልክዎ ላይ ስለተሸጎጠ ያለ ኢንተርኔት ንብረቶችን መፈለግና ረቂቅ መለጠፍ ይችላሉ።"
                else
                    "Offline Mode active: All listings and drafts are cached locally and work without internet.",
                color = Color(0xFF78350F),
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun VerificationBadge(
    status: VerificationStatus,
    isAmharic: Boolean,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status) {
        VerificationStatus.VERIFIED_TITLE_DEED -> Triple(
            Color(0xFFDCFCE7),
            Color(0xFF166534),
            Icons.Default.VerifiedUser
        )
        VerificationStatus.VERIFIED_LIBRE -> Triple(
            Color(0xFFE0F2FE),
            Color(0xFF0369A1),
            Icons.Default.CheckCircle
        )
        VerificationStatus.PENDING_REVIEW -> Triple(
            Color(0xFFFEF3C7),
            Color(0xFFB45309),
            Icons.Default.HourglassEmpty
        )
        VerificationStatus.UNVERIFIED -> Triple(
            Color(0xFFF1F5F9),
            Color(0xFF64748B),
            Icons.Default.Security
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isAmharic) status.amharicName else status.englishName,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun CategoryIcon(
    category: ListingCategory,
    modifier: Modifier = Modifier
) {
    val icon = when (category) {
        ListingCategory.ALL -> Icons.Default.LocationCity
        ListingCategory.HOUSE -> Icons.Default.Apartment
        ListingCategory.VEHICLE -> Icons.Default.DirectionsCar
        ListingCategory.COMMERCIAL -> Icons.Default.Storefront
        ListingCategory.LAND -> Icons.Default.Landscape
        ListingCategory.MACHINERY -> Icons.Default.Build
    }
    Icon(
        imageVector = icon,
        contentDescription = category.englishName,
        modifier = modifier
    )
}

@Composable
fun VoiceWaveformVisualizer(
    amplitudes: List<Float>,
    progress: Float = 0f,
    isPlaying: Boolean = false,
    color: Color = EmeraldPrimary,
    modifier: Modifier = Modifier
) {
    val displayAmps = if (amplitudes.isEmpty()) {
        listOf(0.3f, 0.7f, 0.5f, 0.9f, 0.6f, 0.4f, 0.8f, 0.6f, 0.5f, 0.85f, 0.3f)
    } else amplitudes

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        modifier = modifier.height(28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        displayAmps.forEachIndexed { index, amp ->
            val fraction = (index.toFloat() / displayAmps.size.coerceAtLeast(1))
            val isPlayed = fraction <= progress
            val barHeight = (amp.coerceIn(0.15f, 1.0f) * 26).dp
            val barColor = if (isPlayed) GoldSecondary else if (isPlaying) color.copy(alpha = pulseAlpha) else color

            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
fun EscrowProgressTracker(
    status: EscrowStatus,
    isAmharic: Boolean,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        Pair("1. ተቀማጭ", "1. Deposit"),
        Pair("2. በአደራ", "2. Escrowed"),
        Pair("3. ፍተሻ", "3. Inspected"),
        Pair("4. ተለቋል", "4. Released")
    )

    val currentStep = status.stepIndex

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = if (isAmharic) "የአደራ ክፍያ ሁኔታ (Escrow Status)" else "Escrow Progress",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, pair ->
                    val isCompleted = index <= currentStep
                    val isCurrent = index == currentStep

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCurrent) GoldSecondary
                                    else if (isCompleted) EmeraldPrimary
                                    else Color.LightGray
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted && !isCurrent) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isAmharic) pair.first else pair.second,
                            fontSize = 9.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (index < steps.size - 1) {
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .weight(0.5f)
                                .background(if (index < currentStep) EmeraldPrimary else Color.LightGray)
                        )
                    }
                }
            }
        }
    }
}
