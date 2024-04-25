package com.yigitalasoy.stylestreetapp.ui.fragment.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yigitalasoy.stylestreetapp.databinding.FragmentNotificationBinding
import com.yigitalasoy.stylestreetapp.model.NotificationResponse
import com.yigitalasoy.stylestreetapp.util.ItemClickListener
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.viewmodel.NotificationViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private var _notificationBinding: FragmentNotificationBinding? = null
    private val notificationBinding get() = _notificationBinding!!

    @Inject lateinit var notificationViewModel: NotificationViewModel
    @Inject lateinit var userViewModel: UserViewModel

    lateinit var notificationAdapter: NotificationAdapter
    var notificationList = ArrayList<NotificationResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _notificationBinding = FragmentNotificationBinding.inflate(inflater,container,false)
        val view = notificationBinding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationViewModel.getNotificationData(userViewModel.userLiveData.value?.data?.id!!)

        notificationAdapter = NotificationAdapter(arrayListOf(),object : ItemClickListener{

            override fun onItemClick(notificationItem: Any) {
                notificationItem as NotificationResponse

                notificationViewModel.changeNotificationSeen(notificationItem.notificationId!!)

            }

        })

        notificationBinding.recyclerViewNotification.apply {
            adapter = notificationAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
        }

        observe()

    }


    fun observe(){

        notificationViewModel.notificationLiveData.observe(viewLifecycleOwner){
            println("notificationViewModel observe çalıştı: ${it.data}")
            if(it.data != null){
                if(it.data.size != 0){
                    notificationAdapter.updateNotificationList(it.data)
                    notificationBinding.textViewNoNotification.hide()
                    notificationBinding.recyclerViewNotification.show()
                } else {
                    notificationBinding.textViewNoNotification.show()
                    notificationBinding.recyclerViewNotification.hide()
                }
                println("observe yeni gelen notification list: ${it.data}")
            }

        }

    }

}