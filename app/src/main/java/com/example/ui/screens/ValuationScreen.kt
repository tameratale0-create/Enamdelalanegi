package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ListingCategory
import com.example.model.ListingType
import com.example.ui.AppTab
import com.example.ui.DelalaViewModel
import com.example.ui.components.CategoryIcon
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ValuationScreen(
    viewModel: DelalaViewModel,
    modifier: Modifier = Modifier
) {
    val isAmharic by viewModel.isAmharic.collectAsState()
    val category by viewModel.valCategory.collectAsState()
    val subCity by viewModel.valSubCity.collectAsState()
    val area by viewModel.valArea.collectAsState()
    val condition by viewModel.valCondition.collectAsState()
    val result by viewModel.valuationResult.collectAsState()

    val subCities = listOf(
        "ቦሌ (Bole)",
        "ቂርቆስ (ሳርቤት)",
        "የካ (ሲኤምሲ)",
        "አዲስ ከተማ (መርካቶ)",
        "ቢሾፍቱ (ደብረዘይት)"
    )

    val conditions = listOf(
        "አዲስ (Brand New)",
        "ጥሩ ይዞታ (Good)",
        "እድሳት የሚፈልግ (Needs Renovation)"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hero Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isAmharic) "የኢትዮጵያ የገበያ ዋጋ ተመን ማስያ" else "Ethiopian Market Valuation",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isAmharic)
                        "የአካባቢውን ወቅታዊ የሽያጭና ኪራይ ዋጋዎች፣ የካሬ ተመንና የዋጋ ግሽበትን መሰረት በማድረግ ትክክለኛ የገበያ ተመን ይተነብዩ።"
                    else
                        "Estimate fair property and asset values based on location, area, and recent municipal transactions.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Parameter 1: Category
        Text(
            text = if (isAmharic) "1. የንብረት ወይም የእቃው አይነት" else "1. Asset Category",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                ListingCategory.HOUSE,
                ListingCategory.VEHICLE,
                ListingCategory.COMMERCIAL,
                ListingCategory.LAND
            ).forEach { cat ->
                val isSelected = cat == category
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surface)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { viewModel.setValuationParams(category = cat) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CategoryIcon(
                            category = cat,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isAmharic) cat.amharicName.split(" ").first() else cat.englishName.split(" ").first(),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Parameter 2: Location / Subcity
        Text(
            text = if (isAmharic) "2. አካባቢ / ክፍለ ከተማ" else "2. Location / Sub-city",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            subCities.forEach { city ->
                val isSelected = city == subCity
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) EmeraldContainer else MaterialTheme.colorScheme.surface)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { viewModel.setValuationParams(subCity = city) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (isSelected) EmeraldPrimary else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = city,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Parameter 3: Area or Model Year
        Text(
            text = if (category == ListingCategory.VEHICLE) {
                if (isAmharic) "3. የተሽከርካሪው ሞዴል ዓ.ም" else "3. Vehicle Model Year"
            } else {
                if (isAmharic) "3. ስፋት በካሬ ሜትር (m²)" else "3. Area in Square Meters (m²)"
            },
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = area,
            onValueChange = { viewModel.setValuationParams(area = it) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("valuation_area_input"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Parameter 4: Condition
        Text(
            text = if (isAmharic) "4. የይዞታው ሁኔታ" else "4. Condition / Finishing",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            conditions.forEach { cond ->
                val isSelected = cond == condition
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) GoldSecondary else MaterialTheme.colorScheme.surface)
                        .clickable { viewModel.setValuationParams(condition = cond) }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cond.split(" ").first(),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Result Card
        result?.let { res ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("valuation_result_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAmharic) "የተገመተ አማካይ የገበያ ዋጋ" else "Estimated Fair Market Value",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFDCFCE7))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ተዓማኒነት ${res.confidenceScore}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val formattedAvg = NumberFormat.getNumberInstance(Locale.US).format(res.estimatedAvgPrice)
                    val formattedMin = NumberFormat.getNumberInstance(Locale.US).format(res.estimatedMinPrice)
                    val formattedMax = NumberFormat.getNumberInstance(Locale.US).format(res.estimatedMaxPrice)

                    Text(
                        text = "$formattedAvg ብር",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Range Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isAmharic) "ዝቅተኛ ዋጋ" else "Min Price",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "$formattedMin ብር",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isAmharic) "ከፍተኛ ዋጋ" else "Max Price",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "$formattedMax ብር",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (res.pricePerSqM > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        val formattedSqM = NumberFormat.getNumberInstance(Locale.US).format(res.pricePerSqM)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAmharic) "የአንድ ካሬ ሜትር አማካይ ተመን" else "Price per Sq. Meter",
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "$formattedSqM ብር/m²",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = EmeraldPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Market Trend Insight
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = GoldSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = res.trendAmharic,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action button to post listing using this calculated price
                    Button(
                        onClick = {
                            viewModel.updatePostForm(
                                category = category,
                                subCity = subCity.split(" ").first(),
                                priceBirrText = res.estimatedAvgPrice.toString(),
                                areaSqMText = area
                            )
                            viewModel.setTab(AppTab.POST_LISTING)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.PostAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAmharic) "በዚህ ተመን ማስታወቂያ ለጥፍ" else "Post Listing with this Valuation",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
