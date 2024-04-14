package com.yigitalasoy.stylestreetapp.ui.fragment.wishlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.WishListRowBinding
import com.yigitalasoy.stylestreetapp.model.ProductResponse
import com.yigitalasoy.stylestreetapp.model.WishListResponse
import com.yigitalasoy.stylestreetapp.util.downloadImage

class WishListAdapter(val wishList: ArrayList<WishListResponse>,val productList: ArrayList<ProductResponse>): RecyclerView.Adapter<ViewHolder>() {
    lateinit var binding: WishListRowBinding

    class WishListViewHolder(itemView: View):ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.wish_list_row,parent,false)
        return WishListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return wishList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binding = WishListRowBinding.bind(holder.itemView)


        binding.apply {
            imageViewProductImage.downloadImage(productList.find { it.productId == wishList[position].Product_Id }!!.allProducts[0].subProductImageURL?.get(0)!!)
            textViewProductName.text = productList.find { it.productId == wishList[position].Product_Id }!!.allProducts[0].subProductName
            textViewProductPrice.text = productList.find { it.productId == wishList[position].Product_Id }!!.allProducts[0].subProductPrice
        }


    }

    fun updateWishList(newWishList: List<WishListResponse>){
        this.wishList.clear()
        this.wishList.addAll(newWishList)
        notifyDataSetChanged()
    }

}