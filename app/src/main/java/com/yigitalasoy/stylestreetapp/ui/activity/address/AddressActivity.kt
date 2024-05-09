package com.yigitalasoy.stylestreetapp.ui.activity.address

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.ActivityAddressBinding
import com.yigitalasoy.stylestreetapp.model.AddressResponse
import com.yigitalasoy.stylestreetapp.ui.activity.basket.BasketActivity
import com.yigitalasoy.stylestreetapp.util.ItemClickListener
import com.yigitalasoy.stylestreetapp.util.ObjectUtil
import com.yigitalasoy.stylestreetapp.util.Resource
import com.yigitalasoy.stylestreetapp.util.Status
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.util.toast
import com.yigitalasoy.stylestreetapp.viewmodel.AddressViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import javax.inject.Inject

@AndroidEntryPoint
class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding

    @Inject lateinit var addressViewModel: AddressViewModel
    @Inject lateinit var userViewModel: UserViewModel

    lateinit var addressAdapter: AddressAdapter

    var selectedAddressPosition = 0

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




        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.LEFT ->{//remove yapılcak
                        val position = viewHolder.bindingAdapterPosition
                        //addressViewModel.addressLiveData.value?.data?.removeAt(viewHolder.bindingAdapterPosition)
                        //addressViewModel.addressLiveData.value = Resource.success(addressViewModel.addressLiveData.value?.data)


                        val builder = AlertDialog.Builder(this@AddressActivity)
                        builder.setTitle("Delete")
                        builder.setMessage("Are you sure for delete this address?")
                        builder.setPositiveButton("Delete") { _: DialogInterface, _: Int ->
                            //addressViewModel.addressLiveData.value?.data?.removeAt(position)
                            //addressAdapter.notifyItemRemoved(position)
                            selectedAddressPosition = viewHolder.bindingAdapterPosition
                            addressViewModel.removeAddress(addressViewModel.addressLiveData.value?.data?.get(selectedAddressPosition)?.addressId.toString())
                        }
                        builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                            addressAdapter.notifyItemChanged(position)
                        }
                        builder.setOnDismissListener {
                            addressAdapter.notifyDataSetChanged()
                        }
                        builder.show()


                        println("left çalıştı")
                    }
                    ItemTouchHelper.RIGHT ->{//edit yapılcak
                        println("right çalıştı")
                        val addAddressActivity = Intent(this@AddressActivity,AddAddressActivity::class.java)
                        addAddressActivity.putExtra("selectedAddress",ObjectUtil().addressToJsonString(addressViewModel.addressLiveData.value?.data?.get(viewHolder.bindingAdapterPosition)))
                        this@AddressActivity.startActivity(addAddressActivity)
                        addressAdapter.notifyItemChanged(viewHolder.bindingAdapterPosition)
                    }
                }


            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )


                var a = RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)


                    if (dX < 0) { // Swiping to the right
                        a.addSwipeRightLabel("Delete address")
                            .setSwipeRightLabelColor(R.color.black)
                            .addBackgroundColor(
                                ContextCompat.getColor(
                                    this@AddressActivity,
                                    R.color.remove_red
                                )
                            )
                            .addActionIcon(R.drawable.dustbin)

                    } else if (dX > 0) { // Swiping to the left
                        a.addSwipeLeftLabel("Edit address")
                            .setSwipeLeftLabelColor(R.color.black)
                            .addBackgroundColor(
                                ContextCompat.getColor(
                                    this@AddressActivity,
                                    R.color.teal_700
                                )
                            )
                            .addActionIcon(R.drawable.edit_vector)
                    } else {
                        println("else")
                    }

                    a.create()
                    .decorate()

            }
        })

        itemTouchHelper.attachToRecyclerView(binding.recyclerViewAddress)

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
                    if(it.data?.size == 0){
                        println("size == 0")
                        binding.textViewNoAddress.show()
                        binding.recyclerViewAddress.hide()
                    }
                    if(it.data != null){
                        binding.recyclerViewAddress.show()
                        binding.textViewNoAddress.hide()
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

        addressViewModel.removeState.observe(this){

            when(it.status){
                Status.SUCCESS -> {
                    addressViewModel.addressLiveData.value?.data?.removeAt(selectedAddressPosition)
                    addressViewModel.addressLiveData.value = Resource.success(addressViewModel.addressLiveData.value?.data)
                    //Toast.makeText(this@AddressActivity, "Öğe silindi", Toast.LENGTH_SHORT).show()
                }
                Status.ERROR -> {
                    this.toast("ERROR: ${it.message.toString()}")
                }
                Status.LOADING -> {
                    println("address remove loading")
                }
            }

        }


    }

    override fun onDestroy() {
        super.onDestroy()
        println("address destroyed")
    }
}