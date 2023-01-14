package com.viol4tsf.noteappm.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.viol4tsf.noteappm.ui.MainActivity
import com.viol4tsf.noteappm.R
import com.viol4tsf.noteappm.databinding.FragmentNewNoteBinding
import com.viol4tsf.noteappm.model.Group
import com.viol4tsf.noteappm.model.Note
import com.viol4tsf.noteappm.other.toast
import com.viol4tsf.noteappm.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewNoteFragment : Fragment(R.layout.fragment_new_note) {

    private var _binding: FragmentNewNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        _binding = FragmentNewNoteBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()

        noteViewModel = (activity as MainActivity).noteViewModel
        mView = view

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.new_note_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.saveMenu -> {
                        saveNote(mView)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val data: List<String> = mutableListOf("")

        CoroutineScope(Dispatchers.Default).launch {
            val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                android.R.id.text1,
                data
            )
            (1..noteViewModel.getGroup().size).forEach {
                spinnerAdapter.add(noteViewModel.getGroup()[it-1])
            }
            noteViewModel.getGroup().size
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAdapter.notifyDataSetChanged()
            binding.groupSpinner.adapter = spinnerAdapter
        }
    }

    private fun saveNote(view: View){

        val noteTitle = binding.noteTitleEditText.text.toString().trim()
        val noteBody = binding.noteBodyEditText.text.toString().trim()
        val noteGroup = binding.groupSpinner.selectedItem.toString().trim()

        if (noteTitle.isNotEmpty()){
            val note = Note(0, noteTitle, noteBody, System.currentTimeMillis(), noteGroup)

            noteViewModel.addNote(note)
            Snackbar.make(view, "Заметка добавлена", Snackbar.LENGTH_SHORT).show()

            view.findNavController().navigate(R.id.action_newNoteFragment_to_homeFragment)
        } else {
            activity?.toast("Пожалуйста, впишите заголовок")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}