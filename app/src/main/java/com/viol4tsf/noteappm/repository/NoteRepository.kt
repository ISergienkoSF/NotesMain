package com.viol4tsf.noteappm.repository

import com.viol4tsf.noteappm.db.NoteDatabase
import com.viol4tsf.noteappm.model.Note

class NoteRepository(private val db: NoteDatabase) {

    suspend fun addNote(note: Note) = db.getNoteDao().addNote(note)

    suspend fun updateNote(note: Note) = db.getNoteDao().updateNote(note)

    suspend fun deleteNote(note: Note) = db.getNoteDao().deleteNote(note)

    fun getAllNotes() = db.getNoteDao().getAllNotes()

    fun searchNotes(query: String?) = db.getNoteDao().searchNote(query)
}