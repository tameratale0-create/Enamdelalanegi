package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.ListingCategory
import com.example.model.ListingItem
import com.example.model.ListingType
import com.example.model.VerificationStatus
import com.example.ui.AppTab
import com.example.ui.DelalaViewModel
import com.example.ui.components.CategoryIcon
import com.example.ui.components.PropertyMapView
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.CbePurple
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.TelebirrBlue
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    viewModel: DelalaViewModel,
    modifier: Modifier = Modifier
) {
    val listings by viewModel.filteredListings.collectAsState()
    val isAmharic by viewModel.isAmharic.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedSubCity by viewModel.selectedSubCity.collectAsState()
    val onlyVerified by viewModel.onlyVerified.collectAsState()
    val selectedListing by viewModel.selectedListing.collectAsState()

    var showEscrowDialogForListing by remember { mutableStateOf<ListingItem?>(null) }
    var isMapViewMode by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val subCities = listOf("ሁሉም", "ቦሌ", "ቂርቆስ", "የካ", "አዲስ ከተማ", "ቢሾፍቱ")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search & Filter header
        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                // Search TextField & View Mode Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_field"),
                        placeholder = {
                            Text(
                                text = if (isAmharic) "ቦታ፣ ቤት፣ መኪና..." else "Search properties...",
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = EmeraldPrimary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true
                    )

                    // View Mode Switcher Pill (List vs Map)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (!isMapViewMode) EmeraldPrimary else Color.Transparent)
                                    .clickable { isMapViewMode = false }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("toggle_list_view")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = "List",
                                    tint = if (!isMapViewMode) Color.White else Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isMapViewMode) GoldSecondary else Color.Transparent)
                                    .clickable { isMapViewMode = true }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("toggle_map_view")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Map,
                                        contentDescription = "Map",
                                        tint = if (isMapViewMode) Color.White else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (isAmharic) "ካርታ" else "Map",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isMapViewMode) Color.White else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ListingCategory.values()) { category ->
                        val isSelected = category == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(category) },
                            label = {
                                Text(
                                    text = if (isAmharic) category.amharicName else category.englishName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                CategoryIcon(category = category, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Secondary filter bar: Type (Rent/Sale) & Subcity
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Type selector
                    ListingType.values().forEach { type ->
                        val isSelected = type == selectedType
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) GoldSecondary else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { viewModel.selectType(type) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (isAmharic) type.amharicName else type.englishName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Verified only toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (onlyVerified) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { viewModel.setOnlyVerified(!onlyVerified) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = if (onlyVerified) Color(0xFF166534) else Color.Gray,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAmharic) "የተረጋገጠ ብቻ" else "Verified Only",
                                fontSize = 11.sp,
                                fontWeight = if (onlyVerified) FontWeight.Bold else FontWeight.Normal,
                                color = if (onlyVerified) Color(0xFF166534) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Subcities
                    subCities.forEach { city ->
                        val isSelected = city == selectedSubCity
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { viewModel.selectSubCity(city) }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = city,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Listings List or Map View
        if (isMapViewMode) {
            PropertyMapView(
                listings = listings,
                isAmharic = isAmharic,
                onListingClick = { viewModel.selectListing(it) },
                onEscrowClick = { showEscrowDialogForListing = it }
            )
        } else if (listings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = EmeraldPrimary.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isAmharic) "ምንም የተገኘ ንብረት የለም" else "No matching listings found",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isAmharic) "የፍለጋ ቃላትን ይቀይሩ ወይም ማጣሪያዎችን ያጽዱ።" else "Try clearing your filters or changing search keywords.",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.setSearchQuery("")
                            viewModel.selectCategory(ListingCategory.ALL)
                            viewModel.selectType(ListingType.ALL)
                            viewModel.selectSubCity("ሁሉም")
                            viewModel.setOnlyVerified(false)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text(if (isAmharic) "ሁሉንም አሳይ" else "Reset Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listings, key = { it.id }) { item ->
                    ListingCard(
                        item = item,
                        isAmharic = isAmharic,
                        onClick = { viewModel.selectListing(item) },
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${item.posterPhone}")
                            }
                            context.startActivity(intent)
                        },
                        onChat = {
                            viewModel.openChatForListing(item)
                        },
                        onValuate = {
                            viewModel.setValuationParams(
                                category = item.category,
                                subCity = item.subCity,
                                area = (item.areaSqM ?: 120.0).toString()
                            )
                            viewModel.setTab(AppTab.VALUATION)
                        },
                        onEscrow = {
                            showEscrowDialogForListing = item
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    // Listing Details Bottom Sheet
    selectedListing?.let { item ->
        ModalBottomSheet(
            onDismissRequest = { viewModel.selectListing(null) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            ListingDetailSheetContent(
                item = item,
                isAmharic = isAmharic,
                onClose = { viewModel.selectListing(null) },
                onCall = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${item.posterPhone}")
                    }
                    context.startActivity(intent)
                },
                onChat = {
                    viewModel.selectListing(null)
                    viewModel.openChatForListing(item)
                },
                onEscrow = {
                    viewModel.selectListing(null)
                    showEscrowDialogForListing = item
                }
            )
        }
    }

    // Escrow Deposit Dialog
    showEscrowDialogForListing?.let { listing ->
        EscrowDepositDialog(
            listing = listing,
            isAmharic = isAmharic,
            onDismiss = { showEscrowDialogForListing = null },
            onConfirm = { amount, method ->
                viewModel.initiateEscrowDeposit(listing, amount, method)
                showEscrowDialogForListing = null
            }
        )
    }
}

@Composable
fun ListingCard(
    item: ListingItem,
    isAmharic: Boolean,
    onClick: () -> Unit,
    onCall: () -> Unit,
    onChat: () -> Unit,
    onValuate: () -> Unit,
    onEscrow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("listing_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header row: Category pill + Verification Badge + Rent/Sale
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (item.type == ListingType.RENT) GoldContainer else EmeraldContainer
                            )
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isAmharic) item.type.amharicName else item.type.englishName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.type == ListingType.RENT) GoldSecondary else EmeraldPrimary
                        )
                    }

                    VerificationBadge(status = item.verificationStatus, isAmharic = isAmharic)
                }

                if (item.isPromoted) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isAmharic) "ተመራጭ" else "Featured",
                            color = Color(0xFFB45309),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Property photo via Coil
            if (!item.imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.location,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Attributes row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item.areaSqM?.let { area ->
                    AttributeChip(text = "$area ካሬ (m²)")
                }
                item.bedrooms?.let { beds ->
                    AttributeChip(text = "$beds መኝታ (Beds)")
                }
                item.vehicleYear?.let { year ->
                    AttributeChip(text = "$year ሞዴል")
                }
                if (item.titleDeedNumber.isNotBlank()) {
                    AttributeChip(text = "ይዞታ: ${item.titleDeedNumber.take(12)}")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price & Negotiable tag
            val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(item.priceBirr)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$formattedPrice ብር",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldPrimary
                        )
                        item.pricePeriod?.let { period ->
                            Text(
                                text = " /$period",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = if (item.isNegotiable) (if (isAmharic) "ድርድር አለው" else "Negotiable")
                        else (if (isAmharic) "የመጨረሻ ዋጋ" else "Fixed price"),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                // Poster role info
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = item.posterName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.posterRole.amharicName,
                        fontSize = 10.sp,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCall,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isAmharic) "ደውል" else "Call", fontSize = 12.sp)
                }

                Button(
                    onClick = onChat,
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isAmharic) "መልእክት/ድምጽ" else "Chat/Voice", fontSize = 11.sp)
                }

                Button(
                    onClick = onEscrow,
                    modifier = Modifier.weight(1.3f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldSecondary)
                ) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isAmharic) "አደራ ክፍያ" else "Escrow", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AttributeChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ListingDetailSheetContent(
    item: ListingItem,
    isAmharic: Boolean,
    onClose: () -> Unit,
    onCall: () -> Unit,
    onChat: () -> Unit,
    onEscrow: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        // Top row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            VerificationBadge(status = item.verificationStatus, isAmharic = isAmharic)
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        // Property photo banner via Coil
        if (!item.imageUrl.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = item.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(item.priceBirr)
        Text(
            text = "$formattedPrice ብር ${item.pricePeriod?.let { "/$it" } ?: ""}",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = EmeraldPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Title Deed / Carta Verification Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isAmharic) "የይዞታ ማረጋገጫ ቁጥር (Title Deed/Carta)" else "Verified Title / Libre",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF166534)
                    )
                    Text(
                        text = item.titleDeedNumber,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Color(0xFF15803D)
                    )
                    Text(
                        text = if (isAmharic) "በደላላ ነኝ የመረጃ ቋት የተረጋገጠና ከእዳ ነጻ የሆነ።" else "Verified against municipal land & property records.",
                        fontSize = 11.sp,
                        color = Color(0xFF166534).copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (isAmharic) "ዝርዝር መግለጫ" else "Description",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.description,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Map Location & Google Maps Navigation Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAmharic) "የይዞታው መገኛ ካርታ (Map Location)" else "Map Location",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        text = "${String.format(Locale.US, "%.4f", item.latitude)}, ${String.format(Locale.US, "%.4f", item.longitude)}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${item.location} (${item.subCity})",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                val ctx = LocalContext.current
                Button(
                    onClick = {
                        val gmmIntentUri = Uri.parse("geo:${item.latitude},${item.longitude}?q=${item.latitude},${item.longitude}(${Uri.encode(item.title)})")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        if (mapIntent.resolveActivity(ctx.packageManager) != null) {
                            ctx.startActivity(mapIntent)
                        } else {
                            val webIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.google.com/maps/search/?api=1&query=${item.latitude},${item.longitude}")
                            )
                            ctx.startActivity(webIntent)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isAmharic) "በGoogle Maps አቅጣጫ ጀምር (Navigate)" else "Navigate in Google Maps", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Owner/Broker contact card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = item.posterName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${item.posterRole.amharicName} • ${item.posterPhone}",
                        fontSize = 12.sp,
                        color = EmeraldPrimary
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onCall,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = Color.White)
                    }
                    IconButton(
                        onClick = onChat,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(GoldSecondary)
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = "Chat", tint = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Big Escrow Action Button
        Button(
            onClick = onEscrow,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
        ) {
            Icon(imageVector = Icons.Default.Security, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isAmharic) "በአስተማማኝ አደራ ይዘዙ (Telebirr/CBE Birr)" else "Secure with Escrow Deposit",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun EscrowDepositDialog(
    listing: ListingItem,
    isAmharic: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (amount: Long, method: String) -> Unit
) {
    var depositAmountText by remember { mutableStateOf("50000") }
    var selectedMethod by remember { mutableStateOf("Telebirr (ቴሌብር)") }

    val methods = listOf("Telebirr (ቴሌብር)", "CBE Birr (ንግድ ባንክ)", "Awash Bank (አዋሽ ባንክ)")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = EmeraldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAmharic) "አስተማማኝ የአደራ ክፍያ (Escrow)" else "Secure Escrow Deposit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column {
                Text(
                    text = if (isAmharic)
                        "ገንዘብዎ በደላላ ነኝ አስተማማኝ አደራ ሒሳብ ውስጥ ይያዛል። ንብረቱን በአካል አይተው የይዞታ ማረጋገጫ እስኪያጸድቁ ድረስ ለሻጭ አይለቀቅም።"
                    else
                        "Funds will be securely locked in Delala Escrow. Money is not released to the seller/landlord until you inspect and approve the title.",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isAmharic) "የቅድመ ክፍያ መጠን (በብር)" else "Deposit Amount (ETB)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = depositAmountText,
                    onValueChange = { depositAmountText = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isAmharic) "የክፍያ አማራጭ ይምረጡ" else "Select Payment Gateway",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                methods.forEach { method ->
                    val isSelected = method == selectedMethod
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) EmeraldContainer else Color.Transparent)
                            .clickable { selectedMethod = method }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Payments,
                            contentDescription = null,
                            tint = if (isSelected) EmeraldPrimary else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = method,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) EmeraldPrimary else Color.Black
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = depositAmountText.toLongOrNull() ?: 50000L
                    onConfirm(amount, selectedMethod)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text(if (isAmharic) "ክፍያውን በአደራ ያዙ" else "Lock Deposit in Escrow")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(if (isAmharic) "ይቅር" else "Cancel")
            }
        }
    )
}
