package com.yigitalasoy.stylestreetapp.di

import com.yigitalasoy.stylestreetapp.repository.CategoryRepository
import com.yigitalasoy.stylestreetapp.repository.ProductColorRepository
import com.yigitalasoy.stylestreetapp.repository.ProductRepository
import com.yigitalasoy.stylestreetapp.repository.ProductSizeRepository
import com.yigitalasoy.stylestreetapp.repository.UserRepository
import com.yigitalasoy.stylestreetapp.viewmodel.CategoryViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductColorViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductSizeViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
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
        userRepository: UserRepository
    ): UserViewModel {
        return UserViewModel(userRepository)
    }


}