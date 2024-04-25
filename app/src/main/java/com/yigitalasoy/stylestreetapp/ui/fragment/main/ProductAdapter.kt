package com.yigitalasoy.stylestreetapp.ui.fragment.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.ProductRowBinding
import com.yigitalasoy.stylestreetapp.model.ProductResponse
import com.yigitalasoy.stylestreetapp.util.ItemClickListener
import com.yigitalasoy.stylestreetapp.util.downloadImage


class ProductAdapter(
    var productList: ArrayList<ProductResponse>,
    var onItemClickListener: ItemClickListener): RecyclerView.Adapter<ViewHolder>() {

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
        binding.textViewProductName.text = productList[position].productName

        if(productList[position].allProducts?.size != 0){
            productList[position].allProducts.let {
                binding.textViewPrice.text = productList[position].allProducts?.get(0)!!.subProductPrice
                productList[position].allProducts?.get(0)!!.subProductImageURL?.get(0)?.let{
                    binding.imageViewProductImage.downloadImage(it)
                }


            }
        }

        binding.cardViewProductDetail.setOnClickListener {
            onItemClickListener.onItemClick(productList[position])
        }


    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun updateProductList(newProductList: List<ProductResponse>){
        this.productList.clear()
        this.productList.addAll(newProductList)
        notifyDataSetChanged()
    }

}