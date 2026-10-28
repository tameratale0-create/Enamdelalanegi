package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ListingCategory
import com.example.model.ListingType
import com.example.model.UserRole
import com.example.ui.DelalaViewModel
import com.example.ui.components.CategoryIcon
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldSecondary

@Composable
fun PostListingScreen(
    viewModel: DelalaViewModel,
    modifier: Modifier = Modifier
) {
    val form by viewModel.postFormState.collectAsState()
    val isAmharic by viewModel.isAmharic.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    val subCities = listOf("ቦሌ", "ቂርቆስ", "የካ", "አዲስ ከተማ", "ሊዲታ", "አራዳ", "ቢሾፍቱ")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Role tailored Posting Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (profile.role == UserRole.LANDLORD) GoldContainer else EmeraldContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (profile.role == UserRole.LANDLORD) GoldSecondary else EmeraldPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PostAdd,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isAmharic) {
                            if (profile.role == UserRole.LANDLORD) "እንደ አከራይ ንብረት ማከራየት"
                            else if (profile.role == UserRole.SELLER) "እንደ ሻጭ ንብረት መሸጥ"
                            else "አዲስ ማስታወቂያ መለጠፍ"
                        } else {
                            if (profile.role == UserRole.LANDLORD) "Post Rental (Landlord)"
                            else "Post for Sale (Seller)"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (profile.role == UserRole.LANDLORD) Color(0xFF78350F) else Color(0xFF042B1A)
                    )
                    Text(
                        text = if (isAmharic) "ደላላ ነኝ፡ የተረጋገጠ ይዞታና ፈጣን ገዥ/ተከራይ ማገናኛ"
                        else "Delala Negn: Verified title with escrow protection",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Success / Error banner
        AnimatedVisibility(visible = form.isSuccess) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAmharic) "ማስታወቂያዎ በተሳካ ሁኔታ ተለጥፏል! ወደ ገበያ ዝርዝር እየተዛወሩ ነው..."
                        else "Listing published successfully! Redirecting to marketplace...",
                        color = Color(0xFF166534),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        form.errorMessage?.let { error ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = error, color = Color(0xFF991B1B), fontSize = 12.sp)
                }
            }
        }

        // Section 1: Listing Type (ኪራይ / ሽያጭ)
        Text(
            text = if (isAmharic) "የአገልግሎቱ አይነት" else "Listing Type",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(ListingType.RENT, ListingType.SALE).forEach { t ->
                val isSelected = t == form.type
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
                        .clickable { viewModel.updatePostForm(type = t) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isAmharic) t.amharicName else t.englishName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 2: Category Selector
        Text(
            text = if (isAmharic) "ምድብ (Category)" else "Category",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                ListingCategory.HOUSE,
                ListingCategory.VEHICLE,
                ListingCategory.COMMERCIAL,
                ListingCategory.LAND
            ).forEach { cat ->
                val isSelected = cat == form.category
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) GoldSecondary else MaterialTheme.colorScheme.surface)
                        .clickable { viewModel.updatePostForm(category = cat) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CategoryIcon(category = cat, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isAmharic) cat.amharicName.split(" ").first() else cat.englishName.split(" ").first(),
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 3: Title
        Text(
            text = if (isAmharic) "የርዕስ መግለጫ *" else "Listing Title *",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = form.title,
            onValueChange = { viewModel.updatePostForm(title = it) },
            placeholder = {
                Text(
                    text = if (form.type == ListingType.RENT)
                        (if (isAmharic) "ለምሳሌ፡ ባለ 3 መኝታ ቅንጡ ቪላ ለኪራይ - ቦሌ" else "e.g. 3 Bed Luxury Villa for Rent - Bole")
                    else
                        (if (isAmharic) "ለምሳሌ፡ ቶዮታ ራቭ4 2021 ሞዴል - ንጹህ ሊብሬ" else "e.g. Toyota RAV4 2021 Model - Clean Libre"),
                    fontSize = 12.sp
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("post_title_input"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Section 4: Price & Negotiable
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1.5f)) {
                Text(
                    text = if (isAmharic) "የዋጋ መጠን (በብር) *" else "Price (in ETB) *",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = form.priceBirrText,
                    onValueChange = { viewModel.updatePostForm(priceBirrText = it) },
                    placeholder = { Text("ለምሳሌ 75000", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("post_price_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (form.type == ListingType.RENT) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isAmharic) "የክፍያ ጊዜ" else "Period",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = form.pricePeriod,
                        onValueChange = { viewModel.updatePostForm(pricePeriod = it) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Negotiable checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                viewModel.updatePostForm(isNegotiable = !form.isNegotiable)
            }
        ) {
            Checkbox(
                checked = form.isNegotiable,
                onCheckedChange = { viewModel.updatePostForm(isNegotiable = it) },
                colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
            )
            Text(
                text = if (isAmharic) "ዋጋው ይደራደራል (Negotiable)" else "Price is negotiable",
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Section 5: Location & Sub-city
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = if (isAmharic) "ዝርዝር አድራሻ" else "Location Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = form.location,
                    onValueChange = { viewModel.updatePostForm(location = it) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Column(modifier = Modifier.weight(0.8f)) {
                Text(
                    text = if (isAmharic) "ክ/ከተማ" else "Sub-city",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = form.subCity,
                    onValueChange = { viewModel.updatePostForm(subCity = it) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 6: Ownership & Title Deed Verification (ካርታ ወይም ሊብሬ ቁጥር)
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAmharic) "የይዞታ ማረጋገጫ ቁጥር (ካርታ ወይም ሊብሬ) *"
                        else "Title Deed / Libre Verification Code *",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = EmeraldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isAmharic)
                        "የካርታ ወይም የሊብሬ ቁጥር ማስገባት ማስታወቂያዎ 'የተረጋገጠ (Verified)' ምልክት እንዲያገኝ እና ገዥዎች እንዲያምኑት ያደርጋል።"
                    else
                        "Entering title deed number grants 'Verified' badge and attracts 3x more trusted buyers.",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = form.titleDeedNumber,
                    onValueChange = { viewModel.updatePostForm(titleDeedNumber = it) },
                    placeholder = { Text("ለምሳሌ፡ BL-RW-90218-AA", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("post_title_deed_input"),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 7: Description
        Text(
            text = if (isAmharic) "ተጨማሪ ዝርዝር መግለጫ" else "Detailed Description",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = form.description,
            onValueChange = { viewModel.updatePostForm(description = it) },
            placeholder = {
                Text(
                    text = if (isAmharic) "የመብራት፣ የውሃ፣ የመኪና ማቆሚያ፣ የሰነድ ሁኔታዎችን ይግለጹ..."
                    else "Describe amenities, documentation, water/power supply...",
                    fontSize = 12.sp
                )
            },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Section 8: Property Photo (Coil Image Loading)
        Text(
            text = if (isAmharic) "የንብረቱ ፎቶ (Property Photo URL)" else "Property Photo (Image URL)",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = form.imageUrl,
            onValueChange = { viewModel.updatePostForm(imageUrl = it) },
            placeholder = {
                Text(
                    text = if (isAmharic) "የፎቶ ሊንክ (URL) ያስገቡ ወይም ባዶ ይተዉት" else "Paste image URL or leave empty for auto-assign",
                    fontSize = 12.sp
                )
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = EmeraldPrimary)
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("post_image_url_input"),
            shape = RoundedCornerShape(12.dp)
        )

        // Live preview if URL provided
        if (form.imageUrl.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = form.imageUrl,
                    contentDescription = "Preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Offline notice
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isAmharic) "ከመስመር ውጭ ሲሆኑም በስልክዎ ላይ በቀጥታ ይቀመጣል።"
                else "Offline draft sync: Saved locally even without internet.",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Submit Button
        Button(
            onClick = { viewModel.submitPostListing() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("submit_post_button"),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.PostAdd, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isAmharic) "ማስታወቂያውን በደላላ ነኝ ለጥፍ" else "Publish to Delala Negn",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
