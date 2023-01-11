package com.viol4tsf.noteappm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.viol4tsf.noteappm.databinding.GroupItemBinding
import com.viol4tsf.noteappm.model.Group

class GroupAdapter: RecyclerView.Adapter<GroupAdapter.GroupViewHolder>(){

    class GroupViewHolder(val itemBinding: GroupItemBinding): RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Group>(){
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.groupName == newItem.groupName
        }

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder(
            GroupItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val currentGroup = differ.currentList[position]

        holder.itemBinding.groupNameTextView.text = currentGroup.groupName
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}