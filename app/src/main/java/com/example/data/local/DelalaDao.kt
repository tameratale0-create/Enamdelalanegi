package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {
    @Query("SELECT * FROM listings ORDER BY isPromoted DESC, createdAt DESC")
    fun getAllListings(): Flow<List<ListingEntity>>

    @Query("SELECT * FROM listings WHERE id = :id LIMIT 1")
    suspend fun getListingById(id: Long): ListingEntity?

    @Query("SELECT * FROM listings WHERE category = :category ORDER BY createdAt DESC")
    fun getListingsByCategory(category: String): Flow<List<ListingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: ListingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listings: List<ListingEntity>)

    @Query("DELETE FROM listings WHERE id = :id")
    suspend fun deleteListing(id: Long)

    @Query("SELECT COUNT(*) FROM listings")
    suspend fun getListingsCount(): Int
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE listingId = :listingId ORDER BY timestamp ASC")
    fun getMessagesForListing(listingId: Long): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllRecentMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long
}

@Dao
interface EscrowDao {
    @Query("SELECT * FROM escrow_transactions ORDER BY timestamp DESC")
    fun getAllEscrows(): Flow<List<EscrowEntity>>

    @Query("SELECT * FROM escrow_transactions WHERE id = :id LIMIT 1")
    suspend fun getEscrowById(id: String): EscrowEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEscrow(escrow: EscrowEntity)

    @Query("UPDATE escrow_transactions SET status = :newStatus WHERE id = :id")
    suspend fun updateEscrowStatus(id: String, newStatus: String)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    fun getUserProfile(id: String): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)
}
