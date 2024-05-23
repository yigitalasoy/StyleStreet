package com.yigitalasoy.stylestreetapp.repository

import com.yigitalasoy.stylestreetapp.model.NotificationResponse
import com.yigitalasoy.stylestreetapp.util.Resource

interface NotificationRepository {

    suspend fun getUserNotification(userId: String): Resource<ArrayList<NotificationResponse>>

    suspend fun changeNotificationSeen(notificationId: String, itSeen: String): Resource<Boolean>

    suspend fun addNotification(userId: String,message: String): Boolean


}