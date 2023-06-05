package com.codingWithUmair.app.notify.data

import com.codingWithUmair.app.notify.data.dataBase.NoteDao
import com.codingWithUmair.app.notify.model.Note
import kotlinx.coroutines.flow.Flow

interface NotifyRepository {

	fun getAllNotes(): Flow<List<Note>>

	fun getNotesByQuery(query: String): Flow<List<Note>>

	suspend fun addNoteToDataBase(note: Note)

	suspend fun updateNote(note: Note)

	suspend fun deleteNote(note: Note)

	suspend fun deleteAllNotes(notes: List<Note>)

}

class OfflineNotifyRepository(private val noteDao: NoteDao): NotifyRepository {

	override fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

	override fun getNotesByQuery(query: String): Flow<List<Note>> = noteDao.getNotesByQuery(query)

	override suspend fun addNoteToDataBase(note: Note) = noteDao.addNoteToDataBase(note)

	override suspend fun updateNote(note: Note) = noteDao.updateNote(note)

	override suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

	override suspend fun deleteAllNotes(notes: List<Note>) = noteDao.deleteAllNotes(notes)

}