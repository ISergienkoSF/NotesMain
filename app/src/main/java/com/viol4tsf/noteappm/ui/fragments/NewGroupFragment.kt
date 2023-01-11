package com.viol4tsf.noteappm.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.viol4tsf.noteappm.R
import com.viol4tsf.noteappm.databinding.FragmentNewGroupBinding
import com.viol4tsf.noteappm.model.Group
import com.viol4tsf.noteappm.model.Note
import com.viol4tsf.noteappm.other.toast
import com.viol4tsf.noteappm.ui.MainActivity
import com.viol4tsf.noteappm.ui.viewmodel.NoteViewModel

class NewGroupFragment : Fragment(R.layout.fragment_new_group) {

    private var _binding: FragmentNewGroupBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewGroupBinding.inflate(
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
                menuInflater.inflate(R.menu.new_group_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.saveGroupMenu -> {
                        saveGroup(mView)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun saveGroup(view: View){

        val groupName = binding.addGroupNameEditText.text.toString().trim()

        if (groupName.isNotEmpty()){
            val group = Group(groupName)

            noteViewModel.addGroup(group)
            Snackbar.make(view, "Группа добавлена", Snackbar.LENGTH_SHORT).show()

            view.findNavController().navigate(R.id.action_newGroupFragment_to_homeFragment)
        } else {
            activity?.toast("Пожалуйста, впишите название группы")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}