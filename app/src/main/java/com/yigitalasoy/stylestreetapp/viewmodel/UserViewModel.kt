package com.yigitalasoy.stylestreetapp.viewmodel

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContextCompat.getString
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.model.UserResponse
import com.yigitalasoy.stylestreetapp.repository.UserRepository
import com.yigitalasoy.stylestreetapp.ui.activity.edituser.EditUserActivity
import com.yigitalasoy.stylestreetapp.util.Constants
import com.yigitalasoy.stylestreetapp.util.Resource
import com.yigitalasoy.stylestreetapp.util.Status
import com.yigitalasoy.stylestreetapp.util.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(val userRepository: UserRepository,val auth: FirebaseAuth,val db: FirebaseFirestore): ViewModel() {

    val userLiveData = MutableLiveData<Resource<UserResponse>>()
    //val userLoading = MutableLiveData<Resource<Boolean>>()
    //val userError = MutableLiveData<Resource<Boolean>>()
    //val isLogin = MutableLiveData<Resource<Boolean>>()

    private var job : Job? = null

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        userLiveData.value = Resource.error(throwable.localizedMessage ?: "error!",data = null)
    }



    fun userLogin(email: String,password: String){

        userLiveData.value = Resource.loading(null)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            //IO'da sunucu işlemleri yapılır
            val firebaseUser = userRepository.userLogin(email,password)
            Log.e("FIREBASE LOGIN: ","${firebaseUser.message} ID=${firebaseUser.data?.uid}")
            val databaseUser = userRepository.getUserDataFromDatabase(firebaseUser.data?.uid)
            Log.e("DATABASE LOGIN: ","${databaseUser.message} ID=${databaseUser.data?.id}")

            withContext(Dispatchers.Main){

                when (firebaseUser.status) {
                    Status.SUCCESS ->{
                        if(databaseUser.status == Status.SUCCESS){
                            //login type = password
                            databaseUser.data?.loginType = Constants.PASSWORD_LOGIN_TYPE
                            userLiveData.value = databaseUser
                            Log.i("User login oldu: ","${userLiveData.value!!.data}")
                        } else {
                            userLiveData.value = Resource.error(databaseUser.message.toString(),null)
                        }
                    }

                    Status.ERROR -> {
                        userLiveData.value = Resource.error(firebaseUser.message.toString(),null)
                    }
                    Status.LOADING -> {
                        userLiveData.value = Resource.loading(null)
                    }
                }
            }
        }
    }

    fun userSignUp(user: UserResponse,photoBitmap: Bitmap){
        userLiveData.value = Resource.loading(null)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val registeredUser = userRepository.userSignUp(user, photoBitmap)

            withContext(Dispatchers.Main){
                when (registeredUser.status) {
                    Status.SUCCESS ->{
                        println("user success")
                        userLiveData.value = Resource.success(registeredUser.data)
                    }

                    Status.ERROR -> {
                        userLiveData.value = Resource.error(registeredUser.message.toString(),null)
                    }

                    else -> {
                        println("else")
                    }
                }
            }
        }
    }


    fun loginWithGoogle(account: GoogleSignInAccount?) {
        userLiveData.value = Resource.loading(null)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            //IO'da sunucu işlemleri yapılır
            val googleLoginUser = userRepository.loginWithGoogle(account)

            withContext(Dispatchers.Main){
                when(googleLoginUser.status){
                    Status.SUCCESS -> {
                        if(googleLoginUser.data != null){
                            Log.e("REGISTER SUCCESS","user email: ${googleLoginUser.data}")
                            //login type = google
                            googleLoginUser.data.loginType = Constants.GOOGLE_LOGIN_TYPE
                            userLiveData.value = Resource.success(googleLoginUser.data)
                            println("user live data değişti: ${userLiveData.value!!.data} ${userLiveData.value!!.data?.loginType}")
                            //isLogin.value = Resource.success(true)
                        }
                    }
                    Status.ERROR -> {
                        userLiveData.value = Resource.error(googleLoginUser.message.toString(),null)
                    }
                    else -> {
                        println("else")
                    }
                }

            }
        }
    }

    fun signOut(context: Context){

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(context,R.string.googleServerClientId))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context,gso)
        googleSignInClient.signOut().addOnCompleteListener {
            Log.e("GOOGLE SIGN OUT","SUCCUSFULLY SIGN OUT")
        }

        auth.signOut()
        userLiveData.value = Resource.success(null)

    }

    fun sendResetPasswordLink(email: String, fragment: Fragment){

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val resetPasswordState = userRepository.resetPassword(email)

            withContext(Dispatchers.Main){
                if(resetPasswordState){
                    Log.e("LINK SEND","user email: ${email}")

                    println("LINK SEND: ${email}")
                } else {
                    println("LINK error: ${email}")
                }
            }
        }

    }

    fun updateUser(user: UserResponse, activity: Activity, bitMap: Bitmap){
        user.loginType = userLiveData.value!!.data?.loginType
        activity as EditUserActivity
        activity.loading(true)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val updateUserHash = userRepository.updateUser(user,activity,bitMap)
            println("gelen hash: $updateUserHash")
            withContext(Dispatchers.Main){
                activity.loading(false)
                if(updateUserHash != null){
                    if((updateUserHash["Task"] as Boolean)){
                        Log.e("USER UPDATE","SUCCESS")
                        user.userImageURL = (updateUserHash["URL"] as String)
                        println("success olacak user: $user")
                        userLiveData.value = Resource.success(user)
                        activity.toast("Update successfully!")
                        activity.onBackPressed()
                        activity.recreate()
                    } else {
                        activity.toast("Update failed! ${(updateUserHash["Task"] as Task<*>).exception?.message.toString()}")
                    }

                } else {
                    print(updateUserHash)
                    Log.e("USER UPDATE","FAIL")
                    activity.toast("Update failed!")
                }

            }
        }

    }
}