package com.yigitalasoy.stylestreetapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.model.ProductColorResponse
import com.yigitalasoy.stylestreetapp.util.Constants
import com.yigitalasoy.stylestreetapp.util.Resource
import kotlinx.coroutines.tasks.await

class ProductColorRepositoryImp(val firebaseFirestore: FirebaseFirestore): ProductColorRepository {
    override suspend fun getAllProductColor(): Resource<ArrayList<ProductColorResponse>> {
        //firestore'dan ve storage'dan veriler alınıp return edilecek.
        var colorList = ArrayList<ProductColorResponse>()

        val docRef = firebaseFirestore.collection(Constants.FIRESTORE_DATABASE_COLORS).get().await()

        return try {
            docRef.documents.let {
                for (color in it){
                    /*val color = ProductColorResponse(category.data!![Constants.PRODUCTCOLORRESPONSE_colorId].toString())
                    // color.colorId = category.data!!["Color_Id"].toString()
                    color.colorName = category.data!![Constants.PRODUCTCOLORRESPONSE_colorName].toString()
                    color.colorCode = category.data!![Constants.PRODUCTCOLORRESPONSE_colorCode].toString()
                    colorList.add(color)*/
                    colorList.add(color.toObject(ProductColorResponse::class.java)!!)
                }
                return@let Resource.success(colorList)
            }

        } catch (e: Exception){
            return Resource.error(e.message.toString(),null)
        }
    }
}