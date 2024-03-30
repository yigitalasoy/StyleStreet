package com.yigitalasoy.stylestreetapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitalasoy.stylestreetapp.model.CategoryResponse
import com.yigitalasoy.stylestreetapp.model.ProductColorResponse
import com.yigitalasoy.stylestreetapp.model.ProductResponse
import com.yigitalasoy.stylestreetapp.model.ProductSizeResponse
import com.yigitalasoy.stylestreetapp.repository.ProductRepository
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
class ProductViewModel @Inject constructor(val productRepository: ProductRepository): ViewModel() {

    val productLiveData = MutableLiveData<Resource<ArrayList<ProductResponse>>>()
    val productLoading = MutableLiveData<Resource<Boolean>>()
    val productError = MutableLiveData<Resource<Boolean>>()

    private var job : Job? = null


    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        productError.value = Resource.error(throwable.message.toString(),true)
    }

    fun getNewInProduct(){

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val productResponse = productRepository.getNewInProduct()

            withContext(Dispatchers.Main){
                if(productResponse.data != null){
                    println("ürünler geldi..")
                    productLiveData.value = productResponse
                    println(productLiveData.value?.data)
                } else {
                    productResponse.message.let {
                        productError.value = Resource.error(productResponse.message.toString(),true)
                    }
                }
            }
        }
    }

    fun updateProductColorName(colors: ArrayList<ProductColorResponse>){

        colors.let {
            /*
            val productByNameMap = colors.associateBy { it.colorName }


            val productNameToFind = "Blue"
            val productIdByName = productByNameMap.get(productNameToFind)?.colorId ?: -1

            if (productIdByName != -1) {
                println("$productNameToFind ürününün ID'si: $productIdByName")
            } else {
                println("$productNameToFind ürünü bulunamadı.")
            }

            val productIdToFind = "xM230BXhr0TTtZ2xfEBy"
            val productNameById = productByIdMap.get(productIdToFind)?.colorName

            if (productNameById != null) {
                println("ID $productIdToFind olan ürünün adı: $productNameById")
            } else {
                println("ID $productIdToFind olan ürün bulunamadı.")
            }
            */

            val productByIdMap = colors.associateBy { it.colorId }

            if(productLiveData.value?.data != null){
                println("color kontrole girdi")
                for (product in productLiveData.value?.data!!){
                    for (subproduct in product.allProducts){
                        subproduct.productColor.colorName = productByIdMap[subproduct.productColor.colorId]?.colorName
                        println("subproduct----> ${subproduct}")
                    }
                }

                println("color live datalar:  ${productLiveData.value?.data}")

            }
        }
    }

    fun updateProductSizeName(productSizes: ArrayList<ProductSizeResponse>){
        productSizes?.let {

            val productByIdMap = productSizes.associateBy { it.productSizeId }

            if(productLiveData.value?.data != null){
                println("size kontrole girdi")
                for (product in productLiveData.value?.data!!){
                    for (subproduct in product.allProducts){
                        subproduct.productSize.sizeName = productByIdMap[subproduct.productSize.productSizeId]?.sizeName
                        println("subproduct----> ${subproduct}")
                    }
                }
                println("size live datalar:  ${productLiveData.value?.data}")
            }
        }
    }


    fun updateProductCategories(categories: ArrayList<CategoryResponse>){
        categories?.let {

            val productByIdMap = categories.associateBy { it.categoryId }

            if(productLiveData.value?.data != null){
                println("category kontrole girdi")
                for (product in productLiveData.value?.data!!){
                    product.categoryId.categoryName = productByIdMap[product.categoryId.categoryId]?.categoryName
                    product.categoryId.categoryImage = productByIdMap[product.categoryId.categoryId]?.categoryImage
                    println("product----> $product")

                }
                println("category live datalar:  ${productLiveData.value?.data}")
            }
        }
    }

}