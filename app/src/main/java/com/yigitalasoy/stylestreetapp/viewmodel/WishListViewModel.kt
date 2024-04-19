package com.yigitalasoy.stylestreetapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitalasoy.stylestreetapp.model.WishListResponse
import com.yigitalasoy.stylestreetapp.repository.WishListRepository
import com.yigitalasoy.stylestreetapp.util.Resource
import com.yigitalasoy.stylestreetapp.util.Status
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
    //val wishListLoading = MutableLiveData<Resource<Boolean>>()
    //val wishListError = MutableLiveData<Resource<Boolean>>()

    private var job : Job? = null


    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        wishListLiveData.value = Resource.error(throwable.localizedMessage ?: "error!",null)
    }


    fun getWishList(userId: String){
        wishListLiveData.value = Resource.loading(null)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = wishListRepository.getWishList(userId)
            withContext(Dispatchers.Main){
                when(response.status){
                    Status.SUCCESS -> {
                        //println("wish list geldi: ${response.data}")
                        wishListLiveData.value = Resource.success(response.data)
                    }
                    Status.ERROR -> {
                        wishListLiveData.value = Resource.error(response.message.toString(),null)
                    }
                    Status.LOADING -> {
                        wishListLiveData.value = Resource.loading(null)
                    }
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
                    wishListLiveData.value = Resource.error(response["newId"] as String,null)
                }
            }
        }
    }

    fun removeProductToWishList(wishId: String){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = wishListRepository.removeProductToWishList(wishId)

            withContext(Dispatchers.Main){
                if(response){
                    wishListLiveData.value?.data?.remove(wishListLiveData.value?.data?.find { it.wish_Id == wishId })
                    wishListLiveData.value = Resource.success(wishListLiveData.value?.data)
                } else {
                    wishListLiveData.value = Resource.error("ERROR TO WISH LIST",null)
                }
            }
        }
    }

}