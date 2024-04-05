package com.yigitalasoy.stylestreetapp.ui.activity.productdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.ImageRowBinding
import com.yigitalasoy.stylestreetapp.util.downloadImage

class ProductImageAdapter(var subProductImages: ArrayList<String>?): RecyclerView.Adapter<ViewHolder>() {

    private lateinit var binding: ImageRowBinding

    class ImageViewHolder(itemView: View): ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.image_row,parent,false)
        return ImageViewHolder(view.rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binding = ImageRowBinding.bind(holder.itemView)

        // subProductImages.forEach {
        //     binding.imageViewSubProductImage.downloadImage(subProductImages[position])
        // }
        if(subProductImages!=null){
            binding.apply {
                println("image adapter gelen: ${subProductImages!![position]}")
                imageViewSubProductImage.downloadImage(subProductImages!![position])
            }
        }

    }

    override fun getItemCount(): Int {
        return subProductImages?.size ?: 0
    }

    fun updateCategoryList(newImageList: List<String>){
        this.subProductImages?.clear()
        this.subProductImages?.addAll(newImageList)
        notifyDataSetChanged()
    }
}