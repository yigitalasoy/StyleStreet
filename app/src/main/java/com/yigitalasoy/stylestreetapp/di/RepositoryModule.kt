package com.yigitalasoy.stylestreetapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.repository.CategoryRepository
import com.yigitalasoy.stylestreetapp.repository.CategoryRepositoryImp
import com.yigitalasoy.stylestreetapp.repository.ProductColorRepository
import com.yigitalasoy.stylestreetapp.repository.ProductColorRepositoryImp
import com.yigitalasoy.stylestreetapp.repository.ProductRepository
import com.yigitalasoy.stylestreetapp.repository.ProductRepositoryImp
import com.yigitalasoy.stylestreetapp.repository.ProductSizeRepository
import com.yigitalasoy.stylestreetapp.repository.ProductSizeRepositoryImp
import com.yigitalasoy.stylestreetapp.repository.UserRepository
import com.yigitalasoy.stylestreetapp.repository.UserRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun getProvidesUserRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): UserRepository{
        return UserRepositoryImp(auth = firebaseAuth,db=firestore)
    }

    @Provides
    @Singleton
    fun getProvidesCategoryRepository(
        firebaseFirestore: FirebaseFirestore
    ): CategoryRepository{
        return CategoryRepositoryImp(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun getProvidesProductColorRepository(
        firebaseFirestore: FirebaseFirestore
    ): ProductColorRepository{
        return ProductColorRepositoryImp(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun getProvidesProductSizeRepository(
        firebaseFirestore: FirebaseFirestore
    ): ProductSizeRepository{
        return ProductSizeRepositoryImp(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun getProvidesProductRepository(
        firebaseFirestore: FirebaseFirestore,
        productColorRepository: ProductColorRepository
    ): ProductRepository{
        return ProductRepositoryImp(productColorRepository,firebaseFirestore)
    }
}