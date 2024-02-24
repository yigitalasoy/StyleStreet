package com.yigitalasoy.stylestreetapp.screens.SignIn

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.FragmentLoginBinding

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.imageViewEye.setOnClickListener {
            // if(binding.imageViewEye.background == resources.getDrawable(R.drawable.eye_hide)){
            //     binding.imageViewEye.setBackgroundResource(R.drawable.eye_show)
            // } else {
            //     binding.imageViewEye.setBackgroundResource(R.drawable.eye_hide)
            // }
            // Log.e("ERROR","basıldı")
            binding.apply {
                if(editTextPassword.transformationMethod.equals(HideReturnsTransformationMethod.getInstance())){
                    editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                    imageViewEye.setImageResource(R.drawable.eye_show)
                } else {
                    editTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    imageViewEye.setImageResource(R.drawable.eye_hide)
                }
            }


        }


    }






}