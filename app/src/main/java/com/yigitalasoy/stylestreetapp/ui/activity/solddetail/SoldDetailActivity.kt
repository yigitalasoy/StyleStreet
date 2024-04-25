package com.yigitalasoy.stylestreetapp.ui.activity.solddetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yigitalasoy.stylestreetapp.databinding.ActivitySoldDetailBinding
import com.yigitalasoy.stylestreetapp.model.SoldResponse
import com.yigitalasoy.stylestreetapp.model.SubProductResponse
import com.yigitalasoy.stylestreetapp.util.ObjectUtil
import com.yigitalasoy.stylestreetapp.viewmodel.AddressViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.SoldViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SoldDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivitySoldDetailBinding

    @Inject lateinit var soldViewModel: SoldViewModel
    @Inject lateinit var productViewModel: ProductViewModel
    @Inject lateinit var addressViewModel: AddressViewModel

    val soldProductsAdapter = SoldProductsAdapter(arrayListOf(), arrayListOf())

    var gelenSoldString: String? = null
    var gelenSold: SoldResponse? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoldDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            fabBack.setOnClickListener {
                this@SoldDetailActivity.finish()
            }

            recyclerViewOrderItems.apply {
                adapter = soldProductsAdapter
                layoutManager = LinearLayoutManager(this@SoldDetailActivity,RecyclerView.VERTICAL,false)
            }

        }

        gelenSoldString = intent.getStringExtra("selectedSold")
        gelenSold = ObjectUtil().jsonStringToSold(gelenSoldString)

        if(gelenSold != null){
            println("null gelmedi $gelenSold")
            binding.apply {
                textViewDeliveryStatus.text = gelenSold?.delivertStatus
                textViewAddress.text = addressViewModel.addressLiveData.value?.data?.find { it.addressId == gelenSold?.addressId }?.addressDetail
                textViewOrderId.text = gelenSold?.soldId
                println("adres: ${addressViewModel.addressLiveData.value?.data?.find { it.addressId == gelenSold?.addressId }?.addressDetail}")
                textViewDeliveryDate.text = epochToDate(gelenSold?.lastUpdate!!.toLong())
                textViewTotalPrice.text = gelenSold?.totalPrice + "TL"


                val productsList: ArrayList<SubProductResponse> = arrayListOf()

                soldViewModel.soldDetailLiveData.value?.data?.filter { it.soldId == gelenSold?.soldId }?.forEach {filterSoldDetail ->
                    productViewModel.getSubproductWithSubproductId(filterSoldDetail.subProductId!!)?.let {
                        productsList.add(it)
                    }
                }
                soldProductsAdapter.updateProductList(productsList,soldViewModel.soldDetailLiveData.value?.data!!)

            }



        } else {
            println("null geldi")
        }

    }

    fun epochToDate(epoch: Long): String  {
        val sdf = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = epoch * 1000 // Saniyeleri milisaniyeye dönüştürmek için 1000 ile çarpıyoruz
        return sdf.format(calendar.time)
    }
}