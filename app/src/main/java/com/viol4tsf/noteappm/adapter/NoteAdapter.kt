package com.viol4tsf.noteappm.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.viol4tsf.noteappm.R
import com.viol4tsf.noteappm.databinding.NoteItemBinding
import com.viol4tsf.noteappm.fragments.HomeFragmentDirections
import com.viol4tsf.noteappm.model.Note
import java.util.*

class NoteAdapter: RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(val itemBinding: NoteItemBinding): RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
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
            100,
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        )
        holder.itemBinding.noteLL.setBackgroundColor(color)

        holder.itemView.setOnClickListener{ mView ->
            val direction = HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment(currentNote)
            mView.findNavController().navigate(direction)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}