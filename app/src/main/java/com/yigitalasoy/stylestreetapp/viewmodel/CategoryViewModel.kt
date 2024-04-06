package com.yigitalasoy.stylestreetapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitalasoy.stylestreetapp.model.CategoryResponse
import com.yigitalasoy.stylestreetapp.repository.CategoryRepository
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
class CategoryViewModel @Inject constructor(val categoryRepository: CategoryRepository): ViewModel() {

    val categoryLiveData = MutableLiveData<Resource<ArrayList<CategoryResponse>>>()
    val categoryError = MutableLiveData<Resource<Boolean>>()
    val categoryLoading = MutableLiveData<Resource<Boolean>>()

    private var job : Job? = null


    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        categoryError.value = Resource.error(throwable.message.toString(),true)
    }

    fun getAllCategories(){

        categoryLoading.value = Resource.loading(true)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val response = categoryRepository.getAllCategories()

            withContext(Dispatchers.Main){
                if(response.data != null){
                    categoryLiveData.value = response
                } else {
                    response.message?.let {
                        categoryError.value = Resource.error(it,true)
                    }
                }
                categoryLoading.value = Resource.loading(false)
            }


        }
    }




}