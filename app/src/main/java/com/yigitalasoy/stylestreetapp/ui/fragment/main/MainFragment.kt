package com.yigitalasoy.stylestreetapp.ui.fragment.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.FragmentLoginBinding
import com.yigitalasoy.stylestreetapp.databinding.FragmentMainBinding
import com.yigitalasoy.stylestreetapp.ui.activity.login.LoginActivity
import com.yigitalasoy.stylestreetapp.ui.activity.main.MainActivity
import com.yigitalasoy.stylestreetapp.util.Resource
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _mainFragmentBinding: FragmentMainBinding? = null
    private val mainFragmentBinding get() = _mainFragmentBinding!!

    val userViewModel: UserViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _mainFragmentBinding = FragmentMainBinding.inflate(inflater,container,false)
        val view = mainFragmentBinding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainFragmentBinding.buttonCikisYap.setOnClickListener {
            Firebase.auth.signOut()

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.googleServerClientId))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(it.context,gso)
            googleSignInClient.signOut().addOnCompleteListener {
                Log.e("GOOGLE SIGN OUT","SUCCUSFULLY SIGN OUT")
            }
            userViewModel.isLogin.value = Resource.success(false)
        }

        observer()

    }

    private fun observer() {
        userViewModel.isLogin.observe(viewLifecycleOwner){
            if(!it.data!!){
                //val loginActivity
                Log.e("FİREBASE GOOGLE LOGİN","SUCCESFULLY SIGNOUT")


/*
                val preferences = getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
                val editor = preferences.edit()
                editor.clear()
                editor.apply()
*/

                val loginActivity = Intent(activity, LoginActivity::class.java)
                startActivity(loginActivity)
                activity?.finish()
            }
        }
    }

}