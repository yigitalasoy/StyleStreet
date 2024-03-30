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
import javax.inject.Inject

class ProductRepositoryImp @Inject constructor(val productColorRepository: ProductColorRepository,val firebaseFirestore: FirebaseFirestore): ProductRepository {

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
                    println("hash test ${hashMap[0]}")

                    for (hash in hashMap){
                        println("hash: $hash")

                        subProductList.add(
                            SubProductResponse(
                                subProductId = hash["SubProduct_Id"].toString(),
                                productColor = ProductColorResponse(hash["SubProduct_ColorId"].toString()),
                                stock = hash["SubProduct_Stock"].toString(),
                                productSize = ProductSizeResponse(hash["SubProduct_SizeId"].toString()),
                                price = hash["SubProduct_Price"].toString(),
                                updateTime = hash["UpdateTime"].toString(),
                                subProductImageURL = hash["SubProduct_ImageURL"].toString()
                            )
                        )
                    }
                    val testProduct = ProductResponse(
                        categoryId = CategoryResponse(categoryId = product.data!!["Category_Id"].toString()),
                        productId = product.data!!["Product_Id"].toString(),
                        productName = product.data!!["Product_Name"].toString(),
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
        TODO("Not yet implemented")
    }

    override suspend fun getProductsWithId(productId: String): Resource<ArrayList<ProductResponse>> {
        TODO("Not yet implemented")
    }
}