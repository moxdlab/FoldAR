package com.example.foldAR.kotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foldAR.kotlin.helloar.databinding.ItemDialogDetailBinding
import com.google.ar.core.Anchor

class ObjectAdapter :
    ListAdapter<Anchor, ObjectAdapter.ItemViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemDialogDetailBinding.inflate(
                LayoutInflater.from(
                    parent.context,
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.binding.selectButton.setOnClickListener {}
        holder.bind(current)
    }


    class ItemViewHolder(
        val binding: ItemDialogDetailBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(anchor: Anchor) {
            binding.apply {
                position.text = anchor.pose.qx().toString()
            }

        }
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