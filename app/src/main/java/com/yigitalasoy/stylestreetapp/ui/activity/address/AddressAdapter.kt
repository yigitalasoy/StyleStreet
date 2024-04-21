package com.yigitalasoy.stylestreetapp.ui.activity.address

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.AddressRowBinding
import com.yigitalasoy.stylestreetapp.model.AddressResponse
import com.yigitalasoy.stylestreetapp.util.ItemClickListener

class AddressAdapter(val addressList: ArrayList<AddressResponse>,val onItemClickListener: ItemClickListener): RecyclerView.Adapter<ViewHolder>() {

    lateinit var binding: AddressRowBinding

    class AddressViewHolder(itemView: View):ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.address_row,parent,false)
        return AddressViewHolder(view.rootView)
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binding = AddressRowBinding.bind(holder.itemView)

        binding.apply {
            textViewAddressName.text = addressList[position].addressHeader
            textViewAddressDetail.text = "${addressList[position].addressDetail} ${addressList[position].addressProvince}/${addressList[position].addressDistrict}"
            textViewTelNumber.text = addressList[position].telNumber
            textViewAddressNameSurname.text = addressList[position].addressName + " " +addressList[position].addressSurname

            constraintAddress.setOnClickListener {
                onItemClickListener.onItemClick(addressList[position])
            }

        }
    }

    fun updateAddressList(newAddressList: List<AddressResponse>){
        this.addressList.clear()
        this.addressList.addAll(newAddressList)
        notifyDataSetChanged()
    }

}