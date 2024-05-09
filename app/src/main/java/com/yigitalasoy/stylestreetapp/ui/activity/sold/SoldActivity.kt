package com.yigitalasoy.stylestreetapp.ui.activity.sold

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yigitalasoy.stylestreetapp.databinding.ActivitySoldBinding
import com.yigitalasoy.stylestreetapp.model.SoldResponse
import com.yigitalasoy.stylestreetapp.ui.activity.solddetail.SoldDetailActivity
import com.yigitalasoy.stylestreetapp.util.ItemClickListener
import com.yigitalasoy.stylestreetapp.util.ObjectUtil
import com.yigitalasoy.stylestreetapp.util.Status
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.viewmodel.SoldViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SoldActivity : AppCompatActivity() {

    @Inject lateinit var soldViewModel: SoldViewModel
    @Inject lateinit var userViewModel: UserViewModel

    lateinit var soldAdapter: SoldAdapter
    lateinit var binding: ActivitySoldBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoldBinding.inflate(layoutInflater)
        setContentView(binding.root)
        soldViewModel.getUserSold(userViewModel.userLiveData.value?.data?.id!!)
        soldViewModel.getUserSoldDetail(userViewModel.userLiveData.value?.data?.id!!)

        soldAdapter = SoldAdapter(arrayListOf(),arrayListOf(),object : ItemClickListener{
            override fun onItemClick(Item: Any) {
                val soldDetailIntent = Intent(this@SoldActivity, SoldDetailActivity::class.java)
                soldDetailIntent.putExtra("selectedSold",ObjectUtil().soldToJsonString((Item as SoldResponse)))
                this@SoldActivity.startActivity(soldDetailIntent)
            }
        })

        binding.apply {
            recyclerViewSold.apply {
                adapter = soldAdapter
                layoutManager = LinearLayoutManager(this@SoldActivity,RecyclerView.VERTICAL,false)
            }

            fabBack.setOnClickListener {
                super.onBackPressed()
                this@SoldActivity.finish()
            }

        }

        observe()

    }

    fun updateUI(){
        binding.apply {
            recyclerViewSold.show()
            progressBarLoading.hide()
            binding.textViewError.hide()


            if(soldViewModel.soldLiveData.value?.data!!.size != 0){
                binding.constraintLayoutEmptyList.hide()
                binding.recyclerViewSold.show()

                soldAdapter.updateSoldList(
                    soldViewModel.soldLiveData.value?.data!!,
                    soldViewModel.soldDetailLiveData.value?.data!!,
                )
            } else {
                binding.constraintLayoutEmptyList.show()
                binding.recyclerViewSold.hide()
            }


        }
    }

    private fun observe(){
        soldViewModel.soldLiveData.observe(this){

            when(it.status){
                Status.SUCCESS -> {
                    if(soldViewModel.soldDetailLiveData.value?.status == Status.SUCCESS && it.data != null){
                        updateUI()
                    }
                }
                Status.ERROR -> {
                    println("soldLiveData error: ${it.message}")
                    binding.progressBarLoading.hide()
                    binding.textViewError.show()
                    binding.textViewError.text = it.message
                }
                Status.LOADING -> {
                    binding.progressBarLoading.show()
                }
            }
        }

        soldViewModel.soldDetailLiveData.observe(this){

            when(it.status){
                Status.SUCCESS -> {
                    if(soldViewModel.soldLiveData.value?.status == Status.SUCCESS && it.data != null){
                        updateUI()
                    }
                }
                Status.ERROR -> {
                    println("soldDetailLiveData error: ${it.message}")
                    binding.textViewError.show()
                    binding.progressBarLoading.hide()
                    binding.textViewError.text = it.message
                }
                Status.LOADING -> {
                    binding.progressBarLoading.show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("sold activity destroyed")
    }

}