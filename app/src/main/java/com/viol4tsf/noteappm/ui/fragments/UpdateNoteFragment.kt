package com.viol4tsf.noteappm.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.viol4tsf.noteappm.ui.MainActivity
import com.viol4tsf.noteappm.R
import com.viol4tsf.noteappm.databinding.FragmentUpdateNoteBinding
import com.viol4tsf.noteappm.model.Note
import com.viol4tsf.noteappm.other.toast
import com.viol4tsf.noteappm.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdateNoteFragment : Fragment(R.layout.fragment_update_note) {

    private var _binding: FragmentUpdateNoteBinding? = null
    private val binding get() = _binding!!

    private val args: UpdateNoteFragmentArgs by navArgs()
    private lateinit var currentNote: Note
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentUpdateNoteBinding.inflate(
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
        currentNote = args.note!!
        binding.noteTitleUpdateEditText.setText(currentNote.noteTitle)
        binding.noteBodyUpdateEditText.setText(currentNote.noteBody)

        binding.updateFloatingActionButton.setOnClickListener{

            val title = binding.noteTitleUpdateEditText.text.toString().trim()
            val body = binding.noteBodyUpdateEditText.text.toString().trim()
            val group = binding.groupUpdateSpinner.selectedItem.toString().trim()

            if (title.isNotEmpty()){
                val note = Note(currentNote.id, title, body, currentNote.creationDate, group)
                noteViewModel.updateNote(note)
                activity?.toast("Заметка обновлена")

                view.findNavController().navigate(R.id.action_updateNoteFragment_to_homeFragment)
            } else {
                activity?.toast("Пожалуйста, впишите заголовок")
            }
        }

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.update_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.deleteMenu -> {
                        deleteNote()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val data: List<String> = mutableListOf(currentNote.groupName)

        CoroutineScope(Dispatchers.Default).launch {
            val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                android.R.id.text1,
                data
            )
            (1..noteViewModel.getGroup().size).forEach {
                if (currentNote.groupName != noteViewModel.getGroup()[it-1]) {
                    spinnerAdapter.add(noteViewModel.getGroup()[it - 1])
                }
            }
            noteViewModel.getGroup().size
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAdapter.notifyDataSetChanged()
            binding.groupUpdateSpinner.adapter = spinnerAdapter
        }
    }

    private fun deleteNote(){
        AlertDialog.Builder(activity).apply {
            setTitle("Удаление заметки")
            setMessage("Вы уверены, что хотите удалить заметку?")
            setPositiveButton("УДАЛИТЬ"){ _, _ ->
                noteViewModel.deleteNote(currentNote)
                view?.findNavController()?.navigate(R.id.action_updateNoteFragment_to_homeFragment)
            }
            setNegativeButton("ОТМЕНА", null)
        }.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}