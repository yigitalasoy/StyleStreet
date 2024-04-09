package com.yigitalasoy.stylestreetapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitalasoy.stylestreetapp.model.BasketResponse
import com.yigitalasoy.stylestreetapp.model.ProductResponse
import com.yigitalasoy.stylestreetapp.model.SubProductResponse
import com.yigitalasoy.stylestreetapp.repository.BasketRepository
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
class BasketViewModel @Inject constructor(val basketRepository: BasketRepository): ViewModel() {

    val basketLiveData = MutableLiveData<Resource<BasketResponse>>()
    val basketSubProductsLiveData = MutableLiveData<Resource<ArrayList<SubProductResponse>>>()
    val basketLoading = MutableLiveData<Resource<Boolean>>()
    val basketError = MutableLiveData<Resource<Boolean>>()


    private var job : Job? = null
    private var productQuantityTask = false
    private var productRemoveTask = false

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        basketError.value = Resource.error(throwable.message.toString(),true)
    }

    fun getBasketData(userId: String){

        basketLoading.value = Resource.loading(true)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = basketRepository.getUserBasket(userId)
            withContext(Dispatchers.Main){
                if (response.data != null) {
                    basketLoading.value = Resource.loading(false)
                    basketLiveData.value = Resource.success(response.data)
                } else {
                    basketError.value = Resource.error(response.message.toString(), true)
                }
            }
        }
    }

    fun getBasketProducts(products: ArrayList<ProductResponse>){

        basketLoading.value = Resource.loading(true)

        products.let {

            val productsYeni = basketLiveData.value?.data!!.basketProducts!!
            val subproductsYeni = ArrayList<SubProductResponse>()
            val productByIdMap = products.associateBy { it.productId }

            for (urun in productsYeni){
                val z = productByIdMap[urun.productId.toString()]?.allProducts?.find { it.subProductId == urun.subProductId }
                z?.let {
                    subproductsYeni.add(z)
                }
            }

            if(subproductsYeni.size != 0){
                basketLoading.value = Resource.loading(false)
                basketSubProductsLiveData.value = Resource.success(subproductsYeni)
            }
        }
    }

    fun basketProductChangeQuantity(userId: String,subProductId: String, type: String, position: Int){

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if(!productQuantityTask){
                productQuantityTask = true
                var state = basketRepository.productQuantityChange(userId,subProductId,type,position)

                withContext(Dispatchers.Main){

                    var newLiveData = MutableLiveData<Resource<BasketResponse>>()

                    newLiveData.value = basketLiveData.value

                    if(state && type.equals("increase")){
                        newLiveData.value?.data?.basketProducts?.find { it.subProductId == subProductId }?.quantity = (newLiveData.value?.data?.basketProducts?.find { it.subProductId == subProductId }?.quantity!!) + 1
                        basketLiveData.value = Resource.success(newLiveData.value!!.data)
                    } else if(state && type.equals("decrease")){
                        newLiveData.value?.data?.basketProducts?.find { it.subProductId == subProductId }?.quantity = (newLiveData.value?.data?.basketProducts?.find { it.subProductId == subProductId }?.quantity!!) - 1
                        basketLiveData.value = Resource.success(newLiveData.value!!.data)
                    }

                    productQuantityTask = false
                }
            } else {
                println("quantity change task devam ettiği için girilmedi!")
            }

        }
    }

    fun basketRemoveProduct(userId: String,subProductId: String, type: String, position: Int){

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if(!productRemoveTask){
                productRemoveTask=true

                var state = basketRepository.productRemove(userId,subProductId,type,position)

                withContext(Dispatchers.Main){

                    var newLiveData = MutableLiveData<Resource<BasketResponse>>()

                    newLiveData.value = basketLiveData.value
                    if(state){
                        newLiveData.value?.data?.basketProducts?.removeAt(position)
                        basketLiveData.value = Resource.success(newLiveData.value!!.data)
                    }

                    productRemoveTask = false
                }

            } else {
                println("remove task devam ettiği için girilmedi !")
            }
        }

    }

}