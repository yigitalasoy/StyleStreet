package com.yigitalasoy.stylestreetapp.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.model.UserResponse
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject


class UserRepositoryImp(auth: FirebaseAuth,db: FirebaseFirestore): UserRepository {
    override fun userLogin(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun getUserDataFromDatabase(userUid: String?) {
        TODO("Not yet implemented")
    }

    override fun userSignUp(user: UserResponse) {
        TODO("Not yet implemented")
    }

    override fun registerUserDatabase(user: UserResponse) {
        TODO("Not yet implemented")
    }

    override fun loginWithGoogle() {
        TODO("Not yet implemented")
    }
}