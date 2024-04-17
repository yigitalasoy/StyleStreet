package com.yigitalasoy.stylestreetapp.ui.activity.basket

import android.app.Activity
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.BasketProductRowBinding
import com.yigitalasoy.stylestreetapp.model.BasketResponse
import com.yigitalasoy.stylestreetapp.model.SubProductResponse
import com.yigitalasoy.stylestreetapp.util.ItemClickListener
import com.yigitalasoy.stylestreetapp.util.downloadImage
import com.yigitalasoy.stylestreetapp.util.toast

class BasketProductAdapter(val activity: Activity,
    var productList: ArrayList<SubProductResponse>,
    var basketData: BasketResponse?,
    var onItemClickListener: ItemClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            textViewBasketProductQuantity.text = basketData?.basketProducts?.find { it.subProductId == productList[position].subProductId}?.quantity.toString()
            textViewBasketProductSize.text = "Beden: ${productList[position].subProductSizeId?.productSizeName}"
            textViewBasketProductColor.text = "Renk: ${productList[position].subProductColorId?.colorName}"
            textViewBasketProductTotal.text = "${productList[position].subProductPrice} TL"

            buttonIncreaseProductQuantity.setOnClickListener {
                if(textViewBasketProductQuantity.text.toString().toInt() + 1 < 10){

                    val dataTransferList = mapOf(
                        "subProductId" to productList[position].subProductId,
                        "type" to "increase",
                        "position" to position.toString())
                    onItemClickListener.onItemClick(dataTransferList)

                    //basketViewModel.basketProductIncrease(userViewModel.userLiveData.value?.data?.id.toString(),productList[position].subProductId,true)

                    buttonDecreaseProductQuantity.isClickable = true
                    //textViewBasketProductQuantity.text = (textViewBasketProductQuantity.text.toString().toInt() + 1).toString()
                } else {
                    activity.toast("The number of orders cannot be more than 9")
                    buttonIncreaseProductQuantity.isClickable = false
                }
            }

            buttonDecreaseProductQuantity.setOnClickListener {
                if(textViewBasketProductQuantity.text.toString().toInt() - 1 > 0){

                    val dataTransferList = mapOf(
                        "subProductId" to productList[position].subProductId,
                        "type" to "decrease",
                        "position" to position.toString())
                    onItemClickListener.onItemClick(dataTransferList)


                    buttonIncreaseProductQuantity.isClickable = true
                    //textViewBasketProductQuantity.text = (textViewBasketProductQuantity.text.toString().toInt() - 1).toString()
                } else {

                    activity.toast("The number of orders cannot be less than 1")
                    buttonDecreaseProductQuantity.isClickable = false
                }
            }

            textViewDeleteText.setOnClickListener {
                deleteProduct(position)
            }

            imageViewDelete.setOnClickListener {
                deleteProduct(position)
            }


        }
    }

    private fun deleteProduct(position: Int) {
        val dataTransferList = mapOf(
            "subProductId" to productList[position].subProductId,
            "type" to "remove",
            "position" to position.toString()
        )
        onItemClickListener.onItemClick(dataTransferList)
    }

    fun updateBasketSubProductList(newProductList: List<SubProductResponse>,newBasketData: BasketResponse){
        this.productList.clear()
        this.productList.addAll(newProductList as ArrayList<SubProductResponse>)

        basketData = newBasketData


        notifyDataSetChanged()
    }

}