package com.example.foldAR.kotlin.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foldAR.kotlin.helloar.R
import com.example.foldAR.kotlin.helloar.databinding.ItemDialogDetailBinding
import com.example.foldAR.kotlin.mainActivity.MainActivityViewModel
import com.example.foldAR.kotlin.renderer.WrappedAnchor
import com.google.ar.core.Anchor

class ObjectAdapter(
    private val onItemClicked: ClickListenerButton,
    private val viewModel: MainActivityViewModel,
) :
    ListAdapter<WrappedAnchor, ObjectAdapter.ItemViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemDialogDetailBinding.inflate(
                LayoutInflater.from(
                    parent.context,
                )
            ), viewModel
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.binding.selectButton.setOnClickListener {
            onItemClicked.onItemClicked(position)
        }
        holder.bind(current.anchor, position)
    }


    class ItemViewHolder(
        val binding: ItemDialogDetailBinding,
        private val viewModel: MainActivityViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(anchor: Anchor, position: Int) {
            binding.apply {
                index.text = position.toString()

                if (viewModel.currentPosition.value == position)
                    innerLayout.setBackgroundResource(R.drawable.layout_rounded_corners)
                else
                    innerLayout.setBackgroundColor(Color.parseColor("#232424"))

            }

        }
    }

    interface ClickListenerButton {
        fun onItemClicked(position: Int)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<WrappedAnchor>() {

            override fun areItemsTheSame(
                oldItem: WrappedAnchor,
                newItem: WrappedAnchor,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: WrappedAnchor,
                newItem: WrappedAnchor,
            ): Boolean {
                return oldItem.anchor == newItem.anchor
            }
        }
    }
}