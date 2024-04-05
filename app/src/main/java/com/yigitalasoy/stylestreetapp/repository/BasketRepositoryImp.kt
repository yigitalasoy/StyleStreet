package com.yigitalasoy.stylestreetapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.model.BasketDetailResponse
import com.yigitalasoy.stylestreetapp.model.BasketResponse
import com.yigitalasoy.stylestreetapp.util.Constants
import com.yigitalasoy.stylestreetapp.util.Resource
import kotlinx.coroutines.tasks.await

class BasketRepositoryImp(val firebaseFirestore: FirebaseFirestore): BasketRepository {


    override suspend fun getUserBasket(userId: String): Resource<BasketResponse> {

        val docRef = firebaseFirestore.collection(Constants.FIRESTORE_DATABASE_BASKET).document(userId).get().await()


        return try {
            docRef.let {

                val basketProductList = ArrayList<BasketDetailResponse>()
                val hashMap : ArrayList<HashMap<String, String>> = it.data!!["Basket_Products"] as ArrayList<HashMap<String, String>>

                for (hash in hashMap){

                    basketProductList.add(
                        BasketDetailResponse(
                            subProductId = hash["SubProduct_Id"].toString(),
                            basketDetailId = hash["BasketDetail_Id"].toString(),
                            basketId = hash["Basket_Id"].toString(),
                            quantity = hash["Quantity"]?.toInt(),
                            total = hash["Total"]?.toInt(),
                            productId = hash["Product_Id"].toString()
                        )
                    )
                }

                val basket = BasketResponse(
                    basketId = it.data!!["Basket_Id"].toString(),
                    basketTotal = it.data!!["Basket_Total"].toString(),
                    userId = it.data!!["User_Id"].toString(),
                    basketProducts = basketProductList
                )


                return@let Resource.success(basket)
            }

        } catch (e: Exception){
            return Resource.error(e.message.toString(),null)
        }


    }
}