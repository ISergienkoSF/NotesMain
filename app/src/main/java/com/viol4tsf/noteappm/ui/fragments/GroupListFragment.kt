package com.viol4tsf.noteappm.ui.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.viol4tsf.noteappm.R
import com.viol4tsf.noteappm.adapter.GroupListAdapter
import com.viol4tsf.noteappm.databinding.FragmentGroupListBinding
import com.viol4tsf.noteappm.databinding.GroupDialogBinding
import com.viol4tsf.noteappm.model.Group
import com.viol4tsf.noteappm.ui.MainActivity
import com.viol4tsf.noteappm.ui.viewmodel.NoteViewModel

class GroupListFragment : Fragment() {

    private var _binding: FragmentGroupListBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var groupListAdapter: GroupListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupListBinding.inflate(
            inflater,
            container,
            false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel = (activity as MainActivity).noteViewModel

        setUpGroupsRecyclerView()

        binding.addGroupFloatingActionButton.setOnClickListener{
            showNewGroupAlertDialog()
        }
    }

    private fun setUpGroupsRecyclerView(){
        groupListAdapter = GroupListAdapter(noteViewModel) { group ->
            showUpdateGroupAlertDialog(group)
        }

        binding.groupsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = groupListAdapter
        }

        activity?.let {
            noteViewModel.getAllGroups().observe(viewLifecycleOwner) { groups ->
                groupListAdapter.differ.submitList(groups)
                updateUI(groups)
            }
        }
    }

    private fun updateUI(groups: List<Group>){
        if (groups.isNotEmpty()){
            binding.groupsRecyclerView.visibility = View.VISIBLE
            binding.noGroupsTextView.visibility = View.GONE
        } else {
            binding.groupsRecyclerView.visibility = View.GONE
            binding.noGroupsTextView.visibility = View.VISIBLE
        }
    }

    private fun showNewGroupAlertDialog() {
        val dialogBinding: GroupDialogBinding = GroupDialogBinding.inflate(layoutInflater)

        val dialog: AlertDialog = AlertDialog.Builder(context)
            .setTitle("Новая папка")
            .setView(dialogBinding.root)
            .setPositiveButton("Создать", null)
            .create()

        dialog.setOnShowListener{
            dialogBinding.newGroupInputEditText.requestFocus()

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
                val name: String = dialogBinding.newGroupInputEditText.text.toString().trim()
                if (name.isBlank()){
                    dialogBinding.newGroupInputEditText.error = "Пустое значение"
                    return@setOnClickListener
                }
                val group = Group(name)
                noteViewModel.addGroup(group)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showUpdateGroupAlertDialog(group: Group){
        val dialogBinding: GroupDialogBinding = GroupDialogBinding.inflate(layoutInflater)
        dialogBinding.newGroupInputEditText.setText(group.groupName)

        val dialog: AlertDialog = AlertDialog.Builder(context)
            .setTitle("Редактирование папки")
            .setView(dialogBinding.root)
            .setPositiveButton("Изменить", null)
            .create()

        dialog.setOnShowListener{
            dialogBinding.newGroupInputEditText.requestFocus()

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
                val name: String = dialogBinding.newGroupInputEditText.text.toString().trim()
                if (name.isBlank()){
                    dialogBinding.newGroupInputEditText.error = "Пустое значение"
                    return@setOnClickListener
                }
                val updGroup = Group(name)
                noteViewModel.addGroup(updGroup)
                noteViewModel.deleteGroup(group)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}