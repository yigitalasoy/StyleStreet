package com.yigitalasoy.stylestreetapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.model.BasketDetailResponse
import com.yigitalasoy.stylestreetapp.model.SoldDetailResponse
import com.yigitalasoy.stylestreetapp.model.SoldResponse
import com.yigitalasoy.stylestreetapp.util.Resource
import kotlinx.coroutines.tasks.await

class SoldRepositoryImp(val firebaseFirestore: FirebaseFirestore): SoldRepository {
    override suspend fun getUserSold(userId: String): Resource<ArrayList<SoldResponse>> {

        var soldList = ArrayList<SoldResponse>()

        val docRef = firebaseFirestore.collection("tbl_Sold").whereEqualTo("User_Id",userId).get().await()

        return try {
            docRef.documents.let {
                for (sold in it){
                    soldList.add(sold.toObject(SoldResponse::class.java)!!)
                }

                soldList.sortBy { it.soldDate }

                return@let Resource.success(soldList)
            }

        } catch (e: Exception){
            return Resource.error(e.message.toString(),null)
        }

    }

    override suspend fun getUserSoldDetail(userId: String): Resource<ArrayList<SoldDetailResponse>> {

        var soldDetailList = ArrayList<SoldDetailResponse>()

        val docRef = firebaseFirestore.collection("tbl_SoldDetail").whereEqualTo("User_Id",userId).get().await()

        return try {
            docRef.documents.let {
                for (soldDetail in it){
                    soldDetailList.add(soldDetail.toObject(SoldDetailResponse::class.java)!!)
                }
                return@let Resource.success(soldDetailList)
            }

        } catch (e: Exception){
            return Resource.error(e.message.toString(),null)
        }

    }

    override suspend fun addUserSold(sold: SoldResponse): Resource<SoldResponse> {

        val docRef = firebaseFirestore.collection("tbl_Sold")
        val newId = docRef.document().id

        sold.soldId = newId

        val state = docRef.document(newId).set(sold)
        state.await()

        if(state.isSuccessful){
            return Resource.success(sold)
        } else {
            return Resource.error(state.exception?.message.toString(),null)
        }




    }

    override suspend fun addUserSoldDetail(
        userId: String,
        soldId: String,
        basketProducts: ArrayList<BasketDetailResponse>
    ): Resource<ArrayList<SoldDetailResponse>> {

        var soldDetailList = ArrayList<SoldDetailResponse>(arrayListOf())

        val docRef = firebaseFirestore.collection("tbl_SoldDetail")

        for(basketProduct in basketProducts){
            soldDetailList.add(
                SoldDetailResponse(
                    docRef.document().id,
                    soldId,
                    basketProduct.subProductId,
                    basketProduct.quantity.toString(),
                    basketProduct.unitPrice.toString().toInt(),
                    userId
                )
            )
        }

        for(soldDetail in soldDetailList){
            val state = docRef.document(soldDetail.soldDetailId.toString()).set(soldDetail)
            state.await()

            if(!(state.isSuccessful)){
                return Resource.error(state.exception?.message.toString(),null)
            }
        }

        return Resource.success(soldDetailList)

    }


}