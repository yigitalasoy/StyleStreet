package com.yigitalasoy.stylestreetapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitalasoy.stylestreetapp.model.BasketDetailResponse
import com.yigitalasoy.stylestreetapp.model.SoldDetailResponse
import com.yigitalasoy.stylestreetapp.model.SoldResponse
import com.yigitalasoy.stylestreetapp.repository.SoldRepository
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
class SoldViewModel @Inject constructor(val soldRepository: SoldRepository): ViewModel() {

    var soldLiveData = MutableLiveData<Resource<ArrayList<SoldResponse>>>()
    var soldDetailLiveData = MutableLiveData<Resource<ArrayList<SoldDetailResponse>>>()
    var addSoldState = MutableLiveData<Resource<Boolean>>()


    private var job : Job? = null


    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        soldLiveData.value = Resource.error(throwable.localizedMessage ?: "error!",data = null)
        soldDetailLiveData.value = Resource.error(throwable.localizedMessage ?: "error!",data = null)
    }

    fun getUserSold(userId: String){

        soldLiveData.value = Resource.loading(null)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = soldRepository.getUserSold(userId)
            withContext(Dispatchers.Main){
                when(response.status){
                    Status.SUCCESS -> {
                        if(response.data != null){
                            soldLiveData.value = response
                        }
                    }
                    Status.ERROR -> {
                        soldLiveData.value = Resource.error(response.message.toString(),null)
                    }
                    Status.LOADING -> {
                        soldLiveData.value = Resource.loading(null)
                    }
                }
            }
        }

    }


    fun getUserSoldDetail(userId: String){

        soldDetailLiveData.value = Resource.loading(null)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = soldRepository.getUserSoldDetail(userId)
            withContext(Dispatchers.Main){
                when(response.status){
                    Status.SUCCESS -> {
                        if(response.data != null){
                            soldDetailLiveData.value = response
                        }
                    }
                    Status.ERROR -> {
                        soldDetailLiveData.value = Resource.error(response.message.toString(),null)
                    }
                    Status.LOADING -> {
                        soldDetailLiveData.value = Resource.loading(null)
                    }
                }
            }
        }

    }


    fun addUserSold(userId: String,soldResponse: SoldResponse,basketProducts: ArrayList<BasketDetailResponse>){

        addSoldState.value = Resource.loading(null)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = soldRepository.addUserSold(soldResponse)
            withContext(Dispatchers.Main){
                when(response.status){
                    Status.SUCCESS -> {
                        if(response.data != null){

                            val responseSoldDetail = soldRepository.addUserSoldDetail(userId,response.data.soldId!!,basketProducts)

                            when(responseSoldDetail.status){
                                Status.SUCCESS -> {
                                    if(responseSoldDetail.data != null){
                                        soldDetailLiveData.value?.data?.addAll(responseSoldDetail.data)
                                        soldDetailLiveData.value = Resource.success(soldDetailLiveData.value?.data)
                                    }
                                }
                                Status.ERROR -> {
                                    soldDetailLiveData.value = Resource.error(responseSoldDetail.message.toString(),null)
                                    addSoldState.value = Resource.success(false)
                                }
                                Status.LOADING -> {
                                    soldDetailLiveData.value = Resource.loading(null)
                                }
                            }

                            soldLiveData.value?.data?.add(response.data)
                            soldLiveData.value = Resource.success(soldLiveData.value?.data)
                            addSoldState.value = Resource.success(true)
                            println("sold live dataa: ${soldLiveData.value}")
                        }
                    }
                    Status.ERROR -> {
                        soldLiveData.value = Resource.error(response.message.toString(),null)
                    }
                    Status.LOADING -> {
                        soldLiveData.value = Resource.loading(null)
                    }
                }
            }
        }

    }


    /*fun addUserSoldDetail(soldId: String,soldDetailList: ArrayList<SoldDetailResponse>){

        soldDetailLiveData.value = Resource.loading(null)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = soldRepository.addUserSoldDetail(soldId,soldDetailList)
            withContext(Dispatchers.Main){
                when(response.status){
                    Status.SUCCESS -> {
                        if(response.data != null){
                            soldDetailLiveData.value?.data?.addAll(response.data)
                            soldDetailLiveData.value = Resource.success(soldDetailLiveData.value?.data)
                        }
                    }
                    Status.ERROR -> {
                        soldDetailLiveData.value = Resource.error(response.message.toString(),null)
                    }
                    Status.LOADING -> {
                        soldDetailLiveData.value = Resource.loading(null)
                    }
                }
            }
        }

    }*/



}