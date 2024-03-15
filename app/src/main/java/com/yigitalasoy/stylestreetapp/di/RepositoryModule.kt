package com.yigitalasoy.stylestreetapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

}