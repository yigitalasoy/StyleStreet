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
import java.util.Locale

class WishListAdapter(val wishList: ArrayList<WishListResponse>,val productList: ArrayList<ProductResponse>): RecyclerView.Adapter<ViewHolder>() {
    lateinit var binding: WishListRowBinding

    val filteredWishList: ArrayList<WishListResponse> = arrayListOf()
    val filteredProductList: ArrayList<ProductResponse> = arrayListOf()

    class WishListViewHolder(itemView: View):ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.wish_list_row,parent,false)
        return WishListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredWishList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binding = WishListRowBinding.bind(holder.itemView)


        binding.apply {
            imageViewProductImage.downloadImage(filteredProductList.find { it.productId == filteredWishList[position].product_Id }!!.allProducts?.get(0)!!.subProductImageURL?.get(0)!!)
            textViewProductName.text = filteredProductList.find { it.productId == filteredWishList[position].product_Id }!!.allProducts?.get(0)!!.subProductName
            textViewProductPrice.text = filteredProductList.find { it.productId == filteredWishList[position].product_Id }!!.allProducts?.get(0)!!.subProductPrice
        }




    }

    fun filterWishList(word: String){

        filteredWishList.clear()
        filteredProductList.clear()

        if(word.isNotBlank()){
            filteredProductList.addAll(productList.filter { it.productName!!.toLowerCase(Locale.getDefault()).contains(word.toLowerCase(
                Locale.getDefault())) })
            println("filtre edildi: $filteredProductList")
        } else {
            filteredProductList.addAll(productList)
        }

        if(filteredProductList.size != 0){
            filteredWishList.addAll(wishList.filter { it.product_Id in filteredProductList.toList().map { it.productId } })

            if(filteredWishList.size == 0){
                filteredWishList.addAll(wishList)
            }
        }


        /*filteredWishList.forEach {
            println("filtre çalıştı filtered wish list: ${ it.Product_Id }")
        }

        filteredProductList.forEach {
            println("filtre çalıştı filtered product list: ${it.productName}")
        }*/

        notifyDataSetChanged()

        //wishList.any { it.Product_Id in filteredProductList.toList().map { it.productId } }

    }

    fun updateWishList(newWishList: List<WishListResponse>){
        this.wishList.clear()
        this.filteredWishList.clear()

        this.filteredProductList.addAll(this.productList)

        if(newWishList.size != 0){
            this.wishList.addAll(newWishList)
            this.filteredWishList.addAll(newWishList)
        }

        println("wish list adapter update etti: wish list: ${wishList}")

        notifyDataSetChanged()
    }

}