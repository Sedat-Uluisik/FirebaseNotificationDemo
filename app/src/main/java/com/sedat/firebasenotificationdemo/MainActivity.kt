package com.sedat.firebasenotificationdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.sedat.firebasenotificationdemo.databinding.ActivityMainBinding
import com.sedat.firebasenotificationdemo.model.Group
import com.sedat.firebasenotificationdemo.model.Message
import com.sedat.firebasenotificationdemo.model.Notification
import com.sedat.firebasenotificationdemo.service.RetrofitObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var deviceToken: String = ""
    private var groupID: String = "" //notification_key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(!it.isSuccessful){
                return@addOnCompleteListener
            }

            val token = it.result
            deviceToken = token
            println(token)
            binding.tokenCreate.setText(token)
        }

        binding.btnSelectNotifType.setOnClickListener {
            val notificationTypeIntent = Intent(this@MainActivity, NotificationTypeActivity::class.java)
            startActivity(notificationTypeIntent)
        }

        binding.btnSubscribe.setOnClickListener {
            subscribeToTopic()
        }
        binding.btnUnsubscribe.setOnClickListener {
            unsubscribeToTopic()
        }

        binding.btnSendNotification.setOnClickListener {
            val title = binding.titleSend.text.toString()
            val message = binding.messageSend.text.toString()
            if(title.isNotEmpty() && message.isNotEmpty()){
                val notification = Notification(
                    title,
                    message
                )
                val data = Message("/topics/News", notification)
                postNotification(data)
            }
        }

        binding.btnCreateGroup.setOnClickListener {
            val groupName = binding.groupNameCreate.text.toString()
            if(groupName.isNotEmpty() && deviceToken.isNotEmpty())
                createGroup(groupName)
        }

        binding.btnGetGroupId.setOnClickListener {
            getGroupID(binding.groupIdTextview, binding.groupNameAddEdittext.text.toString())
        }

        binding.btnAddUserToGroup.setOnClickListener {
            addUserToGroupWithToken()
        }

        binding.btnSendNotificationToDeviceGroup.setOnClickListener {
            postNotificationToDeviceGroup()
        }

        binding.btnGetGroupIdDevice.setOnClickListener {
            getGroupID(binding.groupIdDeviceGroupTextview, binding.groupNameDeviceGroupEdittext.text.toString())
        }
    }

    private fun subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("News").addOnCompleteListener { task->
            if(!task.isSuccessful){
                Toast.makeText(this, "Subscription operation failed", Toast.LENGTH_LONG).show()
                return@addOnCompleteListener
            }

            Toast.makeText(this, "Subscribed to News", Toast.LENGTH_LONG).show()
        }
    }

    private fun unsubscribeToTopic(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic("News").addOnCompleteListener { task->
            if(!task.isSuccessful){
                Toast.makeText(this, "Unsubscription operation is failed", Toast.LENGTH_LONG).show()
                return@addOnCompleteListener
            }

            Toast.makeText(this, "Unsubscribe is successful", Toast.LENGTH_LONG).show()
        }
    }

    private fun postNotification(message: Message) = CoroutineScope(Dispatchers.IO).launch{
        try {
            val response = RetrofitObject.api.postNotificationToTopic(message)
            if(response.isSuccessful){
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext, "Notification sent", Toast.LENGTH_LONG).show()
                }
            }
            else{
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext, "Failed to send notification", Toast.LENGTH_LONG).show()
                }
            }
        }catch (e: IOException){
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createGroup(groupName: String) = CoroutineScope(Dispatchers.IO).launch{
        if(deviceToken.isNotEmpty()){
            val projectID = FirebaseApp.getInstance().options.gcmSenderId

            val group = Group(
                "create",
                groupName,
                listOf(deviceToken),
                null,
            )

            val response = RetrofitObject.api.createGroup(projectID.toString(), group)

            if(response.isSuccessful){
                println("Group ID -> " + response.body())
            }else{
                //aynı grup adından bir tane daha oluşturulursa hata verir.
                println("Error!${response.errorBody()}")
            }
        }
    }

    private fun getGroupID(textView: TextView, groupName: String) = CoroutineScope(Dispatchers.IO).launch{
        val projectID = FirebaseApp.getInstance().options.gcmSenderId.toString()

       if(groupName.isNotEmpty()){
           val response = RetrofitObject.api.getGroupID(
               projectID,
               groupName
           )

           if(response.isSuccessful){
               Handler(Looper.getMainLooper()).post {
                   response.body()?.let {
                       groupID = it.notification_key
                      textView.text = "Group ID: ${it.notification_key}"
                   }
               }
           }
       }else{
           Handler(Looper.getMainLooper()).post {
               Toast.makeText(applicationContext, "Please enter group name", Toast.LENGTH_LONG).show()
           }
       }
    }

    private fun addUserToGroupWithToken()=CoroutineScope(Dispatchers.IO).launch {
        val projectID = FirebaseApp.getInstance().options.gcmSenderId.toString()
        val groupName = binding.groupNameAddEdittext.text.toString()
        val userToken = binding.tokenAddEdittext.text.toString()

        if(userToken.isNotEmpty() && groupName.isNotEmpty() && projectID.isNotEmpty() && groupID.isNotEmpty()){
            val group = Group(
                "add",
                groupName,
                listOf(userToken),
                groupID,
            )

            val response = RetrofitObject.api.addUserToGroup(
                group,
                projectID
            )

            if(response.isSuccessful){
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext, "User added", Toast.LENGTH_LONG).show()
                }
            }else{
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext, "Failed", Toast.LENGTH_LONG).show()
                }
            }
        }else{
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(applicationContext, "Error!!!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun postNotificationToDeviceGroup()=CoroutineScope(Dispatchers.IO).launch {
        val title = binding.titleSendDeviceGroup.text.toString()
        val body = binding.messageSendDeviceGroup.text.toString()

        if(title.isNotEmpty() && body.isNotEmpty() && groupID.isNotEmpty()){
            val notification = Notification(
                title,
                body
            )
            val message = Message(
                groupID,
                notification
            )

            val response = RetrofitObject.api.postNotificationToDeviceGroup(message)

            if(response.isSuccessful){
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext, "Message send", Toast.LENGTH_LONG).show()
                }
            }else{
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext, "Error!!!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}