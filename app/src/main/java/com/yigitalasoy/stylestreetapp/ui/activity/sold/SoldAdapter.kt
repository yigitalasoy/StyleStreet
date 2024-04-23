package com.yigitalasoy.stylestreetapp.ui.activity.sold

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.SoldRowBinding
import com.yigitalasoy.stylestreetapp.model.SoldDetailResponse
import com.yigitalasoy.stylestreetapp.model.SoldResponse
import com.yigitalasoy.stylestreetapp.util.ItemClickListener

class SoldAdapter(val soldList: ArrayList<SoldResponse>,
                  val soldDetailList: ArrayList<SoldDetailResponse>,
                  val onItemClickListener: ItemClickListener
): RecyclerView.Adapter<ViewHolder>() {

    lateinit var binding: SoldRowBinding


    class SoldViewHolder(itemView: View):ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.sold_row,parent,false)
        return SoldViewHolder(view.rootView)
    }

    override fun getItemCount(): Int {
        return soldList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binding = SoldRowBinding.bind(holder.itemView)

        binding.apply {
            val count = soldDetailList.filter { it.soldId == soldList[position].soldId }.size.toString()
            if(count != "0"){
                textViewItemCount.text = count
            }
            textViewOrderId.text = (position + 1).toString()

            constraintSold.setOnClickListener {
                //soldList[position].soldId = textViewOrderId.text.toString()
                onItemClickListener.onItemClick(soldList[position])
            }

        }
    }

    fun updateSoldList(newSoldList: List<SoldResponse>,newSoldDetailList: List<SoldDetailResponse>){
        println("update sold list geldi.")
        this.soldList.clear()
        this.soldDetailList.clear()

        this.soldList.addAll(newSoldList)
        this.soldDetailList.addAll(newSoldDetailList)


        notifyDataSetChanged()
    }

}