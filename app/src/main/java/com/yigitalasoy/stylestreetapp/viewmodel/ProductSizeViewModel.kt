package com.yigitalasoy.stylestreetapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitalasoy.stylestreetapp.model.ProductSizeResponse
import com.yigitalasoy.stylestreetapp.repository.ProductSizeRepository
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
class ProductSizeViewModel @Inject constructor(val productSizeRepository: ProductSizeRepository): ViewModel() {

    val productSizeLiveData = MutableLiveData<Resource<ArrayList<ProductSizeResponse>>>()

    private var job : Job? = null

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        productSizeLiveData.value = Resource.error(throwable.message.toString(),null)
    }

    fun getAllProductSize(){

        productSizeLiveData.value = Resource.loading(null)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = productSizeRepository.getAllProductSize()

            withContext(Dispatchers.Main){
                when(response.status){
                    Status.SUCCESS -> {
                        if(response.data != null){
                            productSizeLiveData.value = response
                        }
                    }
                    Status.ERROR -> {
                        productSizeLiveData.value = Resource.error(response.message.toString(),null)
                    }
                    Status.LOADING -> {
                        productSizeLiveData.value = Resource.loading(null)
                    }
                }
            }
        }
    }

}