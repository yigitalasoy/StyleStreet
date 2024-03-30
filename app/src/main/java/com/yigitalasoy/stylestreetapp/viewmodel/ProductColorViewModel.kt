package com.yigitalasoy.stylestreetapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitalasoy.stylestreetapp.model.ProductColorResponse
import com.yigitalasoy.stylestreetapp.repository.ProductColorRepository
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
class ProductColorViewModel @Inject constructor(val productColorRepository: ProductColorRepository): ViewModel() {

    val productColorLiveData = MutableLiveData<Resource<ArrayList<ProductColorResponse>>>()
    val productColorError = MutableLiveData<Resource<Boolean>>()
    val productColorLoading = MutableLiveData<Resource<Boolean>>()

    private var job : Job? = null


    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        productColorError.value = Resource.error(throwable.message.toString(),true)
    }

    fun getAllProductColors(){

        println("PRODUCT COLOR VERİSİ ALINACAK. LOADİNG....")

        productColorLoading.value = Resource.loading(true)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val response = productColorRepository.getAllProductColor()

            withContext(Dispatchers.Main){
                if(response.data != null){
                    productColorLiveData.value = response
                    productColorLoading.value = Resource.loading(false)
                } else {
                    response.message?.let {
                        productColorError.value = Resource.error(it,true)
                    }
                }
            }
        }
    }




}