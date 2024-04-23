package com.yigitalasoy.stylestreetapp.ui.activity.solddetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.WishListRowBinding
import com.yigitalasoy.stylestreetapp.model.SubProductResponse
import com.yigitalasoy.stylestreetapp.util.downloadImage

class SoldProductsAdapter(val productList: ArrayList<SubProductResponse>): RecyclerView.Adapter<ViewHolder>() {
    lateinit var binding: WishListRowBinding

    class SoldProductsViewHolder(itemView: View):ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.wish_list_row,parent,false)
        return SoldProductsViewHolder(view.rootView)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binding = WishListRowBinding.bind(holder.itemView)

        binding.apply {
            textViewProductName.text = productList[position].subProductName
            textViewProductPrice.text = productList[position].subProductPrice
            imageViewProductImage.downloadImage(productList[position].subProductImageURL?.get(0))
        }
    }

    fun updateProductList(newProductList: ArrayList<SubProductResponse>){
        this.productList.clear()
        this.productList.addAll(newProductList)
        notifyDataSetChanged()
    }

}