package com.yigitalasoy.stylestreetapp.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationResponse(
    @get:PropertyName("Notification_Id") @set:PropertyName("Notification_Id") var notificationId: String? = "",
    @get:PropertyName("User_Id") @set:PropertyName("User_Id") var userId: String? = "",
    @get:PropertyName("Notification_Message") @set:PropertyName("Notification_Message") var notificationMessage: String? = "",
    @get:PropertyName("Notification_Date") @set:PropertyName("Notification_Date") var notificationDate: String? = "",
    @get:PropertyName("It_Seen") @set:PropertyName("It_Seen") var ItSeen: String? = ""
): Parcelable
