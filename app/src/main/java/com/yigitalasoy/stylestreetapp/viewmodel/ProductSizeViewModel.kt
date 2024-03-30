package com.yigitalasoy.stylestreetapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitalasoy.stylestreetapp.model.ProductSizeResponse
import com.yigitalasoy.stylestreetapp.repository.ProductSizeRepository
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
class ProductSizeViewModel @Inject constructor(val productSizeRepository: ProductSizeRepository): ViewModel() {

    val productSizeLiveData = MutableLiveData<Resource<ArrayList<ProductSizeResponse>>>()
    val productSizeLoading = MutableLiveData<Resource<Boolean>>()
    val productSizeError = MutableLiveData<Resource<Boolean>>()

    private var job : Job? = null

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        productSizeError.value = Resource.error(throwable.message.toString(),true)
    }

    fun getAllProductSize(){

        productSizeLoading.value = Resource.loading(true)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val response = productSizeRepository.getAllProductSize()

            withContext(Dispatchers.Main){
                if(response.data != null){
                    productSizeLiveData.value = response
                    productSizeLoading.value = Resource.loading(false)
                } else {
                    response.message?.let {
                        productSizeError.value = Resource.error(it,true)
                    }
                }
            }
        }
    }

}