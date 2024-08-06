package com.example.foldAR.kotlin.dialog

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.foldAR.kotlin.adapter.ObjectAdapter
import com.example.foldAR.kotlin.helloar.R
import com.example.foldAR.kotlin.helloar.databinding.DialogObjectOptionsBinding
import com.example.foldAR.kotlin.helperClasses.ColorGradingDelete
import com.example.foldAR.kotlin.helperClasses.SwipeBackgroundHelper
import com.example.foldAR.kotlin.mainActivity.MainActivityViewModel

class DialogObjectOptions : DialogFragment() {

    companion object {
        fun newInstance() = DialogObjectOptions().apply {
            arguments = Bundle().apply {}
        }
    }

    private var _binding: DialogObjectOptionsBinding? = null
    private val binding get() = _binding!!

    private val viewModelMainActivity: MainActivityViewModel by activityViewModels()
    private lateinit var objectAdapter: ObjectAdapter

    private var displayWidth: Int? = null

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.854).toInt()

        dialog!!.window?.apply {
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawableResource(android.R.color.transparent)

            val params = attributes
            params.gravity = Gravity.TOP
            params.y = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 125f, resources.displayMetrics).toInt()
            attributes = params

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogObjectOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        bindingRecyclerViewObjects()
        setObjectObserver()
        setSliderObserver()
        swipeToDelete()
        deleteAllObjects()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteAllObjects() {
        binding.deleteAll.setOnClickListener{
            val size = viewModelMainActivity.renderer.wrappedAnchors.size
            viewModelMainActivity.renderer.wrappedAnchors.clear()
            objectAdapter.notifyItemRangeRemoved(0, size)
        }
    }

    override fun onResume() {
        super.onResume()
        displayWidth = dialog?.window?.attributes?.width
    }

    private fun setAdapter() {
        objectAdapter = ObjectAdapter(object : ObjectAdapter.ClickListenerButton {
            override fun onItemClicked(position: Int) {
                objectAdapter.notifyItemChanged(viewModelMainActivity.currentPosition.value!!)
                viewModelMainActivity.setPosition(position)
                objectAdapter.notifyItemChanged(position)
            }
        }, viewModelMainActivity)
    }

    private fun bindingRecyclerViewObjects() {
        binding.objectList.apply {
            adapter = objectAdapter
            layoutManager = GridLayoutManager(this.context, 1)
            setHasFixedSize(true)
        }
    }

    private fun setObjectObserver() {
        viewModelMainActivity.renderer.wrappedAnchorsLiveData.observe(this.viewLifecycleOwner) { wrappedAnchors ->
            objectAdapter.submitList(wrappedAnchors)
        }
    }

    private fun setSliderObserver() {
        binding.slider.value = viewModelMainActivity.scale.value!!
        binding.slider.addOnChangeListener { _, value, _ ->
            viewModelMainActivity.setScale(value)
        }
    }

    private fun swipeToDelete() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                viewModelMainActivity.deleteObject(position)
                objectAdapter.notifyItemRemoved(position)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean,
            ) {
                val colorGradingDelete = ColorGradingDelete(displayWidth!!)

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val viewItem = viewHolder.itemView
                    SwipeBackgroundHelper.paintDrawCommandToStart(
                        c,
                        viewItem,
                        R.drawable.trash,
                        dX,
                        colorGradingDelete
                    )
                }
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }).attachToRecyclerView(binding.objectList)
    }
}