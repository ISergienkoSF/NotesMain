package com.viol4tsf.noteappm.fragments

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.viol4tsf.noteappm.MainActivity
import com.viol4tsf.noteappm.R
import com.viol4tsf.noteappm.adapter.NoteAdapter
import com.viol4tsf.noteappm.databinding.FragmentHomeBinding
import com.viol4tsf.noteappm.model.Note
import com.viol4tsf.noteappm.viewmodel.NoteViewModel

class HomeFragment : Fragment(R.layout.fragment_home),
SearchView.OnQueryTextListener{

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()

        noteViewModel = (activity as MainActivity).noteViewModel
        setUpRecyclerView()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.home_menu, menu)

                val mMenuSearch = menu.findItem(R.id.searchMenu).actionView as SearchView
                mMenuSearch.isSubmitButtonEnabled = true
                mMenuSearch.setOnQueryTextListener(this@HomeFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                TODO("Not yet implemented")
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        binding.addFloatingActionButton.setOnClickListener{ view ->
            view.findNavController().navigate(R.id.action_homeFragment_to_newNoteFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false)

        return binding.root
    }

    private fun setUpRecyclerView(){
        noteAdapter = NoteAdapter()

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = noteAdapter
        }

        activity?.let {
            noteViewModel.getAllNotes().observe(viewLifecycleOwner) { notes ->
                noteAdapter.differ.submitList(notes)
                updateUI(notes)
            }
        }
    }

    private fun updateUI(notes: List<Note>){
        if (notes.isNotEmpty()){
            binding.recyclerView.visibility = View.VISIBLE
            binding.noNotesTextView.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.noNotesTextView.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null){
            searchNote(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            searchNote(newText)
        }
        return true
    }

    private fun searchNote(query: String?){
        val searchQuery = "%$query%"
        noteViewModel.searchNotes(searchQuery).observe(this, { nList ->
            noteAdapter.differ.submitList(nList)
        })
    }
}