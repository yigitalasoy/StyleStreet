package com.yigitalasoy.stylestreetapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.model.ProductSizeResponse
import com.yigitalasoy.stylestreetapp.util.Constants
import com.yigitalasoy.stylestreetapp.util.Resource
import kotlinx.coroutines.tasks.await

class ProductSizeRepositoryImp(val firebaseFirestore: FirebaseFirestore): ProductSizeRepository {
    override suspend fun getAllProductSize(): Resource<ArrayList<ProductSizeResponse>> {
        //firestore'dan ve storage'dan veriler alınıp return edilecek.
        var sizeList = ArrayList<ProductSizeResponse>()

        val docRef = firebaseFirestore.collection(Constants.FIRESTORE_DATABASE_PRODUCT_SIZE).get().await()

        return try {
            docRef.documents.let {
                for (productSize in it){
                    sizeList.add(productSize.toObject(ProductSizeResponse::class.java)!!)
                }
                return@let Resource.success(sizeList)
            }

        } catch (e: Exception){
            return Resource.error(e.message.toString(),null)
        }
    }

}