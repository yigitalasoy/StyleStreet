package com.yigitalasoy.stylestreetapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitalasoy.stylestreetapp.model.NotificationResponse
import com.yigitalasoy.stylestreetapp.repository.NotificationRepository
import com.yigitalasoy.stylestreetapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(var notificationRepository: NotificationRepository): ViewModel() {
    val notificationLiveData = MutableLiveData<Resource<ArrayList<NotificationResponse>>>()
    val notificationLoading = MutableLiveData<Resource<Boolean>>()
    val notificationError = MutableLiveData<Resource<Boolean>>()

    private var job : Job? = null

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        notificationError.value = Resource.error(throwable.message.toString(),true)
    }

    fun getNotificationData(userId: String){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = notificationRepository.getUserNotification(userId)
            withContext(Dispatchers.Main){
                if (response.data != null) {
                    notificationLoading.value = Resource.loading(false)
                    notificationLiveData.value = Resource.success(response.data)
                } else {
                    notificationError.value = Resource.error(response.message.toString(), true)
                }
            }
        }
    }

    fun changeNotificationSeen(notificationId: String){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = notificationRepository.changeNotificationSeen(notificationId)
            println("gelen response: $response")
            withContext(Dispatchers.Main){
                if (response.data != null && response.data == true) {
                    notificationLoading.value = Resource.loading(false)
                    // val temp = notificationLiveData.value?.data
                    // temp?.find { it.notificationId == notificationId }?.ItSeen = "1"

                    notificationLiveData.value?.data?.find { it.notificationId == notificationId }?.ItSeen = "1"
                    notificationLiveData.value = Resource.success(notificationLiveData.value?.data)

                    println("bildirim live data: ${notificationLiveData.value?.data}")
                } else {
                    notificationError.value = Resource.error(response.message.toString(), true)
                }
            }
        }
    }

}