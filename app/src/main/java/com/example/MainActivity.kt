package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.AppTab
import com.example.ui.DelalaViewModel
import com.example.ui.components.DelalaTopAppBar
import com.example.ui.components.NotificationCenterBottomSheet
import com.example.ui.components.OfflineBanner
import com.example.ui.screens.EscrowVerificationScreen
import com.example.ui.screens.MarketplaceScreen
import com.example.ui.screens.MessagesScreen
import com.example.ui.screens.PostListingScreen
import com.example.ui.screens.ValuationScreen
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: DelalaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DelalaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DelalaApp(viewModel: DelalaViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val isAmharic by viewModel.isAmharic.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val activeChatListing by viewModel.activeChatListing.collectAsState()

    val unreadNotifsCount by viewModel.unreadNotificationsCount.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val fcmToken by viewModel.fcmToken.collectAsState()
    var showNotificationSheet by remember { mutableStateOf(false) }

    // Request POST_NOTIFICATIONS permission on Android 13+
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Back handling: pop active chat or return to marketplace tab
    BackHandler(enabled = activeChatListing != null || currentTab != AppTab.MARKETPLACE) {
        if (activeChatListing != null) {
            viewModel.closeChat()
        } else if (currentTab != AppTab.MARKETPLACE) {
            viewModel.setTab(AppTab.MARKETPLACE)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                DelalaTopAppBar(
                    isOnline = isOnline,
                    isAmharic = isAmharic,
                    userProfile = userProfile,
                    unreadNotifsCount = unreadNotifsCount,
                    onToggleOnline = { viewModel.toggleOnlineMode() },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onProfileClick = { viewModel.setTab(AppTab.ESCROW_VERIFY) },
                    onNotificationClick = { showNotificationSheet = true }
                )
                AnimatedVisibility(visible = !isOnline) {
                    OfflineBanner(isAmharic = isAmharic)
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navItems = listOf(
                    Triple(AppTab.MARKETPLACE, Icons.Default.Storefront, "nav_marketplace"),
                    Triple(AppTab.VALUATION, Icons.Default.Calculate, "nav_valuation"),
                    Triple(AppTab.POST_LISTING, Icons.Default.PostAdd, "nav_post"),
                    Triple(AppTab.MESSAGES, Icons.Default.Chat, "nav_messages"),
                    Triple(AppTab.ESCROW_VERIFY, Icons.Default.Security, "nav_escrow")
                )

                navItems.forEach { (tab, icon, testTag) ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (tab != AppTab.MESSAGES) {
                                viewModel.closeChat()
                            }
                            viewModel.setTab(tab)
                        },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = if (isAmharic) tab.amharicTitle else tab.englishTitle
                            )
                        },
                        label = {
                            Text(
                                text = if (isAmharic) tab.amharicTitle else tab.englishTitle,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = EmeraldContainer,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag(testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.MARKETPLACE -> MarketplaceScreen(viewModel = viewModel)
                AppTab.VALUATION -> ValuationScreen(viewModel = viewModel)
                AppTab.POST_LISTING -> PostListingScreen(viewModel = viewModel)
                AppTab.MESSAGES -> MessagesScreen(viewModel = viewModel)
                AppTab.ESCROW_VERIFY -> EscrowVerificationScreen(viewModel = viewModel)
            }
        }
    }

    if (showNotificationSheet) {
        NotificationCenterBottomSheet(
            notifications = notifications,
            fcmToken = fcmToken,
            isAmharic = isAmharic,
            onDismiss = { showNotificationSheet = false },
            onNotificationClick = { notif ->
                viewModel.markNotificationAsRead(notif.id)
                showNotificationSheet = false
                when (notif.type) {
                    com.example.model.NotificationType.CHAT_MESSAGE -> viewModel.setTab(AppTab.MESSAGES)
                    com.example.model.NotificationType.ESCROW_PAYMENT -> viewModel.setTab(AppTab.ESCROW_VERIFY)
                    com.example.model.NotificationType.RENTAL_APPLICATION -> viewModel.setTab(AppTab.MARKETPLACE)
                    com.example.model.NotificationType.PROPERTY_INTEREST -> viewModel.setTab(AppTab.MARKETPLACE)
                }
            },
            onMarkAllRead = { viewModel.markAllNotificationsAsRead() },
            onTriggerTest = { type -> viewModel.triggerTestNotification(type) }
        )
    }
}
