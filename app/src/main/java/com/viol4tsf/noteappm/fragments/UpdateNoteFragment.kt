package com.viol4tsf.noteappm.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.viol4tsf.noteappm.R
import com.viol4tsf.noteappm.databinding.FragmentNewNoteBinding
import com.viol4tsf.noteappm.databinding.FragmentUpdateNoteBinding

class UpdateNoteFragment : Fragment(R.layout.fragment_update_note) {

    private var _binding: FragmentUpdateNoteBinding? = null
    private val binding get() = _binding!!

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
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.new_note_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                TODO("Not yet implemented")
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}