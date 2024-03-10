package com.yigitalasoy.stylestreetapp.ui.fragment.login

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.FragmentLoginBinding
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var binding: FragmentLoginBinding? = null
    private lateinit var userViewModel: UserViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        init()


        binding?.apply {
            textViewSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }

            imageViewEye.setOnClickListener {
                if(editTextPassword.transformationMethod.equals(HideReturnsTransformationMethod.getInstance())){
                    editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                    imageViewEye.setImageResource(R.drawable.eye_show)
                } else {
                    editTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    imageViewEye.setImageResource(R.drawable.eye_hide)
                }
            }

            buttonLogin.setOnClickListener {
                userViewModel.userLogin(editTextEmail.text.toString(),editTextPassword.text.toString())
            }

            buttonLoginWithGoogle.setOnClickListener {

            }

        }

        observer()

    }

    private fun init() {
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

    }

    fun observer(){

        userViewModel.userLiveData.observe(viewLifecycleOwner){
            it?.let {
                Toast.makeText(context,"Login succesfully", Toast.LENGTH_LONG).show()
            }
        }

        userViewModel.isLogin.observe(viewLifecycleOwner){
            it?.let {
                //false olduğu zaman çıkış yapılmış olacak
            }
        }

        userViewModel.userError.observe(viewLifecycleOwner){
            it?.let {
                if(it){
                    binding?.textViewError?.visibility = View.VISIBLE
                } else {
                    binding?.textViewError?.visibility = View.INVISIBLE
                }
            }
        }

        userViewModel.userLoading.observe(viewLifecycleOwner){
            it?.let {
                if(it){
                    binding?.progressBarLoading?.visibility = View.VISIBLE
                } else {
                    binding?.progressBarLoading?.visibility = View.INVISIBLE
                }
            }
        }




    }




}