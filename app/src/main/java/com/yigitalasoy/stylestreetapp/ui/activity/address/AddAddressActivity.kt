package com.yigitalasoy.stylestreetapp.ui.activity.address

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yigitalasoy.stylestreetapp.databinding.ActivityAddAddressBinding
import com.yigitalasoy.stylestreetapp.model.AddressResponse
import com.yigitalasoy.stylestreetapp.util.ObjectUtil
import com.yigitalasoy.stylestreetapp.util.Resource
import com.yigitalasoy.stylestreetapp.util.Status
import com.yigitalasoy.stylestreetapp.util.toast
import com.yigitalasoy.stylestreetapp.viewmodel.AddressViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddAddressActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddAddressBinding

    @Inject lateinit var addressViewModel: AddressViewModel
    @Inject lateinit var userViewModel: UserViewModel

    var gelenAddressString: String? = null
    var gelenAddress: AddressResponse? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        gelenAddressString = null
        gelenAddress = null

        gelenAddressString = intent.getStringExtra("selectedAddress")
        gelenAddress = ObjectUtil().jsonStringToAddress(gelenAddressString)


        if(gelenAddress != null){
            println("gelen: $gelenAddress")
            binding.apply {
                binding.buttonSaveAddress.text = "Save address"

                editTextAddressDetail.setText(gelenAddress!!.addressDetail)
                editTextAddressHeader.setText(gelenAddress!!.addressHeader)
                editTextAddressDistrict.setText(gelenAddress!!.addressDistrict)
                editTextAddressProvince.setText(gelenAddress!!.addressProvince)
                editTextAddressTel.setText(gelenAddress!!.telNumber)
                editTextAddressName.setText(gelenAddress!!.addressName)
                editTextAddressSurname.setText(gelenAddress!!.addressSurname)
            }

        } else {
            println("gelen adress null geldi")
        }

        binding.apply {
            fabBack.setOnClickListener {
                super.onBackPressed()
                this@AddAddressActivity.finish()
            }

            buttonSaveAddress.setOnClickListener {
                val sameHeaderName: AddressResponse?
                if(gelenAddress == null){
                    sameHeaderName = addressViewModel.addressLiveData.value?.data?.find { it.addressHeader == editTextAddressHeader.text.toString() }
                } else {
                    sameHeaderName = null
                }

                if(sameHeaderName != null){
                    this@AddAddressActivity.toast("Already have the same address header. Please change address header.")
                } else {
                    var newAddress = AddressResponse(
                        userViewModel.userLiveData.value?.data?.id,
                        null,
                        editTextAddressDetail.text.toString(),
                        editTextAddressHeader.text.toString(),
                        editTextAddressDistrict.text.toString(),
                        editTextAddressProvince.text.toString(),
                        editTextAddressTel.text.toString(),
                        editTextAddressName.text.toString(),
                        editTextAddressSurname.text.toString()
                    )
                    if(gelenAddress != null){
                        newAddress.addressId = gelenAddress!!.addressId
                    }
                    addressViewModel.addAddress(newAddress)
                }

            }
        }

        observe()

    }

    fun observe(){
        addressViewModel.newAddressLiveData.observe(this){newAddress ->

            when(newAddress.status){
                Status.SUCCESS -> {

                    if(newAddress.data != null){
                        this.toast("Address succesfully added: ${newAddress.data.addressHeader}")
                        if(gelenAddress != null){
                            addressViewModel.addressLiveData.value?.data?.remove(gelenAddress)
                        }
                        addressViewModel.addressLiveData.value?.data?.add(newAddress.data)

                        addressViewModel.addressLiveData.value = Resource.success(addressViewModel.addressLiveData.value?.data)

                        addressViewModel.newAddressLiveData.value = Resource.success(null)
                        this.finish()

                    }

                }
                Status.ERROR -> {
                    println("new address error: ${newAddress.message.toString()}")
                }
                Status.LOADING -> {
                    println("new address loading")
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("addaddress destroyed")
    }

}