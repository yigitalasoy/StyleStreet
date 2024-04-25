package com.yigitalasoy.stylestreetapp.ui.activity.solddetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.SoldDetailRowBinding
import com.yigitalasoy.stylestreetapp.model.SoldDetailResponse
import com.yigitalasoy.stylestreetapp.model.SubProductResponse
import com.yigitalasoy.stylestreetapp.util.downloadImage

class SoldProductsAdapter(val productList: ArrayList<SubProductResponse>, var soldDetailData: ArrayList<SoldDetailResponse>): RecyclerView.Adapter<ViewHolder>() {
    lateinit var binding: SoldDetailRowBinding

    class SoldProductsViewHolder(itemView: View):ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.sold_detail_row,parent,false)
        return SoldProductsViewHolder(view.rootView)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binding = SoldDetailRowBinding.bind(holder.itemView)

        binding.apply {
            textViewProductName.text = productList[position].subProductName
            textViewProductPrice.text = productList[position].subProductPrice
            imageViewProductImage.downloadImage(productList[position].subProductImageURL?.get(0))
            textViewProductQuantity.text = soldDetailData.find { it.subProductId == productList[position].subProductId }?.quantity.toString() + " pieces"
        }
    }

    fun updateProductList(
        newProductList: ArrayList<SubProductResponse>,
        newSoldDetailData: ArrayList<SoldDetailResponse>
    ){
        this.productList.clear()
        this.productList.addAll(newProductList)

        this.soldDetailData.clear()
        this.soldDetailData.addAll(newSoldDetailData)

        notifyDataSetChanged()
    }

}