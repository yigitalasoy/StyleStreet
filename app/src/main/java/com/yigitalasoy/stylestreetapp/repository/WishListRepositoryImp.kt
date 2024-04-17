package com.yigitalasoy.stylestreetapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.model.WishListResponse
import com.yigitalasoy.stylestreetapp.util.Resource
import kotlinx.coroutines.tasks.await

class WishListRepositoryImp(val firebaseFirestore: FirebaseFirestore): WishListRepository {
    override suspend fun getWishList(userId: String): Resource<ArrayList<WishListResponse>> {

        var wishList = ArrayList<WishListResponse>()

        val docRef = firebaseFirestore.collection("tbl_WishList").whereEqualTo("User_Id",userId).get().await()

        return try {
            docRef.documents.let {
                for (wish in it){
                    /*wishList.add(
                        WishListResponse(
                            wish.data!!["Wish_Id"].toString(),
                            wish.data!!["User_Id"].toString(),
                            wish.data!!["Product_Id"].toString(),
                        )
                    )*/
                    wishList.add(wish.toObject(WishListResponse::class.java)!!)
                }
                println("for bitti: ${wishList}")
                return@let Resource.success(wishList)
            }

        } catch (e: Exception){
            return Resource.error(e.message.toString(),null)
        }

    }

    override suspend fun addProductToWishList(userId: String, productId: String): HashMap<String,Any> {

        val docRef = firebaseFirestore.collection("tbl_WishList")
        val newId = docRef.document().id

        /*val state = docRef.document(newId).set(
            hashMapOf(
                "User_Id" to userId,
                "Product_Id" to productId,
                "Wish_Id" to newId
            )
        )*/
        val state = docRef.document(newId).set(WishListResponse(newId,userId,productId))
        state.await()

        if(state.isSuccessful){
            return hashMapOf(
                "state" to state.isSuccessful,
                "newId" to newId
            )
        } else {
            return hashMapOf(
                "newId" to state.exception.toString()
            )
        }


    }

    override suspend fun removeProductToWishList(
        wishId: String
    ): Boolean {
        val docRef = firebaseFirestore.collection("tbl_WishList").document(wishId).delete()
        docRef.await()
        return docRef.isSuccessful
    }

}