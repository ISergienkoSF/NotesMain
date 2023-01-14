package com.viol4tsf.noteappm.repository

import com.viol4tsf.noteappm.db.NoteDatabase
import com.viol4tsf.noteappm.model.Group
import com.viol4tsf.noteappm.model.Note

class NoteRepository(private val db: NoteDatabase) {

    suspend fun addNote(note: Note) = db.getNoteDao().addNote(note)

    suspend fun updateNote(note: Note) = db.getNoteDao().updateNote(note)

    suspend fun deleteNote(note: Note) = db.getNoteDao().deleteNote(note)

    fun getAllNotes() = db.getNoteDao().getAllNotes()

    fun searchNotes(query: String?) = db.getNoteDao().searchNote(query)

    suspend fun addGroup(group: Group) = db.getGroupDao().addGroup(group)

    suspend fun updateGroup(group: Group) = db.getGroupDao().updateGroup(group)

    suspend fun deleteGroup(group: Group) = db.getGroupDao().deleteGroup(group)

    fun getAllGroups() = db.getGroupDao().getAllGroups()

    fun getGroupWithNotes(groupName: String) = db.getGroupDao().getGroupWithNotes(groupName)

    fun selectGroupWithNotes(query: String) = db.getNoteDao().selectGroupWithNotes(query)

    fun getGroup() = db.getGroupDao().getGroup()
}