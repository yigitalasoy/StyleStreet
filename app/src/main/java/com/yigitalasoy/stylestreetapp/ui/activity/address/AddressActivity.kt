package com.yigitalasoy.stylestreetapp.ui.activity.address

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yigitalasoy.stylestreetapp.databinding.ActivityAddressBinding
import com.yigitalasoy.stylestreetapp.model.AddressResponse
import com.yigitalasoy.stylestreetapp.ui.activity.basket.BasketActivity
import com.yigitalasoy.stylestreetapp.util.ItemClickListener
import com.yigitalasoy.stylestreetapp.util.Status
import com.yigitalasoy.stylestreetapp.viewmodel.AddressViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding

    @Inject lateinit var addressViewModel: AddressViewModel
    @Inject lateinit var userViewModel: UserViewModel

    lateinit var addressAdapter: AddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addressViewModel.getAddressData(userViewModel.userLiveData.value?.data?.id!!)


        addressAdapter = AddressAdapter(arrayListOf(),object : ItemClickListener{
            override fun onItemClick(Item: Any) {
                if((Item as AddressResponse).addressId != null){

                    val basketActivity = Intent(this@AddressActivity,BasketActivity::class.java)
                    basketActivity.putExtra("selectedAddressId",Item.addressId)
                    this@AddressActivity.startActivity(basketActivity)
                    this@AddressActivity.finish()
                }

            }
        })

        binding.apply {
            recyclerViewAddress.apply {
                adapter = addressAdapter
                layoutManager = LinearLayoutManager(this@AddressActivity,RecyclerView.VERTICAL,false)
            }

            fabBack.setOnClickListener {
                super.onBackPressed()
            }

            textViewAddAddress.setOnClickListener {
                val addAddressActivity = Intent(this@AddressActivity,AddAddressActivity::class.java)
                this@AddressActivity.startActivity(addAddressActivity)
            }
        }
        observe()
    }

    private fun observe() {
        addressViewModel.addressLiveData.observe(this){

            when(it.status){
                Status.SUCCESS -> {
                    Log.i("address live data succes",it.data.toString())
                    if(it.data != null){
                        addressAdapter.updateAddressList(it.data)
                    }
                }
                Status.ERROR -> {
                    Log.i("address live data error",it.message.toString())
                }
                Status.LOADING -> {
                    Log.i("address live data loading","")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("address destroyed")
    }
}