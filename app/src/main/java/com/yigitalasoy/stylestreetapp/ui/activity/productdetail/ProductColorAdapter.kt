package com.yigitalasoy.stylestreetapp.ui.activity.productdetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.ColorRowBinding
import com.yigitalasoy.stylestreetapp.model.ProductColorResponse

class ProductColorAdapter(context: Context, private val colorItems: List<ProductColorResponse>): ArrayAdapter<ProductColorResponse>(context,0,colorItems) {
    private lateinit var colorBinding: ColorRowBinding
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position,convertView,parent)

    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position,convertView,parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View{

        val view = LayoutInflater.from(context).inflate(R.layout.color_row, parent, false)
        colorBinding = ColorRowBinding.bind(view)

        //colorBinding.imageViewColorImage.setBackgroundColor(ContextCompat.getColor(context,R.color.blue))
        colorBinding.imageViewColorImage.setBackgroundColor(android.graphics.Color.parseColor(colorItems[position].colorCode))
        colorBinding.colorName.text = colorItems[position].colorName

        return view
    }
}
