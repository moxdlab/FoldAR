package com.example.foldAR.kotlin.dialog

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
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

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var objectAdapter: ObjectAdapter

    private var displayWidth: Int? = null

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.854).toInt()
        dialog!!.window?.apply {
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        _binding = DialogObjectOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        bindingRecyclerViewObjects()
        setObjectObserver()

        swipeToDelete()
    }

    override fun onResume() {
        super.onResume()
        displayWidth = dialog?.window?.attributes?.width
    }

    private fun setAdapter() {
        objectAdapter = ObjectAdapter(object : ObjectAdapter.ClickListenerButton {
            override fun onItemClicked(position: Int) {
                objectAdapter.notifyItemChanged(viewModel.currentPosition)
                viewModel.currentPosition = position
                objectAdapter.notifyItemChanged(position)
            }
        }, viewModel)
    }

    private fun bindingRecyclerViewObjects() {
        binding.objectList.apply {
            adapter = objectAdapter
            layoutManager = GridLayoutManager(this.context, 1)
            setHasFixedSize(true)
        }
    }

    private fun setObjectObserver() {
        viewModel.renderer.wrappedAnchorsLiveData.observe(this.viewLifecycleOwner) { wrappedAnchors ->
            val anchors = wrappedAnchors.map { it.anchor }
            objectAdapter.submitList(anchors)
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
                val deletedObjectIndex = viewHolder.adapterPosition
                viewModel.deleteObject(deletedObjectIndex)
                objectAdapter.notifyItemChanged(viewModel.currentPosition)
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