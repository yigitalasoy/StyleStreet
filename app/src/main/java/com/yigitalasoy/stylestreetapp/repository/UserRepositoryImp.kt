package com.yigitalasoy.stylestreetapp.repository

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.errorprone.annotations.Var
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.model.UserResponse
import com.yigitalasoy.stylestreetapp.util.Constants
import com.yigitalasoy.stylestreetapp.util.Resource
import com.yigitalasoy.stylestreetapp.util.mapToObject
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import java.util.Objects
import javax.inject.Inject


class UserRepositoryImp(val auth: FirebaseAuth,val db: FirebaseFirestore): UserRepository {
    override suspend fun userLogin(email: String, password: String): Resource<FirebaseUser?> {
        val firebaseUser: FirebaseUser?

        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                Resource.success(firebaseUser)
                Log.d("fun userLogin", "signInWithEmail:success start")
                firebaseUser?.let {
                    return@let Resource.success(auth.currentUser)
                } ?: Resource.error("Repository signInWithEmail error",null)

            } else {
                Resource.error("Repository signInWithEmail error",null)
            }
        } catch (e: Exception){
            Resource.error("Repository signInWithEmail error : ${e.message}",null)
        }

    }

    override suspend fun getUserDataFromDatabase(userUid: String?): Resource<UserResponse> {

        return try {
            val docRef = db.collection(Constants.FIRESTORE_DATABASE_USERS).document(userUid.toString())
            val document = docRef.get().await()


            if (document != null) {
                Log.e("getUserDataFromDatabase succes","succes user: ${document.data?.mapToObject().toString()}")
                Resource.success(document.data?.mapToObject())
            } else {
                Log.d(ContentValues.TAG, "No such document")
                Resource.error("getUserDataFromDatabase error :",null)

            }
        } catch (e: Exception){
            Resource.error("getUserDataFromDatabase error :",null)
        }

    }

    override suspend fun userSignUp(user: UserResponse): Resource<UserResponse> {


        return try {

            val task = auth.createUserWithEmailAndPassword(user.email.toString(), user.password.toString())
            task.await()

            if (task.isSuccessful) {
                Log.e("error user sign up", "createUserWithEmail:success")
                val firebaseUser = auth.currentUser
                println("REGISTERED USER UUID: ${firebaseUser?.uid}")

                user.id = firebaseUser?.uid
                registerUserDatabase(user)

                Resource.success(user)

            } else {
                Log.e(ContentValues.TAG, "createUserWithEmail:failure")
                Resource.error("repository userSignUp error",null)
            }

        } catch (e: Exception){
            Resource.error("repository userSignUp error: ${e.message}",null)
        }



/*
        return try {

            val task = auth.createUserWithEmailAndPassword(user.email.toString(), user.password.toString()).await()

            if (task != null) {
                Log.e("error user sign up", "createUserWithEmail:success")
                val firebaseUser = auth.currentUser
                println("REGISTERED USER UUID: ${firebaseUser?.uid}")

                user.id = firebaseUser?.uid
                registerUserDatabase(user)

                Resource.success(user)

            } else {
                Log.e(ContentValues.TAG, "createUserWithEmail:failure")
                Resource.error("repository userSignUp error",null)

            }

        } catch (e: Exception){
            Resource.error("repository userSignUp error: ${e.message}",null)
        }

 */
    }

    override suspend fun registerUserDatabase(user: UserResponse): Resource<UserResponse> {

        return try {
            val doc = db.collection(Constants.FIRESTORE_DATABASE_USERS).document(user.id.toString()).set(user)
            doc.await()

            if(doc.isSuccessful){
                println("USER SUCCESFULLY ADDED DATABASE: ${user.id}")
                Resource.success(user)
            } else {
                Log.e("ERROR", doc.exception!!.message!!)
                Resource.error("Error",null)
            }

        } catch (e: Exception){
            Resource.error("Error ${e.message}",null)
        }






    }

    override suspend fun loginWithGoogle(account: GoogleSignInAccount?): Resource<UserResponse> {

        return try {

            val credential = GoogleAuthProvider.getCredential(account?.idToken,null)
            val googleLogin = FirebaseAuth.getInstance().signInWithCredential(credential)
            googleLogin.await()

            if(googleLogin.isSuccessful){
                Log.e("GOOGLE SIGN IN","google sign in success")
                account!!.let {
                    return@let Resource.success(UserResponse("",account.displayName.toString(),account.familyName.toString(),account.email.toString(),""))
                }
            } else {
                Log.e("GOOGLE SIGN IN ERROR",googleLogin.exception?.message.toString())
                Resource.error(googleLogin.exception?.message.toString(),null)

            }

        } catch (e: Exception){
            Resource.error(e.message.toString(),null)
        }



    }
}