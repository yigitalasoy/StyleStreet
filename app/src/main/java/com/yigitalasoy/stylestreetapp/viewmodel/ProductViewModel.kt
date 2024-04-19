package com.yigitalasoy.stylestreetapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitalasoy.stylestreetapp.model.CategoryResponse
import com.yigitalasoy.stylestreetapp.model.ProductColorResponse
import com.yigitalasoy.stylestreetapp.model.ProductResponse
import com.yigitalasoy.stylestreetapp.model.ProductSizeResponse
import com.yigitalasoy.stylestreetapp.model.SubProductResponse
import com.yigitalasoy.stylestreetapp.repository.ProductRepository
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
class ProductViewModel @Inject constructor(val productRepository: ProductRepository): ViewModel() {

    val allProductLiveData = MutableLiveData<Resource<ArrayList<ProductResponse>>>()
    val newInProductLiveData = MutableLiveData<Resource<ArrayList<ProductResponse>>>()


    //val productLoading = MutableLiveData<Resource<Boolean>>()
    //val productError = MutableLiveData<Resource<Boolean>>()

    private var job : Job? = null


    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        newInProductLiveData.value = Resource.error(throwable.message.toString(),null)
    }

    fun getNewInProduct(){

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val productResponse = productRepository.getNewInProduct()

            withContext(Dispatchers.Main){
                when(productResponse.status){
                    Status.SUCCESS -> {
                        if(productResponse.data != null){
                            newInProductLiveData.value = productResponse
                        }
                    }
                    Status.ERROR -> {
                        newInProductLiveData.value = Resource.error(productResponse.message.toString(),null)
                    }
                    Status.LOADING -> {
                        newInProductLiveData.value = Resource.loading(null)
                    }
                }
            }
        }
    }

    fun getAllProduct(){

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val productResponse = productRepository.getAllProduct()

            withContext(Dispatchers.Main){

                when(productResponse.status){
                    Status.SUCCESS -> {
                        if(productResponse.data != null){
                            allProductLiveData.value = productResponse
                        }
                    }
                    Status.ERROR -> {
                        allProductLiveData.value = Resource.error(productResponse.message.toString(),null)
                    }
                    Status.LOADING -> {
                        allProductLiveData.value = Resource.loading(null)
                    }
                }

            }
        }
    }


    fun searchProduct(searchString: String): ArrayList<ProductResponse>?{
        return allProductLiveData.value?.data?.filter {it.productName!!.lowercase().contains(searchString.lowercase())} as ArrayList<ProductResponse>?
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

            if(allProductLiveData.value?.data != null){
                for (product in allProductLiveData.value?.data!!){
                    for (subproduct in product.allProducts!!){
                        subproduct.subProductColorId?.colorName = productByIdMap[subproduct.subProductColorId?.colorId]?.colorName
                        subproduct.subProductColorId?.colorCode = productByIdMap[subproduct.subProductColorId?.colorId]?.colorCode
                    }
                }
            }



        }
    }

    fun updateProductSizeName(productSizes: ArrayList<ProductSizeResponse>){
        productSizes?.let {

            val productByIdMap = productSizes.associateBy { it.productSizeId }

            if(allProductLiveData.value?.data != null){
                for (product in allProductLiveData.value?.data!!){
                    for (subproduct in product.allProducts!!){
                        subproduct.subProductSizeId?.productSizeName = productByIdMap[subproduct.subProductSizeId?.productSizeId]?.productSizeName
                    }
                }
            }
        }
    }


    fun updateProductCategories(categories: ArrayList<CategoryResponse>){
        categories?.let {

            val productByIdMap = categories.associateBy { it.categoryId }

            if(allProductLiveData.value?.data != null){
                for (product in allProductLiveData.value?.data!!){
                    product.categoryId!!.categoryName = productByIdMap[product.categoryId!!.categoryId]?.categoryName
                    product.categoryId!!.categoryImage = productByIdMap[product.categoryId!!.categoryId]?.categoryImage

                }
            }
        }
    }

    fun getProductByProductIdAndSizeId(selectedProductId: String,selectedSizeId: String?): ArrayList<SubProductResponse>{
        var array = ArrayList<SubProductResponse>()

        if(selectedSizeId != null){

            /*productLiveData.value?.data?.find { it.productId == selectedProductId }?.let {
                for(a in it.allProducts){
                    array.put(a.subProductColorId,a.subProductSizeId)
                }
            }*/

            for(i in allProductLiveData.value?.data?.find { it.productId == selectedProductId }?.allProducts!!){
                if(i.subProductSizeId?.productSizeId == selectedSizeId){
                    array.add(i)
                }
            }
            /*productLiveData.value?.data?.find { it.productId == selectedProductId }?.allProducts?.find { it.subProductSizeId.productSizeId == selectedSizeId }?.let {
                println("gelen it: $it")
                array.add(it)
            }*/


            return array

        } else {
            return allProductLiveData.value?.data?.find { it.productId == selectedProductId }?.allProducts!!
        }

    }

    fun getSubProductWithId(selectedProductId: String,selectedSubProductId: String): SubProductResponse? {
        return allProductLiveData.value?.data?.find { it.productId == selectedProductId }?.allProducts?.find { it.subProductId == selectedSubProductId }
    }

}