package com.yigitalasoy.stylestreetapp.ui.activity.edituser

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.ActivityEditUserBinding
import com.yigitalasoy.stylestreetapp.model.UserResponse
import com.yigitalasoy.stylestreetapp.util.Constants
import com.yigitalasoy.stylestreetapp.util.ObjectUtil
import com.yigitalasoy.stylestreetapp.util.downloadImage
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.util.toast
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditUserActivity : AppCompatActivity() {
    private var _binding: ActivityEditUserBinding? = null
    private val binding get() = _binding!!

    var gelenUserString: String? = null
    var gelenUser: UserResponse? = null

    var isSevenChar: Boolean = false
    var isBigChar: Boolean = false
    var isNumber: Boolean = false

    @Inject lateinit var userViewModel: UserViewModel

    var SELECT_PICTURE = 200
    //var isSelectedImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gelenUserString = intent.getStringExtra("selectedUser")
        gelenUser = ObjectUtil().jsonStringToObject(gelenUserString)

        println("gelen user: $gelenUser")
        println("gelen user string: $gelenUserString")

        if(gelenUser?.loginType.equals(Constants.GOOGLE_LOGIN_TYPE)){
            binding.constraintPassword.visibility = View.INVISIBLE
            //binding.constraintPassword.hide()
            binding.imageViewUser.isClickable = false
        } else {
            binding.imageViewUser.setOnClickListener {
                imageChooser()
            }
        }

        if(gelenUser != null){
            binding.apply {
                editTextEmail.setText(gelenUser?.email)
                editTextName.setText(gelenUser?.name)
                editTextSurname.setText(gelenUser?.surname)
                editTextTelephone.setText(gelenUser?.telephone)
                imageViewUser.downloadImage(gelenUser?.userImageURL)
            }
        }

        binding.apply {

            imageViewEye.setOnClickListener {
                hideShowPassword(editTextPassword,imageViewEye)
            }

            editTextPassword.doOnTextChanged { text, start, before, count ->
                passwordCheckRequirements()
            }

            imageViewBackButton.setOnClickListener {
                this@EditUserActivity.finish()
            }



            buttonSave.setOnClickListener {

                if((userViewModel.userLiveData.value?.data?.loginType == Constants.GOOGLE_LOGIN_TYPE) || (isSevenChar && isBigChar && isNumber)){
                    if(checkEmptyEditText()){
                        this@EditUserActivity.toast("Please fill all the fields")
                    } else {
                        val bitmap = (binding.imageViewUser.drawable as BitmapDrawable).bitmap

                        userViewModel.updateUser(UserResponse(
                            email = editTextEmail.text.toString(),
                            id = userViewModel.userLiveData.value?.data?.id,
                            name = editTextName.text.toString(),
                            surname = editTextSurname.text.toString(),
                            password = editTextPassword.text.toString(),
                            userImageURL = userViewModel.userLiveData.value?.data?.userImageURL,
                            telephone = editTextTelephone.text.toString()
                        ),this@EditUserActivity,bitmap)
                    }
                } else {
                    this@EditUserActivity.toast("Please check password requirements.")
                }
            }
        }
    }

    fun imageChooser() {
        val i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)
    }

    fun hideShowPassword(editText: EditText, imageView: ImageView){
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_PICTURE) {
                val selectedImageUri = data?.data
                if (selectedImageUri != null) {
                    binding.imageViewUser.setImageURI(selectedImageUri)
                } else {
                    println("uri seçilmedi")
                }
            }
        }
    }

    private fun checkEmptyEditText(): Boolean{
        binding.apply {
            if(
                (editTextEmail.text.toString().isNullOrEmpty() ||
                editTextName.text.toString().isNullOrEmpty() ||
                editTextSurname.text.toString().isNullOrEmpty() ||
                editTextTelephone.text.toString().isNullOrEmpty() || (gelenUser?.loginType.equals("password") && editTextPassword.text.toString().isNullOrEmpty()))
            ) {
                return true
            } else {
                return false
            }
        }

    }

    fun loading(state: Boolean){
        if(state){
            binding.progressBarSignupLoading.show()
        } else {
            binding.progressBarSignupLoading.hide()
        }
    }

    fun passwordCheckRequirements(){
        binding.apply {
            if ((editTextPassword.text.toString()!!.length >= 7)) {
                isSevenChar = true

                for (char in editTextPassword.text.toString()) {
                    if (char.isUpperCase()) {
                        isBigChar = true
                        break
                    } else {
                        isBigChar = false
                    }
                }

                for (char in editTextPassword.text.toString()) {
                    if (char.isDigit()) {
                        isNumber = true
                        break
                    } else {
                        isNumber = false
                    }
                }
            } else {
                isSevenChar = false
            }

            if (isSevenChar && isBigChar && isNumber) {
                editTextPassword.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.green))
                textViewPasswordRequirements.setTextColor(resources.getColor(R.color.green))
            } else {
                editTextPassword.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.red))
                textViewPasswordRequirements.setTextColor(resources.getColor(R.color.red))

            }
        }

    }

}