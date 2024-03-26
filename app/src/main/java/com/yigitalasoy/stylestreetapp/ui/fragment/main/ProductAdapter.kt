package com.yigitalasoy.stylestreetapp.ui.fragment.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.ProductRowBinding
import com.yigitalasoy.stylestreetapp.model.ProductResponse

class ProductAdapter(var productList: ArrayList<ProductResponse>): RecyclerView.Adapter<ViewHolder>() {

    private lateinit var binding: ProductRowBinding

    class ProductViewHolder(itemView: View) : ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.product_row,parent,false)
        return ProductViewHolder(view.rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binding = ProductRowBinding.bind(holder.itemView)

        binding.textViewProductName.text = "${binding.textViewProductName.text} ${productList[position].id}"

    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun updateProductList(productList: List<ProductResponse>){
        this.productList.addAll(productList)
        notifyDataSetChanged()
    }

}