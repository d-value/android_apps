package com.example.mytodolist.viewmodels

import android.util.Log
import android.widget.CheckBox
import androidx.lifecycle.*
import com.example.mytodolist.data.Note
import com.example.mytodolist.data.NoteDao
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class NotesViewModel(private val noteDao: NoteDao) : ViewModel(){

    var allNotes = noteDao.getAllByName().asLiveData()

    val displayType = MutableLiveData(DisplayType.ALL)
    val sortType = MutableLiveData(SortType.BY_NAME)

    val hasTime = MutableLiveData(false)
    var noteToEdit = Note(0,"", done = false, false,"",false)
    var noteToAdd = Note(0,"", done = false, false,"23:59",false)

    fun getNotes(){
        when(displayType.value){
            DisplayType.ALL-> {
                when(sortType.value) {
                    SortType.BY_NAME -> allNotes = noteDao.getAllByName().asLiveData()
                    SortType.BY_TIME -> allNotes = noteDao.getAllByTime().asLiveData()
                }
            }
            DisplayType.UNDONE -> {
                when(sortType.value){
                    SortType.BY_NAME -> allNotes = noteDao.getUndoneByName().asLiveData()
                    SortType.BY_TIME -> allNotes = noteDao.getUndoneByTime().asLiveData()
                }
            }
        }
    }

    fun showTime(shown: Boolean){
        hasTime.value = shown
    }

    fun isEntryValid(noteContent: String) : Boolean{
        return noteContent.isNotBlank()
    }

    fun insert(note: Note){
        viewModelScope.launch {
            noteDao.insert(note)
        }
    }

    fun delete(vararg note: Note){
        viewModelScope.launch {
            noteDao.delete(*note)
        }
    }

    fun update(note: Note){
        viewModelScope.launch {
            noteDao.update(note)
        }
    }

    fun clear(){
        viewModelScope.launch {
            noteDao.clear()
        }
    }

    fun deleteDoneNote(){
        viewModelScope.launch {
            noteDao.deleteDoneNotes()
        }
    }

    fun clearNoteToAdd(){
        noteToAdd = Note(0,"", done = false, false,"23:59",false)
    }
}
enum class DisplayType{
    ALL, UNDONE
}

enum class SortType{
    BY_NAME, BY_TIME
}

class NotesViewModelFactory(private val noteDao: NoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(noteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
