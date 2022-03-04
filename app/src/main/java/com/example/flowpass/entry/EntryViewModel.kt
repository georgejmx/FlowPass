package com.example.flowpass.entry

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flowpass.database.DatabaseFilter

/**
  * File that holds information about [EntryFragment], to be used by the UI and backend
  */
class EntryViewModel : ViewModel() {

    // Holds the id of the selected entry, to be passed by the Home fragment
    var entryId: Int = -1

    // Stores what has been entered as the current entry name
    val name = MutableLiveData<String>()

    // Stores what has been entered as key value 1
    val key1 = MutableLiveData<String>()

    // Stores what has been entered as key value 2
    val key2 = MutableLiveData<String>()

    // Stores what has been entered as key value 3
    val key3 = MutableLiveData<String>()

    // Stores what has been entered as the current entry name
    val pass = MutableLiveData<String>()

    // Event that triggers editing this entry
    private val _eventEdit = MutableLiveData<Boolean>()
    val eventEdit: LiveData<Boolean>
        get() = _eventEdit

    // Event that triggers deletion of this entry
    private val _eventDelete = MutableLiveData<Boolean>()
    val eventDelete: LiveData<Boolean>
        get() = _eventDelete

    init { Log.i("EntryViewModel", "created") }

    // Sets the initial values for the entry to what is in db
    fun initialiseEntry(activity: FragmentActivity) {
        val thisEntry = DatabaseFilter(activity).getEntry(entryId)
        name.value = thisEntry.name
        Log.i("EntryViewModel entry name", name.value.toString())
        key1.value = thisEntry.key1
        key2.value = thisEntry.key2
        key3.value = thisEntry.key3
        pass.value = thisEntry.password
    }

    // Updates "name" property when the user changes input text
    fun onEntryNameChanged(text: CharSequence) {
        val contents = text.toString()
        name.value = contents
    }

    // Updates "key1" property when the user changes input text
    fun onEntryKey1Changed(text: CharSequence) {
        val contents = text.toString()
        key1.value = contents
    }

    // Updates "key2" property when the user changes input text
    fun onEntryKey2Changed(text: CharSequence) {
        val contents = text.toString()
        key2.value = contents
    }

    // Updates "key3" property when the user changes input text
    fun onEntryKey3Changed(text: CharSequence) {
        val contents = text.toString()
        key3.value = contents
    }

    // Updates "pass" property when the user changes input text
    fun onEntryPassChanged(text: CharSequence) {
        val contents = text.toString()
        pass.value = contents
    }

    // Edit the current entry
    fun onEdit() { _eventEdit.value = true }

    // Marks this entry as edited
    fun onEditComplete() { _eventEdit.value = false }

    // Delete this data from db
    fun onDelete() { _eventDelete.value = true }

    // Marks this entry as deleted
    fun onDeleteComplete() { _eventDelete.value = false }

    // Marks when this component has been destroyed
    override fun onCleared() {
        super.onCleared()
        Log.i("EntryViewModel", "destroyed")
    }
}