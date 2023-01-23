package com.viol4tsf.noteappm.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.viol4tsf.noteappm.R
import com.viol4tsf.noteappm.databinding.GroupDialogBinding
import com.viol4tsf.noteappm.databinding.GroupListItemBinding
import com.viol4tsf.noteappm.db.relations.GroupWithNotes
import com.viol4tsf.noteappm.model.Group
import com.viol4tsf.noteappm.model.Note
import com.viol4tsf.noteappm.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupListAdapter(private val noteViewModel: NoteViewModel): RecyclerView.Adapter<GroupListAdapter.GroupListViewHolder>() {

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
        holder.itemView.setOnClickListener{
            noteViewModel.mutableSelectedGroup.value = currentGroup.groupName
            noteViewModel.mutableSelectedIdGroup.value = position
            it.findNavController().navigate(R.id.action_groupListFragment_to_homeFragment)
        }

        holder.itemView.setOnLongClickListener{

            val popup = PopupMenu(context, holder.itemView)
            popup.inflate(R.menu.context_group_item_menu)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    return when(item?.itemId){
                        R.id.editGroupMenu -> {
                            showUpdateGroupAlertDialog(currentGroup)
                            return true
                        }
                        R.id.deleteGroupMenu -> {
                            val builder = AlertDialog.Builder(context)
                                .setTitle("Удалить группу?")
                                .setMessage("Вы уверены что хотите удалить выбранную группу?")
                                .setPositiveButton("Да") {_, _ ->
                                    CoroutineScope(Dispatchers.Default).launch {
                                        (1..noteViewModel.getNote().size).forEach{
                                            val currentGroupName = currentGroup.groupName
                                            val note = noteViewModel.getNote()[it-1]
                                            if (currentGroupName == note.groupName){
                                                val updNote = Note(note.id, note.noteTitle, note.noteBody, note.creationDate, "")
                                                noteViewModel.updateNote(updNote)
                                            }
                                        }
                                    }
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

    private fun showUpdateGroupAlertDialog(group: Group){
        val dialogBinding: GroupDialogBinding = GroupDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.newGroupInputEditText.setText(group.groupName)

        val currentGroupName = group.groupName

        val dialog: AlertDialog = AlertDialog.Builder(context)
            .setTitle("Редактирование папки")
            .setView(dialogBinding.root)
            .setPositiveButton("Изменить", null)
            .create()

        dialog.setOnShowListener{
            dialogBinding.newGroupInputEditText.requestFocus()

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
                val name = dialogBinding.newGroupInputEditText.text.toString().trim()
                if (name.isBlank()){
                    dialogBinding.newGroupInputEditText.error = "Пустое значение"
                    return@setOnClickListener
                }
                val updGroup = Group(group.id, name)
                noteViewModel.updateGroup(updGroup)
                CoroutineScope(Dispatchers.Default).launch {
                    (1..noteViewModel.getNote().size).forEach{
                        val note = noteViewModel.getNote()[it-1]
                        if (currentGroupName == note.groupName){
                            val updNote = Note(note.id, note.noteTitle, note.noteBody, note.creationDate, name)
                            noteViewModel.updateNote(updNote)
                        }
                    }
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}