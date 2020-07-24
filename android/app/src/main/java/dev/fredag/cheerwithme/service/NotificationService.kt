package dev.fredag.cheerwithme.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.RemoteMessage
import dev.fredag.cheerwithme.R

class NotificationService {
    fun showNotificationString(channelId: String, title: String, context: Context) {
        val notificationBuilder = NotificationCompat.Builder(
            context,
            channelId
        )
            .setSmallIcon(R.drawable.notification_icon_background)
            .setContentTitle(title)
            //.setContentText("Woop content")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, notificationBuilder.build())
        }
    }

    fun showNotification(channelId: String, notification: RemoteMessage.Notification, context: Context) {
        val notificationBuilder = NotificationCompat.Builder(
            context,
            channelId
        )
            //.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logo_gris))
            //.setSmallIcon(R.drawable.big_button_bg_round)
            .setSmallIcon(R.drawable.notification_icon_background)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            //.setAutoCancel(true)
            //.setSound(defaultSoundUri)
            //.setContentIntent(pendingIntent);
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        with(NotificationManagerCompat.from(context)) {
            notify(1, notificationBuilder.build())
        }
    }


    fun createNotificationChannel(channel_id: String, context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.default_notification_channel)
            val descriptionText = context.getString(R.string.default_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                channel_id,
                name,
                importance
            ).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}