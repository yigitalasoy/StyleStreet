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
                    val categoryModel = CategoryResponse("0")
                    categoryModel.categoryId = category.data!!["Category_Id"].toString()
                    categoryModel.categoryName = category.data!!["Category_Name"].toString()
                    categoryModel.categoryImage = category.data!!["Category_Image"].toString()

                    categoryList.add(categoryModel)
                }
                return@let Resource.success(categoryList)
            }

        } catch (e: Exception){
            return Resource.error(e.message.toString(),null)
        }

    }
}