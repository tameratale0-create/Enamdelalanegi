package com.example.notification

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class DelalaFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Registration Token: $token")
        saveTokenLocally(applicationContext, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        val data = remoteMessage.data
        val type = data["type"] ?: "message"
        val title = remoteMessage.notification?.title ?: data["title"] ?: "ደላላ ነኝ ማሳወቂያ"
        val body = remoteMessage.notification?.body ?: data["body"] ?: "አዲስ መረጃ ደርሶዎታል"

        val channelId = when (type.lowercase()) {
            "rental_status", "rental" -> NotificationHelper.CHANNEL_RENTAL_STATUS
            "property_interest", "interest", "lead" -> NotificationHelper.CHANNEL_PROPERTY_INTEREST
            "escrow", "payment" -> NotificationHelper.CHANNEL_ESCROW
            else -> NotificationHelper.CHANNEL_MESSAGES
        }

        NotificationHelper.showSystemNotification(
            context = applicationContext,
            title = title,
            body = body,
            channelId = channelId
        )
    }

    companion object {
        private const val TAG = "DelalaFCMService"
        private const val PREFS_NAME = "delala_fcm_prefs"
        private const val KEY_FCM_TOKEN = "fcm_token"

        fun saveTokenLocally(context: Context, token: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
        }

        fun getStoredToken(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_FCM_TOKEN, null)
        }
    }
}
