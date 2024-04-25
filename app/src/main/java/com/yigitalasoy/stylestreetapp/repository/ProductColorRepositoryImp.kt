package com.yigitalasoy.stylestreetapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.model.ProductColorResponse
import com.yigitalasoy.stylestreetapp.util.Constants
import com.yigitalasoy.stylestreetapp.util.Resource
import kotlinx.coroutines.tasks.await

class ProductColorRepositoryImp(val firebaseFirestore: FirebaseFirestore): ProductColorRepository {
    override suspend fun getAllProductColor(): Resource<ArrayList<ProductColorResponse>> {
        var colorList = ArrayList<ProductColorResponse>()

        val docRef = firebaseFirestore.collection(Constants.FIRESTORE_DATABASE_COLORS).get().await()

        return try {
            docRef.documents.let {
                for (color in it){
                    colorList.add(color.toObject(ProductColorResponse::class.java)!!)
                }
                return@let Resource.success(colorList)
            }

        } catch (e: Exception){
            return Resource.error(e.message.toString(),null)
        }
    }
}