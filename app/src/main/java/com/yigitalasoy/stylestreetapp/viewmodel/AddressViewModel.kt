package com.yigitalasoy.stylestreetapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitalasoy.stylestreetapp.model.AddressResponse
import com.yigitalasoy.stylestreetapp.repository.AddressRepository
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
class AddressViewModel @Inject constructor(val addressRepository: AddressRepository): ViewModel() {

    val addressLiveData = MutableLiveData<Resource<ArrayList<AddressResponse>>>()
    val newAddressLiveData = MutableLiveData<Resource<AddressResponse>>()
    val removeState = MutableLiveData<Resource<Boolean>>()

    private var job : Job? = null


    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        addressLiveData.value = Resource.error(throwable.message.toString(),null)
        newAddressLiveData.value = Resource.error(throwable.message.toString(),null)
        removeState.value = Resource.error(throwable.message.toString(),null)
    }

    fun getAddressData(userId: String){
        addressLiveData.value = Resource.loading(null)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch{
            val response = addressRepository.getUserAddress(userId)

            withContext(Dispatchers.Main){

                when(response.status){
                    Status.SUCCESS -> {
                        if(response.data != null) {
                            addressLiveData.value = response
                        }
                    }
                    Status.ERROR -> {
                        addressLiveData.value = Resource.error(response.message.toString(),null)
                    }
                    Status.LOADING -> {
                        addressLiveData.value = Resource.loading(null)

                    }
                }
            }
        }
    }


    fun addAddress(newAddress: AddressResponse){
        println("add address çalıştı")
        newAddressLiveData.value = Resource.loading(null)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch{
            val response = addressRepository.addAddress(newAddress)

            withContext(Dispatchers.Main){
                when(response.status){
                    Status.SUCCESS -> {
                        if(response.data != null) {
                            newAddressLiveData.value = response
                        }
                    }
                    Status.ERROR -> {
                        newAddressLiveData.value = Resource.error(response.message.toString(),null)
                    }
                    Status.LOADING -> {
                        newAddressLiveData.value = Resource.loading(null)

                    }
                }
            }
        }
    }


    fun removeAddress(addressId: String){
        removeState.value = Resource.loading(null)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch{
            val response = addressRepository.removeAddress(addressId)

            withContext(Dispatchers.Main){
                when(response.status){
                    Status.SUCCESS -> {
                        if(response.data != null) {
                            removeState.value = Resource.success(true)
                        }
                    }
                    Status.ERROR -> {
                        removeState.value = Resource.error(response.message.toString(),null)
                    }
                    Status.LOADING -> {
                        removeState.value = Resource.loading(null)
                    }
                }
            }
        }
    }





}