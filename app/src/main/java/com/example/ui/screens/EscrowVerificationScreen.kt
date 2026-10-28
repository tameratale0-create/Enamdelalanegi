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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EscrowPayment
import com.example.model.EscrowStatus
import com.example.model.UserRole
import com.example.ui.DelalaViewModel
import com.example.ui.components.EscrowProgressTracker
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.TelebirrBlue
import java.text.NumberFormat
import java.util.Locale

@Composable
fun EscrowVerificationScreen(
    viewModel: DelalaViewModel,
    modifier: Modifier = Modifier
) {
    val isAmharic by viewModel.isAmharic.collectAsState()
    val allEscrows by viewModel.allEscrows.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val searchCode by viewModel.verificationSearchCode.collectAsState()
    val verificationResult by viewModel.verificationResult.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Section 1: User Profile & Role Switcher
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = userProfile.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "${userProfile.phone} • ${userProfile.subCity}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Rating & Transactions
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = GoldSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${userProfile.rating}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = "${userProfile.totalTransactions} ግብይቶች",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Role selection chips (አከራይ እንደ አከራይ፣ ሻጭ እንደ ሻጭ)
                Text(
                    text = if (isAmharic) "የምዝገባ ሚናዎን ይምረጡ (Switch Active Role):" else "Select your active profile role:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UserRole.values().forEach { role ->
                        val isSelected = role == userProfile.role
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GoldSecondary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { viewModel.switchUserRole(role) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isAmharic) role.amharicName.split(" ").first() else role.englishName.split(" ").first(),
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section 2: Title Deed Verification (የይዞታ ማረጋገጫ)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAmharic) "የይዞታ ማረጋገጫ ፈልግ (Carta & Libre Checker)" else "Title Deed & Libre Verification",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = EmeraldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isAmharic)
                        "የመሬት ይዞታ ካርታ ወይም የተሽከርካሪ ሊብሬ ቁጥር በማስገባት ትክክለኛነቱንና ከእዳ ነጻ መሆኑን ያረጋግጡ።"
                    else
                        "Enter the Carta deed number or Vehicle Libre ID to verify authenticity against municipal records.",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchCode,
                        onValueChange = { viewModel.setVerificationSearchCode(it) },
                        placeholder = { Text("ለምሳሌ፡ BL-RW-90218-AA", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("verify_code_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = { viewModel.runVerificationSearch() },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isAmharic) "አረጋግጥ" else "Verify")
                    }
                }

                verificationResult?.let { res ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (res.first) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = if (res.first) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (res.first) Color(0xFF166534) else Color(0xFF991B1B),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = res.second,
                                fontSize = 12.sp,
                                color = if (res.first) Color(0xFF166534) else Color(0xFF991B1B),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section 3: Trust Escrow System (አስተማማኝ አደራ ክፍያ)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = GoldSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAmharic) "አስተማማኝ የአደራ ክፍያ (Trust Escrow)" else "Secure Escrow Protection",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isAmharic)
                        "ደላላ ነኝ በቴሌብር (Telebirr) እና በኢትዮጵያ ንግድ ባንክ (CBE) በኩል ክፍያዎችን በአደራ በመያዝ ገዥና ሻጭን ከማጭበርበር ይጠብቃል። ገዥ ይዞታውን አይቶ ሲያጸድቅ ብቻ ገንዘቡ ይለቀቃል።"
                    else
                        "Escrow holds deposits in trust via Telebirr & CBE Birr until physical inspection and deed approval.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (isAmharic) "የአደራ ግብይቶችዎ (Active Escrows)" else "Active Escrow Transactions",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (allEscrows.isEmpty()) {
            Text(
                text = if (isAmharic) "እስካሁን የተመዘገበ የአደራ ክፍያ የለም።" else "No active escrow transactions.",
                color = Color.Gray,
                fontSize = 12.sp
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                allEscrows.forEach { escrow ->
                    EscrowTransactionCard(
                        escrow = escrow,
                        isAmharic = isAmharic,
                        onAdvanceStatus = {
                            viewModel.advanceEscrowStatus(escrow.id, escrow.status)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun EscrowTransactionCard(
    escrow: EscrowPayment,
    isAmharic: Boolean,
    onAdvanceStatus: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = escrow.id,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoldContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = escrow.paymentMethod,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = escrow.listingTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            val formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(escrow.amountBirr)
            Text(
                text = "የአደራ መጠን፡ $formattedAmount ብር",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = EmeraldPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${escrow.buyerName} -> ${escrow.sellerName}",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Step Progress Tracker
            EscrowProgressTracker(status = escrow.status, isAmharic = isAmharic)

            Spacer(modifier = Modifier.height(10.dp))

            if (escrow.status != EscrowStatus.RELEASED_TO_SELLER) {
                Button(
                    onClick = onAdvanceStatus,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (escrow.status) {
                            EscrowStatus.PENDING_DEPOSIT -> if (isAmharic) "ክፍያውን አደራ ያረጋግጡ" else "Confirm Deposit"
                            EscrowStatus.HELD_IN_ESCROW -> if (isAmharic) "የይዞታ ፍተሻን አጽድቅ" else "Approve Inspection & Deed"
                            EscrowStatus.INSPECTION_CONFIRMED -> if (isAmharic) "ገንዘቡን ለሻጭ ልቀቅ" else "Release Funds to Seller"
                            else -> if (isAmharic) "ተጠናቋል" else "Completed"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
