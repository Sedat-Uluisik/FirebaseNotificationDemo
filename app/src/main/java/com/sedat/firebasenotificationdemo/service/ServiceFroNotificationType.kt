package com.sedat.firebasenotificationdemo.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import com.sedat.firebasenotificationdemo.MainActivity
import com.sedat.firebasenotificationdemo.NotificationTypeActivity
import com.sedat.firebasenotificationdemo.R
import java.util.*

class ServiceFroNotificationType: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val title: String = intent?.getStringExtra("notification_title").toString()
        val message: String = intent?.getStringExtra("notification_message").toString()
        val notificationType: Int = intent?.getIntExtra("notification_type", 0)!!

        val toastMessage = intent.getStringExtra("showToast")

        when(notificationType){
            1 -> typeOne(title, message)
            2 -> typeTwo(title, message)
            3 -> typeThree(title, message)
            4 -> expandableNotificationWithLargeImage()
            5 -> expandableNotificationWithLargeText()
            6 -> conversationNotification()
        }

        if(!toastMessage.isNullOrEmpty() && toastMessage != null && toastMessage != "")
            showToast(toastMessage)

        return START_NOT_STICKY
    }

    //Basic notification
    private fun typeOne(title: String, message: String){
        val channelID = "channel_id"

        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.circle_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random().nextInt()

        val notificationClickIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 11, notificationClickIntent, PendingIntent.FLAG_IMMUTABLE)

        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(false)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                channelID,
                "channel_name",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableLights(true)
            }

            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(notificationID, builder.build())
        }else
            notificationManager.notify(notificationID, builder.build())
    }

    //With action button
    @SuppressLint("LaunchActivityFromNotification")
    private fun typeTwo(title: String, message: String){

        val channelID = "channel_id"

        val buttonIntent = Intent(this, MyBroadCastReceiver::class.java).apply {
            putExtra("toast_message", "Button Clicked")
        }
        val buttonPendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 11, buttonIntent, 0)

        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.circle_notifications_24)
            .setContentTitle(title)
            .setContentText(title)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(buttonPendingIntent)
            .addAction(R.drawable.message_24, "Message Button", buttonPendingIntent)
            .setAutoCancel(false)
            .setAllowSystemGeneratedContextualActions(false)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random().nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                channelID,
                "channel_name",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableLights(true)
            }

            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(notificationID, builder.build())
        }else
            notificationManager.notify(notificationID, builder.build())
    }

    private fun showToast(text: String){
        Toast.makeText(baseContext, text, Toast.LENGTH_LONG).show()
    }

    //Add a direct reply action
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun typeThree(title: String, message: String){

        val KEY_TEXT_REPLY = "key_text_reply"
        val replyLabel: String = "Reply Label"
        val requestCode = Random().nextInt()

        val remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
            setLabel(replyLabel)
            build()
        }

        val replyButtonIntent = Intent(this, NotificationTypeActivity::class.java)

        val replyPendingIntent: PendingIntent = PendingIntent.getActivity(this, requestCode, replyButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val action: NotificationCompat.Action = NotificationCompat.Action.Builder(
            R.drawable.message_24,
            "Reply",
            replyPendingIntent
        ).addRemoteInput(remoteInput)
            .build()

        val channelID = "channel_id"

        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.circle_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setAllowSystemGeneratedContextualActions(false)
            .addAction(action)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                channelID,
                "channel_name",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableLights(true)
            }

            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(99 , builder.build())
        }else
            notificationManager.notify(99, builder.build())
    }

    private fun expandableNotificationWithLargeImage(){
        val channelID = "expandable_image"

        val imageBitmap = BitmapFactory.decodeResource(this.resources, R.drawable.notification_image)

        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.circle_notifications_24)
            .setContentTitle("Expandable")
            .setContentText("Expandable notification text")
            .setLargeIcon(imageBitmap)
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(imageBitmap)
                .bigLargeIcon(null))
            .build()

        val notificationManager: NotificationManager =getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = kotlin.random.Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                channelID,
                "expandableImage",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(notificationID, builder)
        }else
            notificationManager.notify(notificationID, builder)
    }

    private fun expandableNotificationWithLargeText(){
        val channelID = "expandable_text"

        val imageBitmap = BitmapFactory.decodeResource(this.resources, R.drawable.notification_image)

        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.circle_notifications_24)
            .setContentTitle("Expandable notification text")
            .setContentText("Expandable notification text")
            .setLargeIcon(imageBitmap)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/**/*/*/*/*/*/*/*/**/*/*/*/*/*/*/*/*")
            )
            .build()

        val notificationManager: NotificationManager =getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = kotlin.random.Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                channelID,
                "expandableText",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(notificationID, builder)
        }else
            notificationManager.notify(notificationID, builder)
    }

    private fun conversationNotification(){
        val channelID = "conversation_notification"

        val sender = Person.Builder()
            .setName("you")
            .build()

        val sender2 = Person.Builder()
            .setName("kotlin")
            .setIcon(IconCompat.createWithResource(this, R.drawable.notification_image))
            .build()

        val message1 = NotificationCompat.MessagingStyle.Message("Do you want to see a movie tonight?", Date().time, sender2)
        val message2 = NotificationCompat.MessagingStyle.Message("Yeah, sounds great!", System.currentTimeMillis(), sender)

        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.circle_notifications_24)
            .setStyle(NotificationCompat.MessagingStyle(sender)
                .addMessage(message1)
                .addMessage(message2)
            )
            .build()

        val notificationManager: NotificationManager =getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = kotlin.random.Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                channelID,
                "conversationNotification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(notificationID, builder)
        }else
            notificationManager.notify(notificationID, builder)
    }
}