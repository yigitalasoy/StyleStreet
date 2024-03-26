package com.yigitalasoy.stylestreetapp.repository

import com.yigitalasoy.stylestreetapp.model.CategoryResponse
import com.yigitalasoy.stylestreetapp.util.Resource

interface CategoryRepository {

    suspend fun getAllCategories(): Resource<ArrayList<CategoryResponse>>

}