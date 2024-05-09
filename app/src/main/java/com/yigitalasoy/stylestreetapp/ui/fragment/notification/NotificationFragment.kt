package com.yigitalasoy.stylestreetapp.ui.fragment.notification

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.FragmentNotificationBinding
import com.yigitalasoy.stylestreetapp.model.NotificationResponse
import com.yigitalasoy.stylestreetapp.util.ItemClickListener
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.viewmodel.NotificationViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
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


        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                notificationViewModel.changeNotificationSeen(notificationViewModel.notificationLiveData.value?.data?.get(viewHolder.bindingAdapterPosition)?.notificationId!!)

                //wishListAdapter.deleteItem()
            }


            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                val itSeen = notificationViewModel.notificationLiveData.value?.data?.get(viewHolder.bindingAdapterPosition)?.ItSeen

                if(itSeen.equals("0")){
                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftLabel("Seen")
                        .setSwipeLeftLabelColor(R.color.black)
                        .addBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.teal_700
                            )
                        )
                        .addActionIcon(R.drawable.eye_show)
                        .create()
                        .decorate()

                } else if (itSeen.equals("1")) {
                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftLabel("Unseen")
                        .setSwipeLeftLabelColor(R.color.black)
                        .addBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.teal_700
                            )
                        )
                        .addActionIcon(R.drawable.eye_hide)
                        .create()
                        .decorate()

                }

            }
        })

        itemTouchHelper.attachToRecyclerView(notificationBinding.recyclerViewNotification)



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