package com.yigitalasoy.stylestreetapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.model.AddressResponse
import com.yigitalasoy.stylestreetapp.util.Resource
import kotlinx.coroutines.tasks.await

class AddressRepositoryImp(val firebaseFirestore: FirebaseFirestore): AddressRepository {
    override suspend fun getUserAddress(userId: String): Resource<ArrayList<AddressResponse>> {

        var addressList = ArrayList<AddressResponse>()

        val docRef = firebaseFirestore.collection("tbl_Address")
            .whereEqualTo("User_Id",userId).get().await()


        return try {
            docRef.documents.let {results->

                for (address in results){
                    addressList.add(address.toObject(AddressResponse::class.java)!!)

                }
                return@let Resource.success(addressList)
            }

        } catch (e: Exception){
            return Resource.error(e.message.toString(),null)
        }

    }

    override suspend fun addAddress(newAddress: AddressResponse): Resource<AddressResponse> {

        val docRef = firebaseFirestore.collection("tbl_Address")
        if(newAddress.addressId == null){
            val newId = docRef.document().id
            newAddress.addressId = newId
        }

        val state = docRef.document(newAddress.addressId!!).set(newAddress)
        state.await()

        return if(state.isSuccessful){
            Resource.success(newAddress)
        } else {
            Resource.error(state.exception.toString(),null)
        }

    }

    override suspend fun removeAddress(addressId: String): Resource<Boolean> {

        val docRef = firebaseFirestore.collection("tbl_Address")

        val state = docRef.document(addressId).delete()
        state.await()

        return if(state.isSuccessful){
            Resource.success(true)
        } else {
            Resource.error(state.exception.toString(),null)
        }

    }
}