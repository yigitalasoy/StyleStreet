package com.yigitalasoy.stylestreetapp.ui.fragment.basket

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.BasketProductRowBinding
import com.yigitalasoy.stylestreetapp.model.SubProductResponse
import com.yigitalasoy.stylestreetapp.util.downloadImage

class BasketProductAdapter(val productList: ArrayList<SubProductResponse>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var basketProductRowBinding: BasketProductRowBinding

    class BasketProductViewHolder(itemView: View): ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.basket_product_row,parent,false)
        return BasketProductViewHolder(view.rootView)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        basketProductRowBinding = BasketProductRowBinding.bind(holder.itemView)

        basketProductRowBinding.apply {

            val content = SpannableString("Sil")
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            textViewDeleteText.text = content

            imageViewBasketProductImage.downloadImage(productList[position].subProductImageURL!![0])

            textViewBasketProductName.text = productList[position].subProductName
            textViewBasketProductSize.text = "Beden: ${productList[position].subProductSizeId.productSizeName}"
            textViewBasketProductColor.text = productList[position].subProductColorId.colorName
            textViewBasketProductTotal.text = "${productList[position].subProductPrice} TL"

            buttonIncreaseProductQuantity.setOnClickListener {
                textViewBasketProductQuantity.text = (textViewBasketProductQuantity.text.toString().toInt() + 1).toString()
            }
            buttonDecreaseProductQuantity.setOnClickListener {
                if(textViewBasketProductQuantity.text.toString().toInt() - 1 > 0){
                    textViewBasketProductQuantity.text = (textViewBasketProductQuantity.text.toString().toInt() - 1).toString()
                }
            }

        }


    }

    fun updateBasketSubProductList(newProductList: ArrayList<SubProductResponse>){
        this.productList.clear()
        this.productList.addAll(newProductList)
        notifyDataSetChanged()
    }

}