package com.viol4tsf.noteappm.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.viol4tsf.noteappm.databinding.GroupItemBinding
import com.viol4tsf.noteappm.model.Group

class GroupAdapter(val listener: (String) -> Unit): RecyclerView.Adapter<GroupAdapter.GroupViewHolder>(){

    var currentPos: Int = -1

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

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val currentGroup = differ.currentList[position]

        holder.itemBinding.groupNameTextView.text = currentGroup.groupName

        holder.itemView.setOnClickListener{ mView ->
            val textGroup: String = currentGroup.groupName
            listener(textGroup)
            currentPos = position
            notifyDataSetChanged()
        }

        if (position == currentPos){
            holder.itemBinding.groupNameTextView.setBackgroundColor(Color.parseColor("#C5ACCC"))
        } else {
            holder.itemBinding.groupNameTextView.setBackgroundColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}