package com.yigitalasoy.stylestreetapp.ui.fragment.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.CategoryRowBinding
import com.yigitalasoy.stylestreetapp.model.CategoryResponse
import com.yigitalasoy.stylestreetapp.util.downloadImage

class CategoryAdapter(var categoryList: ArrayList<CategoryResponse>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var binding: CategoryRowBinding

    class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.category_row,parent,false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        binding = CategoryRowBinding.bind(holder.itemView)

        binding.textViewCategoryName.text = categoryList[position].categoryName
        categoryList[position].categoryImage?.let {
            binding.imageView4.downloadImage(it)
        }


    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    fun updateCategoryList(newCategoryList: List<CategoryResponse>){
        this.categoryList.clear()
        this.categoryList.addAll(newCategoryList)
        notifyDataSetChanged()
    }

}