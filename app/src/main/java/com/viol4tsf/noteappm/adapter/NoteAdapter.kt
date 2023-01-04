package com.viol4tsf.noteappm.adapter

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

class NoteAdapter: RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var binding: NoteItemBinding? = null

    class NoteViewHolder(itemBinding: NoteItemBinding): RecyclerView.ViewHolder(itemBinding.root)

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
        binding = NoteItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return NoteViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = differ.currentList[position]

        holder.itemView.apply {

            binding?.noteTitleTextView?.text = currentNote.noteTitle
            binding?.noteBodyTextView?.text = currentNote.noteBody

        }.setOnClickListener{ mView ->
            val direction = HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment(currentNote)
            mView.findNavController().navigate(
                direction
            )
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}