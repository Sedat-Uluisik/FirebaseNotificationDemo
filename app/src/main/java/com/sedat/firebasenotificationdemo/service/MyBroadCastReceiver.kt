package com.sedat.firebasenotificationdemo.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyBroadCastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val title: String = intent?.getStringExtra("notif_title").toString()
        val message: String = intent?.getStringExtra("notif_message").toString()
        val notificationType: Int = intent?.getIntExtra("notif_type", 0)!!

        val toastMessage = intent.getStringExtra("toast_message")

        val serviceIntent = Intent(context, ServiceFroNotificationType::class.java)
        serviceIntent.putExtra("notification_title", title)
        serviceIntent.putExtra("notification_message", message)
        serviceIntent.putExtra("notification_type", notificationType)
        serviceIntent.putExtra("showToast", toastMessage)
        context?.startService(serviceIntent)
    }
}