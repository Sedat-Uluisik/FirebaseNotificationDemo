package com.sedat.firebasenotificationdemo.model

data class Group(
    val operation: String,
    val notification_key_name: String,
    val registration_ids: List<String>,
    val notification_key: String ?= null
)