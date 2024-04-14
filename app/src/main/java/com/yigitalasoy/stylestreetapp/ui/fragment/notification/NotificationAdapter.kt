package com.yigitalasoy.stylestreetapp.ui.fragment.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.NotificationRowBinding
import com.yigitalasoy.stylestreetapp.model.NotificationResponse
import com.yigitalasoy.stylestreetapp.util.ItemClickListener
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show

class NotificationAdapter(var notificationList: ArrayList<NotificationResponse>, var onItemClickListener: ItemClickListener): RecyclerView.Adapter<ViewHolder>() {

    lateinit var notificationRowBinding: NotificationRowBinding

    class NotificationViewHolder(itemView: View):ViewHolder(itemView){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.notification_row,parent,false)
        return NotificationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        notificationRowBinding = NotificationRowBinding.bind(holder.itemView)

        notificationRowBinding?.apply {

            textViewNotificationMessage.text = notificationList[position].notificationMessage
            if(notificationList[position].ItSeen == "0"){
                notificationRowBinding.viewItSeen.show()
            } else {
                notificationRowBinding.viewItSeen.hide()
            }

            constraintNotification.setOnClickListener {

                onItemClickListener.onItemClick(notificationList[position])

            }

        }


    }

    fun updateNotificationList(newNotificationList: List<NotificationResponse>){
        this.notificationList.clear()
        this.notificationList.addAll(newNotificationList)
        notifyDataSetChanged()
    }

}