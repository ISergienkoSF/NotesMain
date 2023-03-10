package com.viol4tsf.noteappm.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
                activity?.toast("?????????????? ??????????????????")

                view.findNavController().navigate(R.id.action_updateNoteFragment_to_homeFragment)
            } else {
                activity?.toast("????????????????????, ?????????????? ??????????????????")
            }
        }

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.update_note_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.deleteMenu -> {
                        deleteNote()
                        true
                    }
                    R.id.shareMenu -> {
                        val myIntent = Intent()
                        myIntent.action = Intent.ACTION_SEND
                        myIntent.type = "type/plain"
                        val shareBody: String = binding.noteTitleUpdateEditText.text.toString() +
                                "\n" + binding.noteBodyUpdateEditText.text.toString()
                        myIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                        startActivity(Intent.createChooser(myIntent, "???????????????????? ????????????????"))
                        return true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        CoroutineScope(Dispatchers.Default).launch {

            val groups = noteViewModel.getGroup()
            val name = currentNote.groupName
            val value: String = if(name in groups){
                name
            } else {
                ""
            }
            val data: List<String> = mutableListOf(value)

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

        val startSum = binding.noteBodyUpdateEditText.text.length
        binding.symbolSumUpdateTextView.text = "???????????????????? ????????????????: $startSum"

        binding.noteBodyUpdateEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val sum = s?.length
                binding.symbolSumUpdateTextView.text = "???????????????????? ????????????????: $sum"
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun deleteNote(){
        AlertDialog.Builder(activity).apply {
            setTitle("???????????????? ??????????????")
            setMessage("???? ??????????????, ?????? ???????????? ?????????????? ???????????????")
            setPositiveButton("??????????????"){ _, _ ->
                noteViewModel.deleteNote(currentNote)
                view?.findNavController()?.navigate(R.id.action_updateNoteFragment_to_homeFragment)
            }
            setNegativeButton("????????????", null)
        }.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}