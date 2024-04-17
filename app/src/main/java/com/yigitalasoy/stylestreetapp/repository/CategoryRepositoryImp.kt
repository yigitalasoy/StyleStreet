package com.yigitalasoy.stylestreetapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.model.CategoryResponse
import com.yigitalasoy.stylestreetapp.util.Constants
import com.yigitalasoy.stylestreetapp.util.Resource
import kotlinx.coroutines.tasks.await

class CategoryRepositoryImp(val firebaseFirestore: FirebaseFirestore): CategoryRepository {


    override suspend fun getAllCategories(): Resource<ArrayList<CategoryResponse>> {
        //firestore'dan ve storage'dan veriler alınıp return edilecek.
        var categoryList = ArrayList<CategoryResponse>()

        val docRef = firebaseFirestore.collection(Constants.FIRESTORE_DATABASE_CATEGORIES).get().await()


        return try {
            docRef.documents.let {
                for (category in it){
                    /*val categoryModel = CategoryResponse()
                    categoryModel.categoryId = category.data!![Constants.CATEGORYRESPONSE_categoryId].toString()
                    categoryModel.categoryName = category.data!![Constants.CATEGORYRESPONSE_categoryName].toString()
                    categoryModel.categoryImage = category.data!![Constants.CATEGORYRESPONSE_categoryImage].toString()*/

                    //categoryList.add(categoryModel)

                    categoryList.add(category.toObject(CategoryResponse::class.java)!!)

                }
                return@let Resource.success(categoryList)
            }

        } catch (e: Exception){
            return Resource.error(e.message.toString(),null)
        }

    }
}