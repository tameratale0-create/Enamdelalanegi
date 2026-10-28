package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.repository.DelalaRepository
import com.example.model.ChatMessage
import com.example.model.EscrowPayment
import com.example.model.EscrowStatus
import com.example.model.ListingCategory
import com.example.model.ListingItem
import com.example.model.ListingType
import com.example.model.MarketValuationResult
import com.example.model.NotificationType
import com.example.model.PushNotificationItem
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.model.VerificationStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppTab(val amharicTitle: String, val englishTitle: String) {
    MARKETPLACE("ገበያ", "Browse"),
    VALUATION("የዋጋ ተመን", "Valuation"),
    POST_LISTING("አዲስ መለጠፊያ", "Post"),
    MESSAGES("መልእክት", "Messages"),
    ESCROW_VERIFY("አደራና ይዞታ", "Trust & Carta")
}

data class PostListingFormState(
    val title: String = "",
    val description: String = "",
    val category: ListingCategory = ListingCategory.HOUSE,
    val type: ListingType = ListingType.RENT,
    val priceBirrText: String = "",
    val pricePeriod: String = "በወር",
    val isNegotiable: Boolean = true,
    val location: String = "ቦሌ፣ አዲስ አበባ",
    val subCity: String = "ቦሌ",
    val areaSqMText: String = "120",
    val bedroomsText: String = "2",
    val vehicleYearText: String = "2020",
    val titleDeedNumber: String = "BL-AA-40912",
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED_TITLE_DEED,
    val imageUrl: String = "",
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

data class DelalaFilterState(
    val query: String = "",
    val category: ListingCategory = ListingCategory.ALL,
    val type: ListingType = ListingType.ALL,
    val subCity: String = "ሁሉም",
    val onlyVerified: Boolean = false
)

class DelalaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DelalaRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = DelalaRepository(db)
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // App state
    private val _currentTab = MutableStateFlow(AppTab.MARKETPLACE)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _isAmharic = MutableStateFlow(true)
    val isAmharic: StateFlow<Boolean> = _isAmharic.asStateFlow()

    // Search and filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ListingCategory.ALL)
    val selectedCategory: StateFlow<ListingCategory> = _selectedCategory.asStateFlow()

    private val _selectedType = MutableStateFlow(ListingType.ALL)
    val selectedType: StateFlow<ListingType> = _selectedType.asStateFlow()

    private val _selectedSubCity = MutableStateFlow("ሁሉም")
    val selectedSubCity: StateFlow<String> = _selectedSubCity.asStateFlow()

    private val _onlyVerified = MutableStateFlow(false)
    val onlyVerified: StateFlow<Boolean> = _onlyVerified.asStateFlow()

    // Selected listing for details
    private val _selectedListing = MutableStateFlow<ListingItem?>(null)
    val selectedListing: StateFlow<ListingItem?> = _selectedListing.asStateFlow()

    // Active chat listing
    private val _activeChatListing = MutableStateFlow<ListingItem?>(null)
    val activeChatListing: StateFlow<ListingItem?> = _activeChatListing.asStateFlow()

    // Post form state
    private val _postFormState = MutableStateFlow(PostListingFormState())
    val postFormState: StateFlow<PostListingFormState> = _postFormState.asStateFlow()

    // Valuation state
    private val _valCategory = MutableStateFlow(ListingCategory.HOUSE)
    val valCategory: StateFlow<ListingCategory> = _valCategory.asStateFlow()

    private val _valSubCity = MutableStateFlow("ቦሌ (Bole)")
    val valSubCity: StateFlow<String> = _valSubCity.asStateFlow()

    private val _valArea = MutableStateFlow("140")
    val valArea: StateFlow<String> = _valArea.asStateFlow()

    private val _valCondition = MutableStateFlow("ጥሩ ይዞታ (Good)")
    val valCondition: StateFlow<String> = _valCondition.asStateFlow()

    private val _valuationResult = MutableStateFlow<MarketValuationResult?>(null)
    val valuationResult: StateFlow<MarketValuationResult?> = _valuationResult.asStateFlow()

    // Voice recording & playback state
    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()

    private val _liveAmplitudes = MutableStateFlow<List<Float>>(emptyList())
    val liveAmplitudes: StateFlow<List<Float>> = _liveAmplitudes.asStateFlow()

    private val _playingMessageId = MutableStateFlow<Long?>(null)
    val playingMessageId: StateFlow<Long?> = _playingMessageId.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    private var recordingJob: Job? = null
    private var playbackJob: Job? = null

    // Notification state
    private val _fcmToken = MutableStateFlow<String?>(null)
    val fcmToken: StateFlow<String?> = _fcmToken.asStateFlow()

    private val _notifications = MutableStateFlow<List<PushNotificationItem>>(
        listOf(
            PushNotificationItem(
                id = "notif_1",
                title = "አዲስ ፍላጎት በንብረትዎ ላይ! (Property Interest)",
                body = "ተከራይ በሲኤምሲ ሚካኤል ቪላ ቤትዎ ላይ ፍላጎት አሳይቷል፤ ውይይት ይጀምሩ።",
                type = NotificationType.PROPERTY_INTEREST,
                timestamp = System.currentTimeMillis() - 1800000L,
                isRead = false
            ),
            PushNotificationItem(
                id = "notif_2",
                title = "የኪራይ ማመልከቻ ጸድቋል (Rental Approved)",
                body = "የቦሌ ሩዋንዳ አፓርታማ ማመልከቻዎ በአከራዩ ተቀባይነት አግኝቷል።",
                type = NotificationType.RENTAL_APPLICATION,
                timestamp = System.currentTimeMillis() - 7200000L,
                isRead = false
            ),
            PushNotificationItem(
                id = "notif_3",
                title = "የአደራ ክፍያ ተረጋግጧል (Escrow Held)",
                body = "50,000 ብር በቴሌብር (Telebirr) በአደራ ተይዟል፤ ይዞታዎን ማስተላለፍ ይችላሉ።",
                type = NotificationType.ESCROW_PAYMENT,
                timestamp = System.currentTimeMillis() - 14400000L,
                isRead = true
            )
        )
    )
    val notifications: StateFlow<List<PushNotificationItem>> = _notifications.asStateFlow()

    val unreadNotificationsCount: StateFlow<Int> = _notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 2
    )

    // Title deed verification search state
    private val _verificationSearchCode = MutableStateFlow("")
    val verificationSearchCode: StateFlow<String> = _verificationSearchCode.asStateFlow()

    private val _verificationResult = MutableStateFlow<Pair<Boolean, String>?>(null)
    val verificationResult: StateFlow<Pair<Boolean, String>?> = _verificationResult.asStateFlow()

    // User profile from DB
    val userProfile: StateFlow<UserProfile> = repository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserProfile()
    )

    // Raw and filtered listings
    val rawListings: StateFlow<List<ListingItem>> = repository.allListings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val filterStateFlow = combine(
        _searchQuery,
        _selectedCategory,
        _selectedType,
        combine(_selectedSubCity, _onlyVerified) { sc, ov -> Pair(sc, ov) }
    ) { query, cat, type, subCityAndVerified ->
        val (subCity, verifiedOnly) = subCityAndVerified
        DelalaFilterState(query, cat, type, subCity, verifiedOnly)
    }

    val filteredListings: StateFlow<List<ListingItem>> = combine(
        rawListings,
        filterStateFlow
    ) { listings, filters ->
        listings.filter { item ->
            val matchQuery = filters.query.isBlank() ||
                    item.title.contains(filters.query, ignoreCase = true) ||
                    item.location.contains(filters.query, ignoreCase = true) ||
                    item.description.contains(filters.query, ignoreCase = true)
            val matchCategory = (filters.category == ListingCategory.ALL) || (item.category == filters.category)
            val matchType = (filters.type == ListingType.ALL) || (item.type == filters.type)
            val matchSubCity = (filters.subCity == "ሁሉም" || filters.subCity == "All") || item.subCity.contains(filters.subCity, ignoreCase = true)
            val matchVerified = !filters.onlyVerified || (item.verificationStatus != VerificationStatus.UNVERIFIED)

            matchQuery && matchCategory && matchType && matchSubCity && matchVerified
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allEscrows: StateFlow<List<EscrowPayment>> = repository.allEscrows.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentMessages: StateFlow<List<ChatMessage>> = repository.recentMessages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current chat messages for active listing
    private val _currentChatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val currentChatMessages: StateFlow<List<ChatMessage>> = _currentChatMessages.asStateFlow()

    init {
        // Initial valuation calculation
        calculateValuation()
        fetchFcmToken()
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun toggleOnlineMode() {
        _isOnline.value = !_isOnline.value
    }

    fun toggleLanguage() {
        _isAmharic.value = !_isAmharic.value
    }

    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun selectCategory(cat: ListingCategory) {
        _selectedCategory.value = cat
    }

    fun selectType(type: ListingType) {
        _selectedType.value = type
    }

    fun selectSubCity(subCity: String) {
        _selectedSubCity.value = subCity
    }

    fun setOnlyVerified(verified: Boolean) {
        _onlyVerified.value = verified
    }

    fun selectListing(item: ListingItem?) {
        _selectedListing.value = item
    }

    fun openChatForListing(item: ListingItem) {
        _activeChatListing.value = item
        _currentTab.value = AppTab.MESSAGES
        observeChatForListing(item.id)
    }

    fun closeChat() {
        _activeChatListing.value = null
    }

    private fun observeChatForListing(listingId: Long) {
        viewModelScope.launch {
            repository.getChatMessages(listingId).collect { messages ->
                _currentChatMessages.value = messages
            }
        }
    }

    fun sendTextMessage(text: String) {
        val listing = _activeChatListing.value ?: return
        if (text.isBlank()) return
        val profile = userProfile.value
        viewModelScope.launch {
            repository.sendTextMessage(
                listingId = listing.id,
                listingTitle = listing.title,
                text = text,
                senderName = "${profile.name} (${profile.role.amharicName})"
            )
        }
    }

    // Voice recording simulation with realistic waveforms and duration
    fun startVoiceRecording() {
        _isRecordingVoice.value = true
        _recordingSeconds.value = 0
        _liveAmplitudes.value = emptyList()

        recordingJob?.cancel()
        recordingJob = viewModelScope.launch {
            while (_isRecordingVoice.value) {
                delay(500)
                _recordingSeconds.value += 1
                val amp = Random.nextFloat().coerceIn(0.2f, 0.95f)
                _liveAmplitudes.value = (_liveAmplitudes.value + amp).takeLast(25)
            }
        }
    }

    fun stopAndSendVoiceRecording() {
        if (!_isRecordingVoice.value) return
        _isRecordingVoice.value = false
        val duration = _recordingSeconds.value.coerceAtLeast(2)
        val amplitudes = _liveAmplitudes.value.ifEmpty {
            listOf(0.3f, 0.6f, 0.8f, 0.9f, 0.7f, 0.4f, 0.8f, 0.5f)
        }
        val listing = _activeChatListing.value
        val profile = userProfile.value

        recordingJob?.cancel()

        if (listing != null) {
            viewModelScope.launch {
                repository.sendVoiceMessage(
                    listingId = listing.id,
                    listingTitle = listing.title,
                    durationSeconds = duration,
                    amplitudes = amplitudes,
                    senderName = "${profile.name} (${profile.role.amharicName})"
                )
            }
        }
    }

    fun cancelVoiceRecording() {
        _isRecordingVoice.value = false
        _recordingSeconds.value = 0
        _liveAmplitudes.value = emptyList()
        recordingJob?.cancel()
    }

    fun playVoiceMessage(messageId: Long, durationSec: Int) {
        if (_playingMessageId.value == messageId) {
            // Stop playing
            _playingMessageId.value = null
            _playbackProgress.value = 0f
            playbackJob?.cancel()
            return
        }

        _playingMessageId.value = messageId
        _playbackProgress.value = 0f
        playbackJob?.cancel()

        playbackJob = viewModelScope.launch {
            val totalSteps = durationSec * 10
            for (step in 1..totalSteps) {
                delay(100)
                if (_playingMessageId.value != messageId) break
                _playbackProgress.value = step.toFloat() / totalSteps
            }
            if (_playingMessageId.value == messageId) {
                _playingMessageId.value = null
                _playbackProgress.value = 0f
            }
        }
    }

    // Post Listing Form helpers
    fun updatePostForm(
        title: String? = null,
        description: String? = null,
        category: ListingCategory? = null,
        type: ListingType? = null,
        priceBirrText: String? = null,
        pricePeriod: String? = null,
        isNegotiable: Boolean? = null,
        location: String? = null,
        subCity: String? = null,
        areaSqMText: String? = null,
        bedroomsText: String? = null,
        vehicleYearText: String? = null,
        titleDeedNumber: String? = null,
        imageUrl: String? = null
    ) {
        _postFormState.value = _postFormState.value.copy(
            title = title ?: _postFormState.value.title,
            description = description ?: _postFormState.value.description,
            category = category ?: _postFormState.value.category,
            type = type ?: _postFormState.value.type,
            priceBirrText = priceBirrText ?: _postFormState.value.priceBirrText,
            pricePeriod = pricePeriod ?: _postFormState.value.pricePeriod,
            isNegotiable = isNegotiable ?: _postFormState.value.isNegotiable,
            location = location ?: _postFormState.value.location,
            subCity = subCity ?: _postFormState.value.subCity,
            areaSqMText = areaSqMText ?: _postFormState.value.areaSqMText,
            bedroomsText = bedroomsText ?: _postFormState.value.bedroomsText,
            vehicleYearText = vehicleYearText ?: _postFormState.value.vehicleYearText,
            titleDeedNumber = titleDeedNumber ?: _postFormState.value.titleDeedNumber,
            imageUrl = imageUrl ?: _postFormState.value.imageUrl,
            errorMessage = null,
            isSuccess = false
        )
    }

    fun submitPostListing() {
        val form = _postFormState.value
        if (form.title.isBlank() || form.priceBirrText.isBlank()) {
            _postFormState.value = form.copy(errorMessage = "እባክዎ ርዕስ እና ዋጋ በትክክል ያስገቡ።")
            return
        }

        val price = form.priceBirrText.replace(",", "").trim().toLongOrNull() ?: 0L
        if (price <= 0) {
            _postFormState.value = form.copy(errorMessage = "እባክዎ ትክክለኛ የዋጋ መጠን ያስገቡ።")
            return
        }

        val profile = userProfile.value
        val verification = if (form.titleDeedNumber.isNotBlank()) {
            if (form.category == ListingCategory.VEHICLE) VerificationStatus.VERIFIED_LIBRE
            else VerificationStatus.VERIFIED_TITLE_DEED
        } else {
            VerificationStatus.PENDING_REVIEW
        }

        val iconKey = when (form.category) {
            ListingCategory.HOUSE -> if (form.type == ListingType.RENT) "villa" else "apartment"
            ListingCategory.VEHICLE -> "suv"
            ListingCategory.COMMERCIAL -> "shop"
            ListingCategory.LAND -> "land"
            ListingCategory.MACHINERY -> "machinery"
            ListingCategory.ALL -> "house"
        }

        val (lat, lng) = when {
            form.subCity.contains("ቦሌ") || form.subCity.contains("Bole") -> Pair(8.9950 + Random.nextDouble(-0.012, 0.012), 38.7880 + Random.nextDouble(-0.012, 0.012))
            form.subCity.contains("የካ") || form.subCity.contains("Yeka") || form.subCity.contains("ሲኤምሲ") -> Pair(9.0230 + Random.nextDouble(-0.012, 0.012), 38.8350 + Random.nextDouble(-0.012, 0.012))
            form.subCity.contains("ቂርቆስ") || form.subCity.contains("ሳርቤት") -> Pair(9.0010 + Random.nextDouble(-0.012, 0.012), 38.7480 + Random.nextDouble(-0.012, 0.012))
            form.subCity.contains("አዲስ ከተማ") || form.subCity.contains("መርካቶ") -> Pair(9.0300 + Random.nextDouble(-0.012, 0.012), 38.7380 + Random.nextDouble(-0.012, 0.012))
            form.subCity.contains("ቢሾፍቱ") -> Pair(8.7520 + Random.nextDouble(-0.012, 0.012), 38.9810 + Random.nextDouble(-0.012, 0.012))
            else -> Pair(9.0100 + Random.nextDouble(-0.02, 0.02), 38.7600 + Random.nextDouble(-0.02, 0.02))
        }

        val newListing = ListingItem(
            title = form.title,
            description = form.description.ifBlank { "አዲስ የተለጠፈ ንብረት፣ ዝርዝር መረጃ ከባለቤቱ ማግኘት ይችላሉ።" },
            category = form.category,
            type = form.type,
            priceBirr = price,
            pricePeriod = if (form.type == ListingType.RENT) form.pricePeriod else null,
            isNegotiable = form.isNegotiable,
            location = form.location,
            subCity = form.subCity,
            latitude = lat,
            longitude = lng,
            areaSqM = form.areaSqMText.toDoubleOrNull(),
            bedrooms = form.bedroomsText.toIntOrNull(),
            bathrooms = 2,
            vehicleYear = form.vehicleYearText.toIntOrNull(),
            vehicleTransmission = if (form.category == ListingCategory.VEHICLE) "አውቶማቲክ" else null,
            titleDeedNumber = form.titleDeedNumber.ifBlank { "PENDING-${Random.nextInt(1000, 9999)}" },
            verificationStatus = verification,
            posterName = profile.name,
            posterPhone = profile.phone,
            posterRole = profile.role,
            isOfflineCached = true,
            isPromoted = false,
            iconKey = iconKey,
            imageUrl = if (form.imageUrl.isNotBlank()) form.imageUrl else when (form.category) {
                ListingCategory.HOUSE -> if (form.type == ListingType.RENT) "https://images.unsplash.com/photo-1580587771525-78b9dba3b914?w=600&auto=format&fit=crop&q=80" else "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=600&auto=format&fit=crop&q=80"
                ListingCategory.VEHICLE -> "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=600&auto=format&fit=crop&q=80"
                ListingCategory.COMMERCIAL -> "https://images.unsplash.com/photo-1555529669-e69e7aa0ba9a?w=600&auto=format&fit=crop&q=80"
                ListingCategory.LAND -> "https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=600&auto=format&fit=crop&q=80"
                ListingCategory.MACHINERY -> "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=600&auto=format&fit=crop&q=80"
                ListingCategory.ALL -> "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=600&auto=format&fit=crop&q=80"
            }
        )

        viewModelScope.launch {
            repository.addListing(newListing)
            _postFormState.value = PostListingFormState(isSuccess = true)
            // Navigate back to marketplace to see the posted listing
            delay(1200)
            _currentTab.value = AppTab.MARKETPLACE
        }
    }

    // Valuation setters
    fun setValuationParams(
        category: ListingCategory? = null,
        subCity: String? = null,
        area: String? = null,
        condition: String? = null
    ) {
        if (category != null) _valCategory.value = category
        if (subCity != null) _valSubCity.value = subCity
        if (area != null) _valArea.value = area
        if (condition != null) _valCondition.value = condition
        calculateValuation()
    }

    fun calculateValuation() {
        val area = _valArea.value.toDoubleOrNull() ?: 120.0
        val result = repository.calculateValuation(
            category = _valCategory.value,
            subCity = _valSubCity.value,
            areaSqM = area,
            condition = _valCondition.value
        )
        _valuationResult.value = result
    }

    // Title verification search
    fun setVerificationSearchCode(code: String) {
        _verificationSearchCode.value = code
    }

    fun runVerificationSearch() {
        val code = _verificationSearchCode.value
        if (code.isBlank()) return
        val res = repository.verifyTitleDeedOnline(code)
        _verificationResult.value = res
    }

    // Escrow transaction creation
    fun initiateEscrowDeposit(listing: ListingItem, amount: Long, method: String) {
        val profile = userProfile.value
        viewModelScope.launch {
            repository.createEscrowDeposit(
                listingId = listing.id,
                listingTitle = listing.title,
                buyerName = "${profile.name} (ገዥ)",
                sellerName = listing.posterName,
                amountBirr = amount,
                paymentMethod = method
            )
            _currentTab.value = AppTab.ESCROW_VERIFY
        }
    }

    fun advanceEscrowStatus(escrowId: String, currentStatus: EscrowStatus) {
        val nextStatus = when (currentStatus) {
            EscrowStatus.PENDING_DEPOSIT -> EscrowStatus.HELD_IN_ESCROW
            EscrowStatus.HELD_IN_ESCROW -> EscrowStatus.INSPECTION_CONFIRMED
            EscrowStatus.INSPECTION_CONFIRMED -> EscrowStatus.RELEASED_TO_SELLER
            EscrowStatus.RELEASED_TO_SELLER -> EscrowStatus.RELEASED_TO_SELLER
            EscrowStatus.REFUNDED -> EscrowStatus.REFUNDED
        }
        viewModelScope.launch {
            repository.updateEscrowStatus(escrowId, nextStatus)
        }
    }

    // User Profile switch role
    fun switchUserRole(role: UserRole) {
        val current = userProfile.value
        val updated = current.copy(role = role)
        viewModelScope.launch {
            repository.updateUserProfile(updated)
            // also adjust post listing default type if landlord -> rent, seller -> sale
            when (role) {
                UserRole.LANDLORD -> updatePostForm(type = ListingType.RENT)
                UserRole.SELLER -> updatePostForm(type = ListingType.SALE)
                else -> Unit
            }
        }
    }

    // Push Notification Actions
    fun fetchFcmToken() {
        val app = getApplication<Application>()
        try {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful && !task.result.isNullOrBlank()) {
                    val token = task.result
                    _fcmToken.value = token
                    com.example.notification.DelalaFirebaseMessagingService.saveTokenLocally(app, token)
                } else {
                    val fallback = com.example.notification.DelalaFirebaseMessagingService.getStoredToken(app)
                        ?: "fcm_delala_${System.currentTimeMillis() % 1000000}_client_token"
                    _fcmToken.value = fallback
                }
            }
        } catch (e: Exception) {
            val fallback = com.example.notification.DelalaFirebaseMessagingService.getStoredToken(app)
                ?: "fcm_delala_${System.currentTimeMillis() % 1000000}_client_token"
            _fcmToken.value = fallback
        }
    }

    fun triggerTestNotification(type: NotificationType) {
        val app = getApplication<Application>()
        val (title, body, channelId) = when (type) {
            NotificationType.CHAT_MESSAGE -> Triple(
                "ከዳዊት ታደሰ አዲስ መልእክት ደርሷል",
                "«ነገ ከሰዓት በኋላ ቦታውን መጥቼ ማየት እችላለሁ?»",
                com.example.notification.NotificationHelper.CHANNEL_MESSAGES
            )
            NotificationType.RENTAL_APPLICATION -> Triple(
                "የኪራይ ማመልከቻዎ ሁኔታ ተለውጧል",
                "አከራዩ የኪራይ ውል ስምምነቱን አጽድቀዋል፤ በአደራ ክፍያ ይቀጥሉ።",
                com.example.notification.NotificationHelper.CHANNEL_RENTAL_STATUS
            )
            NotificationType.PROPERTY_INTEREST -> Triple(
                "በለጠፉት ቶዮታ ራቭ4 ላይ አዲስ ፍላጎት አለ!",
                "አበበ ከበደ ዋጋ አቅርቧል፤ ማስታወቂያዎን ይጎብኙ።",
                com.example.notification.NotificationHelper.CHANNEL_PROPERTY_INTEREST
            )
            NotificationType.ESCROW_PAYMENT -> Triple(
                "የአደራ ክፍያ በቴሌብር (Telebirr) ገቢ ሆኗል",
                "150,000 ብር በአደራ ተይዟል፤ የይዞታ ፍተሻ ማረጋገጫ በመጠባበቅ ላይ ነው።",
                com.example.notification.NotificationHelper.CHANNEL_ESCROW
            )
        }

        // Show system notification
        com.example.notification.NotificationHelper.showSystemNotification(
            context = app,
            title = title,
            body = body,
            channelId = channelId
        )

        // Append to in-app notification list
        val item = PushNotificationItem(
            id = "notif_${System.currentTimeMillis()}",
            title = title,
            body = body,
            type = type,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        _notifications.value = listOf(item) + _notifications.value
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }
}
