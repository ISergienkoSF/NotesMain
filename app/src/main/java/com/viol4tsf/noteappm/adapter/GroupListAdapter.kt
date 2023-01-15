package com.viol4tsf.noteappm.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.viol4tsf.noteappm.R
import com.viol4tsf.noteappm.databinding.GroupListItemBinding
import com.viol4tsf.noteappm.model.Group
import com.viol4tsf.noteappm.ui.viewmodel.NoteViewModel

class GroupListAdapter(private val noteViewModel: NoteViewModel, val listener: (Group) -> Unit): RecyclerView.Adapter<GroupListAdapter.GroupListViewHolder>() {

    private lateinit var context: Context

    class GroupListViewHolder(val itemBinding: GroupListItemBinding): RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Group>(){
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.groupName == newItem.groupName
        }

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupListViewHolder {
        context = parent.context
        return GroupListAdapter.GroupListViewHolder(
            GroupListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GroupListViewHolder, position: Int) {
        val currentGroup = differ.currentList[position]

        holder.itemBinding.groupNameTextView.text = currentGroup.groupName

        holder.itemView.setOnLongClickListener{

            val popup = PopupMenu(context, holder.itemView)
            popup.inflate(R.menu.context_group_item_menu)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    return when(item?.itemId){
                        R.id.editGroupMenu -> {
                            val group: Group = currentGroup
                            listener(group)
                            return true
                        }
                        R.id.deleteGroupMenu -> {
                            val builder = AlertDialog.Builder(context)
                                .setTitle("Удалить группу?")
                                .setMessage("Вы уверены что хотите удалить выбранную группу?")
                                .setPositiveButton("Да") {_, _ ->
                                    noteViewModel.deleteGroup(currentGroup)
                                }
                                .setNegativeButton("Нет"){dialogInterface, _ ->
                                    dialogInterface.cancel()
                                }
                            val alertDialog: AlertDialog = builder.create()
                            alertDialog.setCancelable(true)
                            alertDialog.show()
                            return true
                        }
                        else -> return false
                    }
                }

            })
            popup.show()

            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}