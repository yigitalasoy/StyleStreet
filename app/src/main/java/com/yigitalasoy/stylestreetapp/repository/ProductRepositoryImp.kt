package com.yigitalasoy.stylestreetapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.model.CategoryResponse
import com.yigitalasoy.stylestreetapp.model.ProductColorResponse
import com.yigitalasoy.stylestreetapp.model.ProductResponse
import com.yigitalasoy.stylestreetapp.model.ProductSizeResponse
import com.yigitalasoy.stylestreetapp.model.SubProductResponse
import com.yigitalasoy.stylestreetapp.util.Constants
import com.yigitalasoy.stylestreetapp.util.Resource
import kotlinx.coroutines.tasks.await
import java.time.Instant
import javax.inject.Inject

class ProductRepositoryImp @Inject constructor(val firebaseFirestore: FirebaseFirestore): ProductRepository {

    override suspend fun getTopSellingProduct(): Resource<ArrayList<ProductResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun getNewInProduct(): Resource<ArrayList<ProductResponse>> {

        val productList = ArrayList<ProductResponse>()


        val docRef = firebaseFirestore.collection(Constants.FIRESTORE_DATABASE_PRODUCTS).get().await()

        return try {
            docRef.documents.let {
                for (product in it){
                    val subProductList = ArrayList<SubProductResponse>()

                    val hashMap : ArrayList<HashMap<String, String>> = product.data!!["SubProducts"] as ArrayList<HashMap<String, String>>

                    for (hash in hashMap){

                        subProductList.add(
                            SubProductResponse(
                                subProductId = hash[Constants.SUBPRODUCTRESPONSE_subProductId].toString(),
                                subProductColorId = ProductColorResponse(hash[Constants.SUBPRODUCTRESPONSE_subProductColorId].toString()),
                                subProductStock = hash[Constants.SUBPRODUCTRESPONSE_subProductStock].toString(),
                                subProductName = hash[Constants.SUBPRODUCTRESPONSE_subProductName].toString(),
                                subProductSizeId = ProductSizeResponse(hash[Constants.SUBPRODUCTRESPONSE_subProductSizeId].toString()),
                                subProductPrice = hash[Constants.SUBPRODUCTRESPONSE_subProductPrice].toString(),
                                updateTime = hash[Constants.SUBPRODUCTRESPONSE_updateTime].toString(),
                                subProductImageURL = hash[Constants.SUBPRODUCTRESPONSE_subProductImageURL] as ArrayList<String>
                            )
                        )
                    }
                    val testProduct = ProductResponse(
                        categoryId = CategoryResponse(categoryId = product.data!![Constants.PRODUCTRESPONSE_categoryId].toString()),
                        productId = product.data!![Constants.PRODUCTRESPONSE_productId].toString(),
                        productName = product.data!![Constants.PRODUCTRESPONSE_productName].toString(),
                        allProducts = subProductList
                    )

                    //testProduct.allProducts.addAll(subProductList)
                    productList.add(testProduct)
                    //subProductList.clear()
                }
                return@let Resource.success(productList)
            }

        } catch (e: Exception){
            return Resource.error(e.message.toString(),null)
        }


    }

    override suspend fun getSearchedProduct(search: String): Resource<ArrayList<ProductResponse>> {

        val productList = ArrayList<ProductResponse>()
        var subProductNumber = 0

        var guncelEpoch = Instant.now().epochSecond
        var epochEsik = 432000

        val docRef = firebaseFirestore.collection(Constants.FIRESTORE_DATABASE_PRODUCTS).get().await()

        return try {
            docRef.documents.let {
                while (true){
                    for (product in it){
                        val subProductList = ArrayList<SubProductResponse>()

                        val hashMap : ArrayList<HashMap<String, String>> = product.data!!["SubProducts"] as ArrayList<HashMap<String, String>>


                        for (hash in hashMap){

                            if((guncelEpoch - hash[Constants.SUBPRODUCTRESPONSE_updateTime].toString().toInt()) < epochEsik){
                                println("max 5 gün")
                                if(subProductNumber < 5){
                                    subProductList.add(
                                        SubProductResponse(
                                            subProductId = hash[Constants.SUBPRODUCTRESPONSE_subProductId].toString(),
                                            subProductColorId = ProductColorResponse(hash[Constants.SUBPRODUCTRESPONSE_subProductColorId].toString()),
                                            subProductStock = hash[Constants.SUBPRODUCTRESPONSE_subProductStock].toString(),
                                            subProductName = hash[Constants.SUBPRODUCTRESPONSE_subProductName].toString(),
                                            subProductSizeId = ProductSizeResponse(hash[Constants.SUBPRODUCTRESPONSE_subProductSizeId].toString()),
                                            subProductPrice = hash[Constants.SUBPRODUCTRESPONSE_subProductPrice].toString(),
                                            updateTime = hash[Constants.SUBPRODUCTRESPONSE_updateTime].toString(),
                                            subProductImageURL = hash[Constants.SUBPRODUCTRESPONSE_subProductImageURL] as ArrayList<String>
                                        )
                                    )
                                    subProductNumber++
                                }


                            }

                        }
                        val testProduct = ProductResponse(
                            categoryId = CategoryResponse(categoryId = product.data!![Constants.PRODUCTRESPONSE_categoryId].toString()),
                            productId = product.data!![Constants.PRODUCTRESPONSE_productId].toString(),
                            productName = product.data!![Constants.PRODUCTRESPONSE_productName].toString(),
                            allProducts = subProductList
                        )


                        if(testProduct.allProducts.size != 0){
                            productList.add(testProduct)
                        }
                        //testProduct.allProducts.addAll(subProductList)
                        //subProductList.clear()
                        if(subProductNumber > 5){
                            println("subProductNumber > 5 returne girdi")
                            return@let Resource.success(productList)
                        }
                    }
                    if(subProductNumber < 2){
                        epochEsik += 259200
                        println("product number $subProductNumber olduğu için epoch artırıldı. güncel epoch: $epochEsik")
                        productList.clear()
                        subProductNumber = 0
                    } else {
                        break
                    }
                }
                println("139 returne girdi product number: $subProductNumber")
                return@let Resource.success(productList)
            }

        } catch (e: Exception){
            return Resource.error(e.message.toString(),null)
        }

    }

    override suspend fun getProductsWithId(productId: String): Resource<ArrayList<ProductResponse>> {
        TODO("Not yet implemented")
    }
}