package com.yigitalasoy.stylestreetapp.screens.Signup

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.compose.ui.graphics.Color
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var isSevenChar: Boolean = false
        var isBigChar: Boolean = false
        var isNumber: Boolean = false
        var emailValidation: Boolean = false

        binding.apply {
            imageViewBackButton.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }
            imageViewEye.setOnClickListener {
                hideShowPassword(editTextPassword,imageViewEye)
            }
            imageViewEye2.setOnClickListener {
                hideShowPassword(editTextConfirmPassword,imageViewEye2)
            }
            editTextConfirmPassword.doOnTextChanged { text, start, before, count ->
                if(editTextPassword.text.toString() != editTextConfirmPassword.text.toString()){
                    editTextConfirmPassword.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.red))
                } else {
                    editTextConfirmPassword.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green))

                }
            }
            editTextPassword.doOnTextChanged { text, start, before, count ->
                if((text!!.length >= 7)){
                    isSevenChar = true

                    for (char in text){
                        if(char.isUpperCase()){
                            isBigChar=true
                            break
                        } else {
                            isBigChar=false
                        }
                    }

                    for (char in text){
                        if(char.isDigit()){
                            isNumber=true
                            break
                        } else {
                            isNumber=false
                        }
                    }
                } else {
                    isSevenChar = false
                }

                if(isSevenChar && isBigChar && isNumber){
                    editTextPassword.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green))
                    textViewPasswordRequirements.setTextColor(resources.getColor(R.color.green))
                } else {
                    editTextPassword.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.red))
                    textViewPasswordRequirements.setTextColor(resources.getColor(R.color.red))

                }

            }

            editTextEmail.doOnTextChanged { text, start, before, count ->

                val emailRegex = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"

                emailValidation = editTextEmail.text.toString().matches(emailRegex.toRegex())

                if(emailValidation){
                    editTextEmail.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green))
                } else {
                    editTextEmail.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.red))
                }

            }

        }
    }

    fun hideShowPassword(editText: EditText,imageView: ImageView){
        Log.e("ERROR","basıldı")
        binding.apply {
            if(editText.transformationMethod.equals(HideReturnsTransformationMethod.getInstance())){
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
                imageView.setImageResource(R.drawable.eye_show)
            } else {
                editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                imageView.setImageResource(R.drawable.eye_hide)
            }
        }
    }

}