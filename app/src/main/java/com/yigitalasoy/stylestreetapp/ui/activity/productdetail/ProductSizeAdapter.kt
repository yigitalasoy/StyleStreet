package com.yigitalasoy.stylestreetapp.ui.activity.productdetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.SizeRowBinding
import com.yigitalasoy.stylestreetapp.model.ProductSizeResponse

class ProductSizeAdapter(context: Context, private var sizeItems: List<ProductSizeResponse>): ArrayAdapter<ProductSizeResponse>(context,0,sizeItems) {
    private lateinit var sizeBinding: SizeRowBinding
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = LayoutInflater.from(context).inflate(R.layout.size_row, parent, false)
        sizeBinding = SizeRowBinding.bind(view)


        sizeBinding.sizeName.text = sizeItems[position].productSizeName

        return view
    }

    fun updateList(newSizeItems: List<ProductSizeResponse>){
        this.sizeItems = newSizeItems
        notifyDataSetChanged()
    }
}