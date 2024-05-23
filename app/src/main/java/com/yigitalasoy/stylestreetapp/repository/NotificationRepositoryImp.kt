package com.yigitalasoy.stylestreetapp.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.model.NotificationResponse
import com.yigitalasoy.stylestreetapp.util.Resource
import kotlinx.coroutines.tasks.await
import java.time.Instant

class NotificationRepositoryImp(val firebaseFirestore: FirebaseFirestore): NotificationRepository {
    override suspend fun getUserNotification(userId: String): Resource<ArrayList<NotificationResponse>> {

        var notificationList = ArrayList<NotificationResponse>()

        val docRef = firebaseFirestore.collection("tbl_Notification")
            .whereEqualTo("User_Id",userId).get().await()


        return try {
            docRef.documents.let {results->

                for (notification in results){
                    /*notificationList.add(NotificationResponse(
                        notification.data!!["Notification_Id"].toString(),
                        notification.data!!["User_Id"].toString(),
                        notification.data!!["Notification_Message"].toString(),
                        notification.data!!["Notification_Date"].toString(),
                        notification.data!!["It_Seen"].toString()
                    ))*/

                    notificationList.add(notification.toObject(NotificationResponse::class.java)!!)

                }
                return@let Resource.success(notificationList)
            }

        } catch (e: Exception){
            return Resource.error(e.message.toString(),null)
        }

    }

    override suspend fun changeNotificationSeen(
        notificationId: String,
        itSeen: String
    ): Resource<Boolean> {
        lateinit var docRef: Task<Void>
        if(itSeen == "0"){
            docRef = firebaseFirestore.collection("tbl_Notification").document(notificationId).update("It_Seen","1")
        } else if(itSeen == "1"){
            docRef = firebaseFirestore.collection("tbl_Notification").document(notificationId).update("It_Seen","0")
        }
        docRef.await()
        if(docRef.isSuccessful){
            return Resource.success(true)
        } else {
            return Resource.error(docRef.exception.toString(),null)
        }

    }

    override suspend fun addNotification(userId: String, message: String): Boolean {



        var docRef = firebaseFirestore.collection("tbl_Notification")

        val id = docRef.document().id

        val state = docRef.document(id).set(NotificationResponse(
            id,
            userId,
            notificationMessage = message,
            notificationDate = Instant.now().epochSecond.toString(),
            ItSeen = "0"
        ))

        state.await()

        if(state.isSuccessful){
            return true
        } else {
            return false
        }
    }


}