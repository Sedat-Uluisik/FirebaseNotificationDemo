package com.sedat.firebasenotificationdemo.service

import com.sedat.firebasenotificationdemo.constants.Constants.Companion.CONTENT_TYPE
import com.sedat.firebasenotificationdemo.constants.Constants.Companion.SERVER_KEY
import com.sedat.firebasenotificationdemo.model.Group
import com.sedat.firebasenotificationdemo.model.Message
import com.sedat.firebasenotificationdemo.model.NotificationKey
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface NotificationService {

    @Headers("Authorization:key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("/fcm/send")
    suspend fun postNotificationToTopic(
        @Body message: Message
    ):Response<ResponseBody>

    @POST("/fcm/notification")
    suspend fun createGroup(
        @Header("project_id") projectID: String,
        @Body group: Group,
        @Header("Authorization") key: String = "key=$SERVER_KEY",
        @Header("Content-Type") content_type: String = CONTENT_TYPE,
    ):Response<NotificationKey>

    @GET("/fcm/notification")
    suspend fun getGroupID(
        @Header("project_id") projectID: String,
        @Query("notification_key_name") groupName: String,
        @Header("Authorization") key: String = "key=$SERVER_KEY",
        @Header("Content-Type") content_type: String = CONTENT_TYPE,
    ):Response<NotificationKey>

    @POST("/fcm/notification")
    suspend fun addUserToGroup(
        @Body group: Group,
        @Header("project_id") projectID: String,
        @Header("Authorization") key: String = "key=$SERVER_KEY",
        @Header("Content-Type") content_type: String = CONTENT_TYPE,
    ):Response<NotificationKey>

    @POST("/fcm/send")
    suspend fun postNotificationToDeviceGroup(
        @Body message: Message,
        @Header("Authorization") key: String = "key=$SERVER_KEY",
        @Header("Content-Type") content_type: String = CONTENT_TYPE,
    ):Response<ResponseBody>
}