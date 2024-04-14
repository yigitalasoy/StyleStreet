package com.yigitalasoy.stylestreetapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitalasoy.stylestreetapp.model.WishListResponse
import com.yigitalasoy.stylestreetapp.repository.WishListRepository
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
class WishListViewModel @Inject constructor(var wishListRepository: WishListRepository): ViewModel() {
    val wishListLiveData = MutableLiveData<Resource<ArrayList<WishListResponse>>>()
    val wishListLoading = MutableLiveData<Resource<Boolean>>()
    val wishListError = MutableLiveData<Resource<Boolean>>()

    private var job : Job? = null


    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        wishListError.value = Resource.error(throwable.localizedMessage ?: "error!",data = true)
    }


    fun getWishList(userId: String){
        wishListLoading.value = Resource.success(true)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = wishListRepository.getWishList(userId)
            withContext(Dispatchers.Main){
                wishListLoading.value = Resource.success(false)

                if(response.data != null){
                    wishListLiveData.value = Resource.success(response.data)
                } else {
                    wishListError.value = Resource.error(response.message!!,true)
                }
            }
        }
    }


    fun addProductToWishList(userId: String, productId: String){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = wishListRepository.addProductToWishList(userId,productId)

            withContext(Dispatchers.Main){
                if(response["state"] as Boolean){
                    wishListLiveData.value?.data?.add(WishListResponse(
                        response["newId"] as String,
                        userId,
                        productId
                    ))
                    wishListLiveData.value = Resource.success(wishListLiveData.value?.data)
                } else {
                    wishListError.value = Resource.error(response["newId"] as String,true)
                }
            }
        }
    }

    fun removeProductToWishList(wishId: String){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = wishListRepository.removeProductToWishList(wishId)

            withContext(Dispatchers.Main){
                if(response){
                    wishListLiveData.value?.data?.remove(wishListLiveData.value?.data?.find { it.Wish_Id == wishId })
                    wishListLiveData.value = Resource.success(wishListLiveData.value?.data)
                } else {
                    wishListError.value = Resource.error("HATA",true)
                }
            }
        }
    }

}