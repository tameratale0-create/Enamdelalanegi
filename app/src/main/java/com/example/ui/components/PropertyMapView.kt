package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ListingCategory
import com.example.model.ListingItem
import com.example.model.ListingType
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldSecondary
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

// Base coordinates for Addis Ababa & central Ethiopia bounding box
private const val MIN_LAT = 8.7000
private const val MAX_LAT = 9.0700
private const val MIN_LNG = 38.6800
private const val MAX_LNG = 39.0200

@Composable
fun PropertyMapView(
    listings: List<ListingItem>,
    isAmharic: Boolean,
    onListingClick: (ListingItem) -> Unit,
    onEscrowClick: (ListingItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Map Pan and Zoom states
    var scale by remember { mutableFloatStateOf(1.15f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var selectedListing by remember { mutableStateOf<ListingItem?>(null) }
    var selectedSubCityFilter by remember { mutableStateOf("ሁሉም") }

    val subCities = listOf(
        Pair("ሁሉም", Pair(9.0100, 38.7600)),
        Pair("ቦሌ", Pair(8.9950, 38.7880)),
        Pair("ሲኤምሲ", Pair(9.0230, 38.8350)),
        Pair("ሳርቤት", Pair(9.0010, 38.7480)),
        Pair("መርካቶ", Pair(9.0300, 38.7380)),
        Pair("ቢሾፍቱ", Pair(8.7520, 38.9810))
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(0.dp))
            .background(Color(0xFFE2E8F0))
            .testTag("property_map_view")
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        // Helper to convert geographic lat/lng into local canvas coordinates
        fun geoToPixel(lat: Double, lng: Double): Offset {
            val normX = ((lng - MIN_LNG) / (MAX_LNG - MIN_LNG)).toFloat()
            // Invert latitude because canvas Y goes downwards
            val normY = (1f - ((lat - MIN_LAT) / (MAX_LAT - MIN_LAT))).toFloat()

            val baseX = normX * widthPx
            val baseY = normY * heightPx

            val centerX = widthPx / 2f
            val centerY = heightPx / 2f

            val scaledX = (baseX - centerX) * scale + centerX + offsetX
            val scaledY = (baseY - centerY) * scale + centerY + offsetY

            return Offset(scaledX, scaledY)
        }

        // Detect Pan gestures
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
        ) {
            // Interactive Map Canvas rendering roads, districts, rivers and terrain
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawMapBackground(
                    scale = scale,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    width = size.width,
                    height = size.height
                )
            }

            // Render Property Markers on the Map
            listings.forEach { item ->
                val pos = geoToPixel(item.latitude, item.longitude)
                val isSelected = selectedListing?.id == item.id

                // Only render if marker is within reasonable viewport
                if (pos.x in -100f..(widthPx + 100f) && pos.y in -100f..(heightPx + 100f)) {
                    PropertyMapMarker(
                        item = item,
                        isSelected = isSelected,
                        isAmharic = isAmharic,
                        modifier = Modifier
                            .offset { IntOffset(pos.x.roundToInt() - 45, pos.y.roundToInt() - 40) }
                            .clickable {
                                selectedListing = item
                            }
                    )
                }
            }
        }

        // Top Controls: Subcity Jump Bar
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 10.dp, start = 12.dp, end = 12.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 6.dp,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(start = 2.dp)
                    )

                    subCities.forEach { (name, coords) ->
                        val isSelected = selectedSubCityFilter == name
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    selectedSubCityFilter = name
                                    // Smoothly re-center map
                                    val (targetLat, targetLng) = coords
                                    val normX = ((targetLng - MIN_LNG) / (MAX_LNG - MIN_LNG)).toFloat()
                                    val normY = (1f - ((targetLat - MIN_LAT) / (MAX_LAT - MIN_LAT))).toFloat()
                                    val targetBaseX = normX * widthPx
                                    val targetBaseY = normY * heightPx
                                    offsetX = (widthPx / 2f - targetBaseX)
                                    offsetY = (heightPx / 2f - targetBaseY)
                                    scale = 1.35f
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Zoom & Re-center FAB Controls on the right
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Zoom In (+)
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shape = CircleShape,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { scale = (scale * 1.25f).coerceAtMost(3.0f) }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom In", tint = EmeraldPrimary)
                }
            }

            // Zoom Out (-)
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shape = CircleShape,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { scale = (scale / 1.25f).coerceAtLeast(0.75f) }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom Out", tint = EmeraldPrimary)
                }
            }

            // Center on Addis Ababa
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shape = CircleShape,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        scale = 1.15f
                        offsetX = 0f
                        offsetY = 0f
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.LocationSearching, contentDescription = "Reset Center", tint = GoldSecondary)
                }
            }
        }

        // Floating Bottom Card for Selected Property
        AnimatedVisibility(
            visible = selectedListing != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            selectedListing?.let { item ->
                SelectedPropertyFloatingCard(
                    item = item,
                    isAmharic = isAmharic,
                    onClose = { selectedListing = null },
                    onDetails = { onListingClick(item) },
                    onEscrow = { onEscrowClick(item) },
                    onOpenGoogleMaps = {
                        // Open external Google Maps navigation intent
                        val gmmIntentUri = Uri.parse("geo:${item.latitude},${item.longitude}?q=${item.latitude},${item.longitude}(${Uri.encode(item.title)})")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            // Fallback to standard web/browser maps
                            val webIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.google.com/maps/search/?api=1&query=${item.latitude},${item.longitude}")
                            )
                            context.startActivity(webIntent)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PropertyMapMarker(
    item: ListingItem,
    isSelected: Boolean,
    isAmharic: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = when (item.category) {
        ListingCategory.HOUSE -> if (item.type == ListingType.RENT) GoldSecondary else EmeraldPrimary
        ListingCategory.VEHICLE -> Color(0xFF2563EB)
        ListingCategory.COMMERCIAL -> Color(0xFF9333EA)
        ListingCategory.LAND -> Color(0xFF059669)
        else -> EmeraldPrimary
    }

    val priceShort = remember(item.priceBirr) {
        val birr = item.priceBirr
        when {
            birr >= 1000000 -> String.format(Locale.US, "%.1fM", birr / 1000000.0)
            birr >= 1000 -> "${birr / 1000}K"
            else -> "$birr"
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Price Pill
        Surface(
            color = if (isSelected) Color(0xFF1E293B) else bgColor,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = if (isSelected) 8.dp else 4.dp,
            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color.White) else null
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = priceShort,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                if (item.type == ListingType.RENT) {
                    Text(
                        text = "/ወር",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 9.sp
                    )
                }
            }
        }

        // Pointer triangle
        Canvas(modifier = Modifier.size(10.dp, 6.dp)) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2f, size.height)
                close()
            }
            drawPath(path, color = if (isSelected) Color(0xFF1E293B) else bgColor)
        }
    }
}

