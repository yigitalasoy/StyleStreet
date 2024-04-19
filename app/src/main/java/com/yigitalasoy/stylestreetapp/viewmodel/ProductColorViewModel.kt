package com.yigitalasoy.stylestreetapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitalasoy.stylestreetapp.model.ProductColorResponse
import com.yigitalasoy.stylestreetapp.repository.ProductColorRepository
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
class ProductColorViewModel @Inject constructor(val productColorRepository: ProductColorRepository): ViewModel() {

    val productColorLiveData = MutableLiveData<Resource<ArrayList<ProductColorResponse>>>()
    //val productColorError = MutableLiveData<Resource<Boolean>>()
    //val productColorLoading = MutableLiveData<Resource<Boolean>>()

    private var job : Job? = null


    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        productColorLiveData.value = Resource.error(throwable.message.toString(),null)
    }

    fun getAllProductColors(){

        println("PRODUCT COLOR VERİSİ ALINACAK. LOADİNG....")

        productColorLiveData.value = Resource.loading(null)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val response = productColorRepository.getAllProductColor()

            withContext(Dispatchers.Main){
                when(response.status){
                    Status.SUCCESS -> {
                        productColorLiveData.value = response
                    }
                    Status.ERROR -> {
                        productColorLiveData.value = Resource.error(response.message.toString(),null)
                    }
                    Status.LOADING -> {
                        productColorLiveData.value = Resource.loading(null)
                    }
                }
            }
        }
    }




}