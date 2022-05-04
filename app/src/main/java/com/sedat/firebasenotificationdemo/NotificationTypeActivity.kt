package com.sedat.firebasenotificationdemo

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.sedat.firebasenotificationdemo.databinding.ActivityNotificationTypeBinding
import com.sedat.firebasenotificationdemo.service.MyBroadCastReceiver

class NotificationTypeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationTypeBinding
    private var type = 0

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnShowNotification.setOnClickListener {
            if(type != 0){
                val intent = Intent(this, MyBroadCastReceiver::class.java)
                intent.putExtra("notif_title", "Notification Title")
                intent.putExtra("notif_message", "Notification Message :)")
                intent.putExtra("notif_type", type)
                val pendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 12, intent, PendingIntent.FLAG_ONE_SHOT)

                pendingIntent.send()
            }
        }
        binding.radioGroupForNotificationType.setOnCheckedChangeListener{ radioGroup, id ->
            when(id){
                R.id.radio_basic -> type = 1
                R.id.radio_action_button -> type = 2
                R.id.radio_reply_button -> type = 3
                R.id.radio_expandable_image -> type = 4
                R.id.radio_expandable_text -> type = 5
                R.id.radio_conversation -> type = 6
            }
        }

        val message = getMessageForReplyButtonNotification()

        if(!message.isNullOrEmpty() && message != null && message != ""){
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            repliedNotification()
        }
    }

    private fun getMessageForReplyButtonNotification(): CharSequence?{

        val intent = intent
        return RemoteInput.getResultsFromIntent(intent)?.getCharSequence("key_text_reply")
    }

    //update notification for reply
    private fun repliedNotification(){
        val channelID = "channel_id"
        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.circle_notifications_24)
            .setContentText("Replied")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setAllowSystemGeneratedContextualActions(false)

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
}