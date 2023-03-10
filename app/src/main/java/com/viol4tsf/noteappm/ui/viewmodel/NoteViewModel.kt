package com.viol4tsf.noteappm.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.viol4tsf.noteappm.db.relations.GroupWithNotes
import com.viol4tsf.noteappm.model.Group
import com.viol4tsf.noteappm.model.Note
import com.viol4tsf.noteappm.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(
    app: Application,
    private val noteRepository: NoteRepository
): AndroidViewModel(app) {

    //для загрузки папки после нажатия на нее в списке
    val mutableSelectedGroup: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val mutableSelectedIdGroup: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    fun addNote(note: Note) = viewModelScope.launch {
        noteRepository.addNote(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        noteRepository.updateNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        noteRepository.deleteNote(note)
    }

    fun getAllNotes() = noteRepository.getAllNotes()

    fun getNote() = noteRepository.getNote()

    fun searchNotes(query: String?) = noteRepository.searchNotes(query)

    fun addGroup(group: Group) = viewModelScope.launch {
        noteRepository.addGroup(group)
    }

    fun updateGroup(group: Group) = viewModelScope.launch {
        noteRepository.updateGroup(group)
    }

    fun deleteGroup(group: Group) = viewModelScope.launch {
        noteRepository.deleteGroup(group)
    }

    fun getAllGroups() = noteRepository.getAllGroups()

    fun getGroupWithNotes(groupName: String) = noteRepository.getGroupWithNotes(groupName)

    fun selectGroupWithNotes(query: String) = noteRepository.selectGroupWithNotes(query)

    fun getGroup() = noteRepository.getGroup()

}