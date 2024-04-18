package com.yigitalasoy.stylestreetapp.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.model.BasketDetailResponse
import com.yigitalasoy.stylestreetapp.model.BasketResponse
import com.yigitalasoy.stylestreetapp.util.Constants
import com.yigitalasoy.stylestreetapp.util.Resource
import kotlinx.coroutines.tasks.await

class BasketRepositoryImp(val firebaseFirestore: FirebaseFirestore): BasketRepository {


    override suspend fun getUserBasket(userId: String): Resource<BasketResponse> {

        println("get user basket repo çalışacak")

        val docRef =
            firebaseFirestore.collection(Constants.FIRESTORE_DATABASE_BASKET).document(userId).get()
                .await()


        return try {
            docRef.let {

                val basketProductList = ArrayList<BasketDetailResponse>()
                val hashMap: ArrayList<HashMap<String, String>> =
                    it.data!!["Basket_Products"] as ArrayList<HashMap<String, String>>

                for (hash in hashMap) {
                    println("quantity: ${hash["Quantity"]?.toInt()}")
                    basketProductList.add(
                        BasketDetailResponse(
                            subProductId = hash["SubProduct_Id"].toString(),
                            basketDetailId = hash["BasketDetail_Id"].toString(),
                            basketId = hash["Basket_Id"].toString(),
                            quantity = hash["Quantity"]!!.toInt(),
                            productId = hash["Product_Id"].toString()
                        )
                    )
                }

                val basket = BasketResponse(
                    basketId = it.data!!["Basket_Id"].toString(),
                    userId = it.data!!["User_Id"].toString(),
                    basketProducts = basketProductList
                )

                println("imp gelen data: $basket")
                return@let Resource.success(basket)
            }

        } catch (e: Exception) {
            return Resource.error(e.message.toString(), null)
        }


    }

    override suspend fun productQuantityChange(
        userId: String,
        subProductId: String,
        type: String,
        position: Int
    ): Boolean {

        //val docRef = firebaseFirestore.collection(Constants.FIRESTORE_DATABASE_BASKET).document(userId).get().await()

        //var database: DatabaseReference

        //database = Firebase.database.reference
        //println("position: $position")

        //database.child(Constants.FIRESTORE_DATABASE_BASKET).child(userId).child("Basket_Products").child(position.toString()).child("Quantity").setValue(5)
        //println("database: ${database.child(Constants.FIRESTORE_DATABASE_BASKET).child(userId).child("Basket_Products").child(position.toString()).child("Quantity").get()}")


        val collectionRef = firebaseFirestore.collection("tbl_Basket")
        val documentRef = collectionRef.document(userId)
        var state = false
        val snapshot = documentRef.get().await()

        snapshot.data?.let { data ->
            if (data != null) {
                val productsArray = data["Basket_Products"] as ArrayList<HashMap<String, String>>
                if (productsArray.isNotEmpty()) {
                    val firstProduct = productsArray[position]
                    val quantity = firstProduct["Quantity"] as String  // Quantity alanını al
                    Log.e("şuanki: ", quantity)
                    if (type.equals("increase")) {
                        firstProduct["Quantity"] = (quantity.toInt() + 1).toString()
                    } else if (type.equals("decrease")) {
                        firstProduct["Quantity"] = (quantity.toInt() - 1).toString()
                    }
                    val update = documentRef.update("Basket_Products", productsArray)
                    update.await()
                    if (update.isSuccessful) {
                        state = true
                        Log.d("Firestore", "Doküman güncellendi.")
                    }
                }
            }
        }
        return state

    }

    override suspend fun productRemove(
        userId: String,
        subProductId: String,
        type: String,
        position: Int
    ): Boolean {


        val collectionRef = firebaseFirestore.collection("tbl_Basket")
        val documentRef = collectionRef.document(userId)
        var state = false
        val snapshot = documentRef.get().await()

        snapshot.data?.let { data ->
            if (data != null) {
                val productsArray = data["Basket_Products"] as ArrayList<HashMap<String, String>>
                if (productsArray.isNotEmpty()) {
                    productsArray.removeAt(position)

                    val update = documentRef.update("Basket_Products", productsArray)
                    update.await()
                    if (update.isSuccessful) {
                        state = true
                        Log.d("Firestore", "Doküman silindi.")
                    }
                }
            }
        }
        return state


    }

    override suspend fun addProduct(userId: String, basketItem: BasketResponse): HashMap<String,Any> {

        val collectionRef = firebaseFirestore.collection("tbl_Basket")
        val documentRef = collectionRef.document(userId)
        var hashReturn: HashMap<String,Any> = hashMapOf()
        val snapshot = documentRef.get().await()
        val productsArray: ArrayList<HashMap<String, Any>>

        var newId = collectionRef.document().id
        println("oluşturulan test idsi: $newId")


        if (snapshot.data != null) {
            //kullanıcı id var. sadece ürünü ekle
            snapshot.data?.let { data ->
                if (!(data["Basket_Id"].toString().isNullOrEmpty())) {
                    productsArray = data["Basket_Products"] as ArrayList<HashMap<String, Any>>

                    var product: HashMap<String, Any> = hashMapOf(
                        //fonksiyondaki basketitemden gelen veriler buraya işlenecek
                        "BasketDetail_Id" to newId,
                        "Basket_Id" to data["Basket_Id"].toString(),
                        "Product_Id" to basketItem.basketProducts!![0].productId!!,
                        "Quantity" to basketItem.basketProducts!![0].quantity!!.toString(),
                        "SubProduct_Id" to basketItem.basketProducts!![0].subProductId!!
                    )

                    productsArray.add(product)

                    val update = documentRef.update("Basket_Products", productsArray)
                    update.await()
                    if (update.isSuccessful) {
                        hashReturn = hashMapOf(
                            "state" to true,
                            "newBasketProductId" to newId
                        )
                        Log.d("Firestore", "basket Dokümanı başarıyla güncellendi.")
                    }
                }
            }

        } else {
            //tbl baskette kullanıcı id'ye göre item yok. oluştur.
            var newBasketId = collectionRef.document().id

            val testState = firebaseFirestore.collection("tbl_Basket").document(userId).set(hashMapOf(
                "Basket_Id" to newBasketId,
                "User_Id" to userId,
                "Basket_Products" to arrayListOf<HashMap<String,Any>>(
                    hashMapOf(
                        "BasketDetail_Id" to newId,
                        "Basket_Id" to newBasketId,
                        "Product_Id" to basketItem.basketProducts!![0].productId!!,
                        "Quantity" to basketItem.basketProducts!![0].quantity!!.toString(),
                        "SubProduct_Id" to basketItem.basketProducts!![0].subProductId!!
                    )
                )
            ))

            testState.await()
            hashReturn = hashMapOf(
                "state" to testState.isSuccessful,
                "newBasketProductId" to newId
            )

        }
        println("sepete ürün ekledikten sonra dönen state: $hashReturn")
        return hashReturn
    }
}