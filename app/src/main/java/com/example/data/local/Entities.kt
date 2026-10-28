package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listings")
data class ListingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val type: String,
    val priceBirr: Long,
    val pricePeriod: String?,
    val isNegotiable: Boolean,
    val location: String,
    val subCity: String,
    val latitude: Double = 9.0100,
    val longitude: Double = 38.7600,
    val areaSqM: Double?,
    val bedrooms: Int?,
    val bathrooms: Int?,
    val vehicleYear: Int?,
    val vehicleTransmission: String?,
    val titleDeedNumber: String,
    val verificationStatus: String,
    val posterName: String,
    val posterPhone: String,
    val posterRole: String,
    val isOfflineCached: Boolean = true,
    val isPromoted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val iconKey: String = "house",
    val imageUrl: String? = null
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listingId: Long,
    val listingTitle: String,
    val senderName: String,
    val messageText: String?,
    val isVoiceMessage: Boolean,
    val voiceDurationSeconds: Int,
    val voiceAmplitudes: String, // comma separated floats
    val isFromMe: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "escrow_transactions")
data class EscrowEntity(
    @PrimaryKey val id: String,
    val listingId: Long,
    val listingTitle: String,
    val buyerName: String,
    val sellerName: String,
    val amountBirr: Long,
    val paymentMethod: String,
    val status: String,
    val transactionRef: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val role: String,
    val isVerified: Boolean,
    val subCity: String,
    val nationalId: String,
    val rating: Float,
    val totalTransactions: Int
)
