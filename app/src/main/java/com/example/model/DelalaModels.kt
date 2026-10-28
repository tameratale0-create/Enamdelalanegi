package com.example.model

enum class UserRole(val amharicName: String, val englishName: String, val defaultAction: String) {
    LANDLORD("አከራይ", "Landlord / Lessor", "ቤት ወይም ሱቅ አከራይ"),
    SELLER("ሻጭ", "Seller", "ንብረት ወይም እቃ ሽጥ"),
    BUYER("ገዥ", "Buyer", "ግዥ ፈልግ"),
    TENANT("ተከራይ", "Tenant / Renter", "ኪራይ ፈልግ"),
    BROKER("ደላላ (ህጋዊ ወኪል)", "Licensed Broker", "ማገናኘትና ማስተዳደር")
}

enum class ListingCategory(val amharicName: String, val englishName: String) {
    ALL("ሁሉም", "All"),
    HOUSE("ቤቶች እና አፓርታማ", "Houses & Condos"),
    VEHICLE("መኪና እና ተሽከርካሪ", "Vehicles & Cars"),
    COMMERCIAL("ሱቅ እና ንግድ ቦታ", "Commercial & Shops"),
    LAND("ቦታ እና መሬት", "Plots & Land"),
    MACHINERY("ማሽነሪ እና እቃዎች", "Machinery & Equipment")
}

enum class ListingType(val amharicName: String, val englishName: String) {
    ALL("ሁሉም", "All"),
    RENT("ኪራይ", "Rent"),
    SALE("ሽያጭ", "Sale")
}

enum class VerificationStatus(val amharicName: String, val englishName: String, val badgeColor: Long) {
    VERIFIED_TITLE_DEED("የተረጋገጠ ካርታ", "Verified Carta", 0xFF0D5C3A),
    VERIFIED_LIBRE("የተረጋገጠ ሊብሬ", "Verified Libre", 0xFF0284C7),
    PENDING_REVIEW("በማረጋገጥ ላይ", "Under Review", 0xFFD97706),
    UNVERIFIED("ያልተረጋገጠ", "Unverified", 0xFF64748B)
}

enum class EscrowStatus(val amharicName: String, val englishName: String, val stepIndex: Int) {
    PENDING_DEPOSIT("ክፍያ በመጠባበቅ ላይ", "Awaiting Deposit", 0),
    HELD_IN_ESCROW("በአደራ ተይዟል (አስተማማኝ)", "Held in Trust Escrow", 1),
    INSPECTION_CONFIRMED("ፍተሻና ይዞታ ጸድቋል", "Inspection Approved", 2),
    RELEASED_TO_SELLER("ገንዘቡ ለሻጭ ተለቋል", "Funds Released", 3),
    REFUNDED("ተመላሽ ተደርጓል", "Refunded", -1)
}

data class UserProfile(
    val id: String = "user_default_1",
    val name: String = "አበበ ከበደ (Abebe Kebede)",
    val phone: String = "+251 911 234 567",
    val role: UserRole = UserRole.SELLER,
    val isVerified: Boolean = true,
    val subCity: String = "ቦሌ (Bole)",
    val nationalId: String = "ET-ID-892401",
    val rating: Float = 4.9f,
    val totalTransactions: Int = 18
)

data class ListingItem(
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: ListingCategory,
    val type: ListingType,
    val priceBirr: Long,
    val pricePeriod: String? = null, // e.g. "በወር" for rent
    val isNegotiable: Boolean = true,
    val location: String,
    val subCity: String,
    val latitude: Double = 9.0100,
    val longitude: Double = 38.7600,
    val areaSqM: Double? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val vehicleYear: Int? = null,
    val vehicleTransmission: String? = null,
    val titleDeedNumber: String,
    val verificationStatus: VerificationStatus,
    val posterName: String,
    val posterPhone: String,
    val posterRole: UserRole,
    val isOfflineCached: Boolean = true,
    val isPromoted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val iconKey: String = "house",
    val imageUrl: String? = null
)

data class ChatMessage(
    val id: Long = 0,
    val listingId: Long,
    val listingTitle: String,
    val senderName: String,
    val messageText: String? = null,
    val isVoiceMessage: Boolean = false,
    val voiceDurationSeconds: Int = 0,
    val voiceAmplitudes: List<Float> = emptyList(),
    val isFromMe: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

data class EscrowPayment(
    val id: String,
    val listingId: Long,
    val listingTitle: String,
    val buyerName: String,
    val sellerName: String,
    val amountBirr: Long,
    val paymentMethod: String, // "Telebirr", "CBE Birr", "Awash"
    val status: EscrowStatus,
    val transactionRef: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class MarketValuationQuery(
    val category: ListingCategory,
    val subCity: String,
    val areaSqM: Double,
    val condition: String, // "አዲስ / Brand New", "ጥሩ ይዞታ / Good", "እድሳት የሚፈልግ / Needs Renovation"
    val yearBuiltOrModel: Int? = null
)

data class MarketValuationResult(
    val estimatedMinPrice: Long,
    val estimatedAvgPrice: Long,
    val estimatedMaxPrice: Long,
    val pricePerSqM: Long,
    val trendAmharic: String,
    val confidenceScore: Int,
    val similarListingsCount: Int
)

enum class NotificationType(val amharicName: String, val englishName: String) {
    CHAT_MESSAGE("አዲስ መልእክት", "New Message"),
    RENTAL_APPLICATION("የኪራይ ማመልከቻ", "Rental Application"),
    PROPERTY_INTEREST("የንብረት ፍላጎት", "Property Lead"),
    ESCROW_PAYMENT("አደራ ክፍያ", "Escrow Payment")
}

data class PushNotificationItem(
    val id: String = "notif_${System.currentTimeMillis()}",
    val title: String,
    val body: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionData: String? = null
)
