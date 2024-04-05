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
    val testproductLiveData = MutableLiveData<Resource<ArrayList<ProductResponse>>>()
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
            val testproductResponse = productRepository.getSearchedProduct("")

            withContext(Dispatchers.Main){
                if(productResponse.data != null){
                    productLiveData.value = productResponse
                    testproductLiveData.value = testproductResponse
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
                for (product in productLiveData.value?.data!!){
                    for (subproduct in product.allProducts){
                        subproduct.subProductColorId.colorName = productByIdMap[subproduct.subProductColorId.colorId]?.colorName
                        subproduct.subProductColorId.colorCode = productByIdMap[subproduct.subProductColorId.colorId]?.colorCode
                    }
                }
            }



        }
    }

    fun updateProductSizeName(productSizes: ArrayList<ProductSizeResponse>){
        productSizes?.let {

            val productByIdMap = productSizes.associateBy { it.productSizeId }

            if(productLiveData.value?.data != null){
                for (product in productLiveData.value?.data!!){
                    for (subproduct in product.allProducts){
                        subproduct.subProductSizeId.productSizeName = productByIdMap[subproduct.subProductSizeId.productSizeId]?.productSizeName
                    }
                }
            }
        }
    }


    fun updateProductCategories(categories: ArrayList<CategoryResponse>){
        categories?.let {

            val productByIdMap = categories.associateBy { it.categoryId }

            if(productLiveData.value?.data != null){
                for (product in productLiveData.value?.data!!){
                    product.categoryId.categoryName = productByIdMap[product.categoryId.categoryId]?.categoryName
                    product.categoryId.categoryImage = productByIdMap[product.categoryId.categoryId]?.categoryImage

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

            for(i in productLiveData.value?.data?.find { it.productId == selectedProductId }?.allProducts!!){
                if(i.subProductSizeId.productSizeId == selectedSizeId){
                    array.add(i)
                }
            }
            /*productLiveData.value?.data?.find { it.productId == selectedProductId }?.allProducts?.find { it.subProductSizeId.productSizeId == selectedSizeId }?.let {
                println("gelen it: $it")
                array.add(it)
            }*/


            return array

        } else {
            return productLiveData.value?.data?.find { it.productId == selectedProductId }?.allProducts!!
        }

    }

    fun getSubProductWithId(selectedProductId: String,selectedSubProductId: String): SubProductResponse? {
        return productLiveData.value?.data?.find { it.productId == selectedProductId }?.allProducts?.find { it.subProductId == selectedSubProductId }
    }

}