@Composable
fun SelectedPropertyFloatingCard(
    item: ListingItem,
    isAmharic: Boolean,
    onClose: () -> Unit,
    onDetails: () -> Unit,
    onEscrow: () -> Unit,
    onOpenGoogleMaps: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                VerificationBadge(status = item.verificationStatus, isAmharic = isAmharic)
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(item.priceBirr)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$formattedPrice ብር",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    color = EmeraldPrimary
                )
                item.pricePeriod?.let {
                    Text(
                        text = " /$it",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 1.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "• ${item.location}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Open in Google Maps navigation button
                Button(
                    onClick = onOpenGoogleMaps,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Icon(imageVector = Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Google Maps", fontSize = 11.sp)
                }

                Button(
                    onClick = onEscrow,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldSecondary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isAmharic) "አደራ" else "Escrow", fontSize = 11.sp)
                }

                Button(
                    onClick = onDetails,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Text(text = if (isAmharic) "ሙሉ መረጃ" else "Details", fontSize = 11.sp)
                }
            }
        }
    }
}

// Draw vector map background styling for Addis Ababa geography
private fun DrawScope.drawMapBackground(
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    width: Float,
    height: Float
) {
    // 1. Water color / Land base
    drawRect(color = Color(0xFFF1F5F9))

    // 2. Green park / mountain zones (Entoto hills at the north, Kuriftu lake at southeast)
    val parkBrush = Brush.radialGradient(
        listOf(Color(0xFFE2F0D9), Color(0xFFF1F5F9)),
        center = Offset(width * 0.5f + offsetX, height * 0.1f + offsetY),
        radius = 350f * scale
    )
    drawCircle(
        brush = parkBrush,
        radius = 300f * scale,
        center = Offset(width * 0.5f + offsetX, height * 0.1f + offsetY)
    )

    // Kuriftu Lake in Bishoftu
    drawCircle(
        color = Color(0xFFBAE6FD),
        radius = 65f * scale,
        center = Offset(width * 0.85f + offsetX, height * 0.85f + offsetY)
    )

    // 3. Major highways / arterial grid lines
    // Ring Road (Circle)
    val ringCenter = Offset(width * 0.5f + offsetX, height * 0.45f + offsetY)
    drawCircle(
        color = Color(0xFFCBD5E1),
        radius = 180f * scale,
        center = ringCenter,
        style = Stroke(width = 6f * scale)
    )

    // Bole Road (Meskel Square -> Bole Airport)
    drawLine(
        color = Color(0xFFFDE68A),
        start = Offset(width * 0.45f + offsetX, height * 0.42f + offsetY),
        end = Offset(width * 0.65f + offsetX, height * 0.60f + offsetY),
        strokeWidth = 7f * scale,
        cap = StrokeCap.Round
    )

    // CMC Expressway (Towards Ayat)
    drawLine(
        color = Color(0xFFFED7AA),
        start = Offset(width * 0.5f + offsetX, height * 0.4f + offsetY),
        end = Offset(width * 0.85f + offsetX, height * 0.35f + offsetY),
        strokeWidth = 6f * scale,
        cap = StrokeCap.Round
    )

    // Debre Zeit Road (Towards Bishoftu)
    drawLine(
        color = Color(0xFFE2E8F0),
        start = Offset(width * 0.55f + offsetX, height * 0.55f + offsetY),
        end = Offset(width * 0.85f + offsetX, height * 0.85f + offsetY),
        strokeWidth = 6f * scale,
        cap = StrokeCap.Round
    )

    // Grid secondary streets
    val step = 60f * scale
    var x = offsetX % step
    while (x < width) {
        drawLine(
            color = Color(0xFFE2E8F0).copy(alpha = 0.5f),
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 1.5f
        )
        x += step
    }

    var y = offsetY % step
    while (y < height) {
        drawLine(
            color = Color(0xFFE2E8F0).copy(alpha = 0.5f),
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1.5f
        )
        y += step
    }
}
