package com.yigitalasoy.stylestreetapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.yigitalasoy.stylestreetapp.model.UserResponse
import com.yigitalasoy.stylestreetapp.repository.UserRepository
import com.yigitalasoy.stylestreetapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(val userRepository: UserRepository): ViewModel() {

    val userLiveData = MutableLiveData<Resource<UserResponse>>()
    val userLoading = MutableLiveData<Resource<Boolean>>()
    val userError = MutableLiveData<Resource<Boolean>>()
    val isLogin = MutableLiveData<Resource<Boolean>>()

    private var job : Job? = null


    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        liveDataError(throwable.localizedMessage ?: "error!",data = true)
    }



    fun userLogin(email: String,password: String){

        liveDataLoading(true)
        liveDataError(data = false)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            //IO'da sunucu işlemleri yapılır
            val firebaseUser = userRepository.userLogin(email,password)
            Log.e("FIREBASE LOGIN: ","${firebaseUser.message} ID=${firebaseUser.data?.uid}")
            val databaseUser = userRepository.getUserDataFromDatabase(firebaseUser.data?.uid)
            Log.e("DATABASE LOGIN: ","${databaseUser.message} ID=${databaseUser.data?.id}")

            withContext(Dispatchers.Main){
                if(firebaseUser.data==null || databaseUser.data==null){
                    liveDataError("Error", true)
                    liveDataLoading(false)
                } else {
                    userLiveData.value = databaseUser
                    isLogin.value = Resource.success(true)
                    liveDataLoading(false)
                    liveDataError("userLogin", false)
                }
            }
        }

        //giriş işlemi yapılacak
        //giriş doğru ise database'deki kullanıcı verilerini de alıp userLiveData' ya aktarılacak.
    }

    fun userSignUp(user: UserResponse){
        //kayıt işlemi yapılacak
        liveDataLoading(true)
        liveDataError(data = false)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            //IO'da sunucu işlemleri yapılır
            val registeredUser = userRepository.userSignUp(user)

            withContext(Dispatchers.Main){
                if(registeredUser.data != null){
                    liveDataError("REGISTER SUCCESS",false)
                    liveDataLoading(data = false)
                } else {
                    liveDataError("Error Firebase register: ${registeredUser.message}",true)
                }
            }
        }
    }


    fun loginWithGoogle(account: GoogleSignInAccount?) {
        liveDataLoading(true)
        liveDataError(data = false)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            //IO'da sunucu işlemleri yapılır
            val googleLoginUser = userRepository.loginWithGoogle(account)

            withContext(Dispatchers.Main){
                if(googleLoginUser.data != null){
                    Log.e("REGISTER SUCCESS","user email: ${googleLoginUser.data}")
                    liveDataError("REGISTER SUCCESS",false)
                    liveDataLoading(false)
                    userLiveData.value = googleLoginUser
                    isLogin.value = Resource.success(true)
                } else {
                    liveDataError("Error Firebase register: ${googleLoginUser.message}",true)
                }
            }
        }
    }

    private fun liveDataLoading(data: Boolean) {
        userLoading.value = Resource.loading(data)
    }

    private fun liveDataError(message: String? = "",data: Boolean) {
        userError.value = Resource.error("Error: $message",data)
    }






}