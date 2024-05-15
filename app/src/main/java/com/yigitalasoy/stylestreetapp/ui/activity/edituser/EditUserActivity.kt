package com.yigitalasoy.stylestreetapp.ui.activity.edituser

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.yigitalasoy.stylestreetapp.databinding.ActivityEditUserBinding
import com.yigitalasoy.stylestreetapp.model.UserResponse
import com.yigitalasoy.stylestreetapp.util.Constants
import com.yigitalasoy.stylestreetapp.util.ObjectUtil
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

    @Inject lateinit var userViewModel: UserViewModel


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
        }

        if(gelenUser != null){
            binding.apply {
                editTextEmail.setText(gelenUser?.email)
                editTextName.setText(gelenUser?.name)
                editTextSurname.setText(gelenUser?.surname)
                editTextTelephone.setText(gelenUser?.telephone)
            }
        }

        binding.apply {

            imageViewBackButton.setOnClickListener {
                this@EditUserActivity.finish()
            }

            buttonSave.setOnClickListener {

                if(checkEmptyEditText()){
                    this@EditUserActivity.toast("Please fill all the fields")
                } else {
                    userViewModel.updateUser(UserResponse(
                        email = editTextEmail.text.toString(),
                        id = userViewModel.userLiveData.value?.data?.id,
                        name = editTextName.text.toString(),
                        surname = editTextSurname.text.toString(),
                        password = editTextPassword.text.toString(),
                        userImageURL = "",
                        telephone = editTextTelephone.text.toString()
                    ),this@EditUserActivity)
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

}