package com.example.flowpass.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * File that handles all non-UI based variables for the Home screen
 */
class HomeViewModel : ViewModel() {

    // Marks whether the passwords table is empty
    var entriesCount: Int = 0

    // Event that triggers adding new data to the app
    private val _eventAdd = MutableLiveData<Boolean>()
    val eventAdd: LiveData<Boolean>
        get() = _eventAdd

    // Event that triggers locking of the database
    private val _eventExit = MutableLiveData<Boolean>()
    val eventExit: LiveData<Boolean>
        get() = _eventExit

    // Integer that codes the selected entry of the screen
    private val _selectionId = MutableLiveData<Int>()
    val selectionId: LiveData<Int>
        get() = _selectionId

    init {
        Log.i("HomeViewModel", "created")
        _selectionId.value = -1
    }

    // Sets the correct prompt on the UI depending on `entriesCount`
    fun getPrompt(): String {
        if (entriesCount == 0) {
            return "Click the + button to add your first entry.."
        } else if (entriesCount < 3) {
            return "Click an entry to expand.."
        }
        return ""
    }

    // Passes the id of selected entry to a property; for use by [EntryFragment]
    fun onEntrySelected(id: Int) { _selectionId.value = id }

    // Reset the default entry placeholder value to -1
    fun onEntrySelectedComplete() { _selectionId.value = -1 }

    // Marks when user wishes to add a new password
    fun onAdd() { _eventAdd.value = true }

    // Resets event
    fun onAddComplete() { _eventAdd.value = false }

    // Marks when the user wishes to exit back to [WelcomeFragment]
    fun onExit() { _eventExit.value = true }

    // Resets event
    fun onExitComplete() { _eventExit.value = false }
}