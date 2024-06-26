package com.yigitalasoy.stylestreetapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.repository.AddressRepository
import com.yigitalasoy.stylestreetapp.repository.BasketRepository
import com.yigitalasoy.stylestreetapp.repository.CategoryRepository
import com.yigitalasoy.stylestreetapp.repository.NotificationRepository
import com.yigitalasoy.stylestreetapp.repository.ProductColorRepository
import com.yigitalasoy.stylestreetapp.repository.ProductRepository
import com.yigitalasoy.stylestreetapp.repository.ProductSizeRepository
import com.yigitalasoy.stylestreetapp.repository.SoldRepository
import com.yigitalasoy.stylestreetapp.repository.UserRepository
import com.yigitalasoy.stylestreetapp.repository.WishListRepository
import com.yigitalasoy.stylestreetapp.viewmodel.AddressViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.BasketViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.CategoryViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.NotificationViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductColorViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductSizeViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.SoldViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.WishListViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {
    @Provides
    @Singleton
    fun getProvidesProductViewModel(
        productRepository: ProductRepository
    ): ProductViewModel {
        return ProductViewModel(productRepository)
    }

    @Provides
    @Singleton
    fun getProvidesProductSizeViewModel(
        productSizeRepository: ProductSizeRepository
    ): ProductSizeViewModel {
        return ProductSizeViewModel(productSizeRepository)
    }

    @Provides
    @Singleton
    fun getProvidesProductColorViewModel(
        productColorRepository: ProductColorRepository
    ): ProductColorViewModel {
        return ProductColorViewModel(productColorRepository)
    }

    @Provides
    @Singleton
    fun getProvidesCategoryViewModel(
        categoryRepository: CategoryRepository
    ): CategoryViewModel {
        return CategoryViewModel(categoryRepository)
    }

    @Provides
    @Singleton
    fun getProvidesUserViewModel(
        userRepository: UserRepository,
        auth: FirebaseAuth,
        db: FirebaseFirestore
    ): UserViewModel {
        return UserViewModel(userRepository,auth,db)
    }

    @Provides
    @Singleton
    fun getProvidesBasketViewModel(
        basketRepository: BasketRepository
    ): BasketViewModel {
        return BasketViewModel(basketRepository)
    }

    @Provides
    @Singleton
    fun getProvidesNotificationViewModel(
        notificationRepository: NotificationRepository
    ): NotificationViewModel {
        return NotificationViewModel(notificationRepository)
    }

    @Provides
    @Singleton
    fun getProvidesWishListViewModel(
        wishListRepository: WishListRepository
    ): WishListViewModel {
        return WishListViewModel(wishListRepository)
    }


    @Provides
    @Singleton
    fun getProvidesAddressViewModel(
        addressRepository: AddressRepository
    ): AddressViewModel {
        return AddressViewModel(addressRepository)
    }

    @Provides
    @Singleton
    fun getProvidesSoldViewModel(
        soldRepository: SoldRepository
    ): SoldViewModel {
        return SoldViewModel(soldRepository)
    }


}