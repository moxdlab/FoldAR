package com.example.foldAR.kotlin.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foldAR.kotlin.adapter.ObjectAdapter
import com.example.foldAR.kotlin.helloar.databinding.DialogObjectOptionsBinding
import com.example.foldAR.kotlin.mainActivity.MainActivityViewModel
import com.google.ar.core.Anchor

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
        viewModel.renderer.wrappedAnchorsLiveData.observe(this.viewLifecycleOwner) { it ->
            val list = mutableListOf<Anchor>()
            it.forEach {
                list.add(it.anchor)
            }
            it.let {
                objectAdapter.submitList(list)
            }
        }
    }
}