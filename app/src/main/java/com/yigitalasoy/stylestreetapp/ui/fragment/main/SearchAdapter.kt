package com.yigitalasoy.stylestreetapp.ui.fragment.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.SearchRowBinding
import com.yigitalasoy.stylestreetapp.model.ProductResponse
import com.yigitalasoy.stylestreetapp.util.ItemClickListener
import com.yigitalasoy.stylestreetapp.util.downloadImage

class SearchAdapter (
    context: Context,
    productList: List<ProductResponse>,
    var onItemClickListener: ItemClickListener) :
    ArrayAdapter<ProductResponse>(context, 0, productList) {

    private val productFull: MutableList<ProductResponse> = ArrayList(productList)

    override fun getFilter(): Filter {
        return productFilter
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: SearchRowBinding
        val itemView: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.search_row, parent, false)
        binding = SearchRowBinding.bind(itemView)

        val productResponse = getItem(position)
        binding.textViewSearchProductName.text = productResponse?.productName
        binding.textViewSearchProductPrice.text = productResponse?.allProducts!![0].subProductPrice + " TL"
        binding.imageViewSearchSubProductImage.downloadImage(productResponse?.allProducts!![0].subProductImageURL!![0])
        binding.searchCardView.setOnClickListener {
            onItemClickListener.onItemClick(productResponse)
        }
        return itemView
    }

    private val productFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            val suggest: MutableList<ProductResponse> = ArrayList()

            if (constraint.isNullOrEmpty()) {
                suggest.addAll(productFull)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim()

                for (item in productFull) {
                    if (item.productName.toLowerCase().contains(filterPattern)) {
                        suggest.add(item)
                    }
                }
            }

            results.values = suggest
            results.count = suggest.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            clear()
            if (results.count > 0) {
                addAll(results.values as List<ProductResponse>)
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as ProductResponse).productName
        }
    }


}