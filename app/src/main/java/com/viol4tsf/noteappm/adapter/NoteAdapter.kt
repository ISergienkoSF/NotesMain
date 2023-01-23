package com.viol4tsf.noteappm.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.viol4tsf.noteappm.R
import com.viol4tsf.noteappm.databinding.NoteItemBinding
import com.viol4tsf.noteappm.model.Note
import com.viol4tsf.noteappm.ui.fragments.HomeFragmentDirections
import com.viol4tsf.noteappm.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class NoteAdapter(private val noteViewModel: NoteViewModel): RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private lateinit var context: Context

    //создание viewholder для адаптера
    class NoteViewHolder(val itemBinding: NoteItemBinding): RecyclerView.ViewHolder(itemBinding.root)

    //коллбек для обнаружения изменений в списке и замене эо=лементов при их изменении
    private val differCallback = object : DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    //асинхронный список
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        context = parent.context
        return NoteViewHolder(
            NoteItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = differ.currentList[position]

        holder.itemBinding.noteTitleTextView.text = currentNote.noteTitle
        holder.itemBinding.noteBodyTextView.text = currentNote.noteBody

        val random = Random()
        val color = Color.argb(
            50,
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        )
        holder.itemBinding.noteLL.setBackgroundColor(color)

        holder.itemView.setOnClickListener{ mView ->
            val direction = HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment(currentNote)
            mView.findNavController().navigate(direction)
        }

        holder.itemView.setOnLongClickListener{

            val popup = PopupMenu(context, holder.itemView)
            popup.inflate(R.menu.context_note_item_menu)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    return when(item?.itemId){
                        R.id.deleteNoteContextMenu -> {
                            val builder = AlertDialog.Builder(context)
                                .setTitle("Удалить заметку?")
                                .setMessage("Вы уверены что хотите удалить выбранную заметку?")
                                .setPositiveButton("Да") {_, _ ->
                                    noteViewModel.deleteNote(currentNote)
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