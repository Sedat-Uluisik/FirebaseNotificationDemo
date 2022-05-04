package com.sedat.firebasenotificationdemo.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sedat.firebasenotificationdemo.MainActivity
import com.sedat.firebasenotificationdemo.R
import java.util.*

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        message.notification?.let {
            showNotification(it.title.toString(), it.body.toString())
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        println("device token -> $token")
    }

    @SuppressLint("ResourceType", "UnspecifiedImmutableFlag")
    private fun showNotification(title: String, message: String){
        val channelID = "channel_id"
        val notificationID = Random().nextInt()

        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.circle_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setColor(0XAA0000)

        val notifClickIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(this, 10, notifClickIntent, PendingIntent.FLAG_ONE_SHOT)

        builder.setContentIntent(pendingIntent)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ //api 26 - android 8.0 ve üstü
            val channel = NotificationChannel(
                channelID,
                "Channel_Name",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification Description"
                lightColor = Color.RED
                enableLights(true)
            }

            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(notificationID, builder.build())
        }else{
            notificationManager.notify(notificationID, builder.build())
        }
    }
}