package com.viol4tsf.noteappm.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.viol4tsf.noteappm.ui.MainActivity
import com.viol4tsf.noteappm.R
import com.viol4tsf.noteappm.adapter.GroupAdapter
import com.viol4tsf.noteappm.adapter.NoteAdapter
import com.viol4tsf.noteappm.databinding.FragmentHomeBinding
import com.viol4tsf.noteappm.model.Note
import com.viol4tsf.noteappm.ui.viewmodel.NoteViewModel

class HomeFragment : Fragment(R.layout.fragment_home),
SearchView.OnQueryTextListener{

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var groupAdapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()

        noteViewModel = (activity as MainActivity).noteViewModel

        setUpNotesRecyclerView()
        setUpGroupsRecyclerView()

        //добавление интерфейса в меню для данного фрагмента
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.home_menu, menu)

                //настройка поисковой строки
                val mMenuSearch = menu.findItem(R.id.searchMenu).actionView as SearchView
                mMenuSearch.isSubmitButtonEnabled = true
                mMenuSearch.setOnQueryTextListener(this@HomeFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                TODO("Not yet implemented")
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        //переход на фрагмент создания новой заметки
        binding.addFloatingActionButton.setOnClickListener{ mView ->
            mView.findNavController().navigate(R.id.action_homeFragment_to_newNoteFragment)
        }
        //переход на фрагмент создания новой группы
        binding.groupListImageButton.setOnClickListener{ mView ->
            mView.findNavController().navigate(R.id.action_homeFragment_to_groupListFragment)
        }

        binding.groupNameTextView.setBackgroundColor(Color.parseColor("#C5ACCC"))
        binding.groupNameTextView.setOnClickListener {
            //setUpNotesRecyclerView()
            noteViewModel.mutableSelectedGroup.value = ""
            noteViewModel.mutableSelectedIdGroup.value = -1
            setUpAllGroups()
            binding.groupNameTextView.setBackgroundColor(Color.parseColor("#C5ACCC"))
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

    override fun onResume() {
        super.onResume()
        if (noteViewModel.mutableSelectedGroup.value == ""){
            binding.groupNameTextView.setBackgroundColor(Color.parseColor("#C5ACCC"))
        }
        //setUpGroupsRecyclerView()
    }

    private fun setUpNotesRecyclerView(){
        noteAdapter = NoteAdapter(noteViewModel)
        binding.notesRecyclerView.apply {
            //создание двух столбцов в заметках
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = noteAdapter
        }

        if (noteViewModel.mutableSelectedGroup.value == null){
            activity?.let {
                noteViewModel.getAllNotes().observe(viewLifecycleOwner) { notes ->
                    noteAdapter.differ.submitList(notes)
                    updateUI(notes)
                }
            }
        }
//        activity?.let {
//            noteViewModel.getAllNotes().observe(viewLifecycleOwner) { notes ->
//                noteAdapter.differ.submitList(notes)
//                updateUI(notes)
//            }
//        }
    }

    private fun setUpGroupsRecyclerView(){
        groupAdapter = GroupAdapter(viewLifecycleOwner, noteViewModel)
        activity?.let {
            noteViewModel.mutableSelectedGroup.observe(viewLifecycleOwner, Observer { str ->
                if (str != ""){
                    binding.groupNameTextView.setBackgroundColor(Color.WHITE)
                    noteViewModel.selectGroupWithNotes(str).observe(viewLifecycleOwner) { groupNotes ->
                        noteAdapter.differ.submitList(groupNotes)
                        updateUI(groupNotes)
                    }
                } else {
                    binding.groupNameTextView.setBackgroundColor(Color.parseColor("#C5ACCC"))
                    noteViewModel.getAllNotes().observe(viewLifecycleOwner) { notes ->
                        noteAdapter.differ.submitList(notes)
                        updateUI(notes)
                    }
                }
            })
        }

        binding.groupsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = groupAdapter
        }

        activity?.let {
            noteViewModel.getAllGroups().observe(viewLifecycleOwner) { groups ->
                groupAdapter.differ.submitList(groups)
            }
        }
    }

    private fun setUpAllGroups(){
        groupAdapter = GroupAdapter(viewLifecycleOwner, noteViewModel)

        binding.groupsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = groupAdapter
        }

        activity?.let {
            noteViewModel.getAllGroups().observe(viewLifecycleOwner) { groups ->
                groupAdapter.differ.submitList(groups)
            }
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
            binding.notesRecyclerView.visibility = View.VISIBLE
            binding.noNotesTextView.visibility = View.GONE
        } else {
            binding.notesRecyclerView.visibility = View.GONE
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
        noteViewModel.searchNotes(searchQuery).observe(this) { nList ->
            noteAdapter.differ.submitList(nList)
        }
    }
}