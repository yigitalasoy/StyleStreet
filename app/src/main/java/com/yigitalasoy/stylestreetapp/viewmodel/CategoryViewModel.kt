package com.yigitalasoy.stylestreetapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitalasoy.stylestreetapp.model.CategoryResponse
import com.yigitalasoy.stylestreetapp.repository.CategoryRepository
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
class CategoryViewModel @Inject constructor(val categoryRepository: CategoryRepository): ViewModel() {

    val categoryLiveData = MutableLiveData<Resource<ArrayList<CategoryResponse>>>()

    private var job : Job? = null


    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        categoryLiveData.value = Resource.error(throwable.message.toString(),null)
    }

    fun getAllCategories(){

        categoryLiveData.value = Resource.loading(null)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val response = categoryRepository.getAllCategories()

            withContext(Dispatchers.Main){

                when(response.status){
                    Status.SUCCESS -> {
                        if(response.data != null){
                            categoryLiveData.value = response
                        }
                    }
                    Status.ERROR -> {
                        categoryLiveData.value = Resource.error(response.message.toString(),null)
                    }
                    Status.LOADING -> {
                        categoryLiveData.value = Resource.loading(null)
                    }
                }
            }
        }
    }
}