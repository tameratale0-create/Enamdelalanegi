package com.example.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

object NotificationHelper {

    const val CHANNEL_MESSAGES = "channel_delala_messages"
    const val CHANNEL_RENTAL_STATUS = "channel_delala_rental_status"
    const val CHANNEL_PROPERTY_INTEREST = "channel_delala_property_interest"
    const val CHANNEL_ESCROW = "channel_delala_escrow"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val messageChannel = NotificationChannel(
                CHANNEL_MESSAGES,
                "አዲስ መልእክቶች (Chat Messages)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "ከአከራዮች እና ከሻጮች የሚመጡ አዳዲስ የጽሁፍና የድምጽ መልእክቶች"
                enableVibration(true)
            }

            val rentalChannel = NotificationChannel(
                CHANNEL_RENTAL_STATUS,
                "የኪራይ ማመልከቻ ሁኔታ (Rental Status)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "የኪራይ ውል እና የማመልከቻ ለውጦች ማሳወቂያ"
                enableVibration(true)
            }

            val interestChannel = NotificationChannel(
                CHANNEL_PROPERTY_INTEREST,
                "የንብረት ፍላጎት (Property Interest & Leads)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "በለጠፉት ንብረት ላይ አዳዲስ ገዥዎች ወይም ተከራዮች ፍላጎት ሲያሳዩ"
                enableVibration(true)
            }

            val escrowChannel = NotificationChannel(
                CHANNEL_ESCROW,
                "የአደራ ክፍያ (Trust Escrow)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "የቴሌብር እና የንግድ ባንክ አደራ ክፍያ ገቢና ወጪ ማሳወቂያዎች"
                enableVibration(true)
            }

            notificationManager.createNotificationChannels(
                listOf(messageChannel, rentalChannel, interestChannel, escrowChannel)
            )
        }
    }

    fun showSystemNotification(
        context: Context,
        title: String,
        body: String,
        channelId: String = CHANNEL_MESSAGES,
        notificationId: Int = (System.currentTimeMillis() % 100000).toInt(),
        intentAction: String? = null
    ) {
        initNotificationChannels(context)

        // Check POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intentAction?.let { action = it }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val iconRes = android.R.drawable.ic_dialog_info

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(iconRes)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Permission denied
        }
    }
}
