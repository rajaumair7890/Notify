package com.codingWithUmair.app.notify.data.dataBase

import androidx.room.*
import com.codingWithUmair.app.notify.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

	@Query("SELECT * FROM note")
	fun getAllNotes(): Flow<List<Note>>

	@Query("SELECT * FROM note WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ")
	fun getNotesByQuery(query: String): Flow<List<Note>>

	@Insert(Note::class)
	suspend fun addNoteToDataBase(note: Note)

	@Update(Note::class)
	suspend fun updateNote(note: Note)

	@Delete(Note::class)
	suspend fun deleteNote(note: Note)

	@Delete(Note::class)
	suspend fun deleteAllNotes(notes: List<Note>)

}