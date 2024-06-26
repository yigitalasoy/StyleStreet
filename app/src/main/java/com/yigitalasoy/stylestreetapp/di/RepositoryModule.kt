package com.yigitalasoy.stylestreetapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.yigitalasoy.stylestreetapp.repository.AddressRepository
import com.yigitalasoy.stylestreetapp.repository.AddressRepositoryImp
import com.yigitalasoy.stylestreetapp.repository.BasketRepository
import com.yigitalasoy.stylestreetapp.repository.BasketRepositoryImp
import com.yigitalasoy.stylestreetapp.repository.CategoryRepository
import com.yigitalasoy.stylestreetapp.repository.CategoryRepositoryImp
import com.yigitalasoy.stylestreetapp.repository.NotificationRepository
import com.yigitalasoy.stylestreetapp.repository.NotificationRepositoryImp
import com.yigitalasoy.stylestreetapp.repository.ProductColorRepository
import com.yigitalasoy.stylestreetapp.repository.ProductColorRepositoryImp
import com.yigitalasoy.stylestreetapp.repository.ProductRepository
import com.yigitalasoy.stylestreetapp.repository.ProductRepositoryImp
import com.yigitalasoy.stylestreetapp.repository.ProductSizeRepository
import com.yigitalasoy.stylestreetapp.repository.ProductSizeRepositoryImp
import com.yigitalasoy.stylestreetapp.repository.SoldRepository
import com.yigitalasoy.stylestreetapp.repository.SoldRepositoryImp
import com.yigitalasoy.stylestreetapp.repository.UserRepository
import com.yigitalasoy.stylestreetapp.repository.UserRepositoryImp
import com.yigitalasoy.stylestreetapp.repository.WishListRepository
import com.yigitalasoy.stylestreetapp.repository.WishListRepositoryImp
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
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): UserRepository{
        return UserRepositoryImp(auth = firebaseAuth, db = firestore , storage = storage)
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
        firebaseFirestore: FirebaseFirestore
    ): ProductRepository{
        return ProductRepositoryImp(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun getProvidesBasketRepository(
        firebaseFirestore: FirebaseFirestore
    ): BasketRepository{
        return BasketRepositoryImp(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun getProvidesNotificationRepository(
        firebaseFirestore: FirebaseFirestore
    ): NotificationRepository{
        return NotificationRepositoryImp(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun getProvidesWishListRepository(
        firebaseFirestore: FirebaseFirestore
    ): WishListRepository{
        return WishListRepositoryImp(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun getProvidesAddressRepository(
        firebaseFirestore: FirebaseFirestore
    ): AddressRepository{
        return AddressRepositoryImp(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun getProvidesSoldRepository(
        firebaseFirestore: FirebaseFirestore
    ): SoldRepository{
        return SoldRepositoryImp(firebaseFirestore)
    }

}