package dev.fredag.cheerwithme

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dev.fredag.cheerwithme.service.NotificationService
import org.koin.android.ext.android.get

class FirebaseNotificationReceiver : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val TAG = "FirebaseNotification"
        val context: Context = get()
        val notificationService: NotificationService = get()
        Log.d("FirebaseNotification", remoteMessage.notification.toString())
        notificationService.showNotification("cheer_with_me", remoteMessage.notification!!, get())

        //notificationService.showNotificationString("cheer_with_me", remoteMessage.data["message"]!!, get())
        //remoteMessage.getFrom()?.let { notificationService.showNotificationString("cheer_with_me", it, context) }
        Log.d(TAG, "did we get contect? $context")

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.

            } else {
                // Handle message within 10 seconds
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification?.body);
        }
    }

    override fun onNewToken(token: String) {
        val TAG = "FirebaseNotificationReceiver"
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }


}
