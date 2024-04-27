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
import com.google.ar.core.Anchor

class ObjectAdapter(
    private val onItemClicked: ClickListenerButton,
    private val viewModel: MainActivityViewModel,
) :
    ListAdapter<Anchor, ObjectAdapter.ItemViewHolder>(DiffCallback) {


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
        holder.bind(current, position)
    }


    class ItemViewHolder(
        val binding: ItemDialogDetailBinding,
        private val viewModel: MainActivityViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(anchor: Anchor, position: Int) {
            binding.apply {
                index.text = position.toString()

                if (viewModel.currentPosition == position)
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
        private val DiffCallback = object : DiffUtil.ItemCallback<Anchor>() {

            override fun areItemsTheSame(
                oldItem: Anchor,
                newItem: Anchor,
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: Anchor,
                newItem: Anchor,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}