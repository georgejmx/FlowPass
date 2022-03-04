package com.example.flowpass.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
  * File that makes sure new entries are correctly added to the database, via the Add screen
  */
class AddViewModel : ViewModel() {

    // Event that triggers sending new data to the database
    private val _eventSend = MutableLiveData<Boolean>()
    val eventSend: LiveData<Boolean>
        get() = _eventSend

    // Stores what has been entered as the current entry name
    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    // Stores what has been entered as key value 1
    private val _key1 = MutableLiveData<String>()
    val key1: LiveData<String>
        get() = _key1

    // Stores what has been entered as key value 2
    private val _key2 = MutableLiveData<String>()
    val key2: LiveData<String>
        get() = _key2

    // Stores what has been entered as key value 3
    private val _key3 = MutableLiveData<String>()
    val key3: LiveData<String>
        get() = _key3

    // Stores what has been entered as the current entry name
    private val _pass = MutableLiveData<String>()
    val pass: LiveData<String>
        get() = _pass

    init { Log.i("AddViewModel", "created") }

    // Updates "name" property when the user changes input text
    fun onEntryNameChanged(text: CharSequence) {
        val contents = text.toString()
        _name.value = contents
    }

    // Updates "key1" property when the user changes input text
    fun onEntryKey1Changed(text: CharSequence) {
        val contents = text.toString()
        _key1.value = contents
    }

    // Updates "key2" property when the user changes input text
    fun onEntryKey2Changed(text: CharSequence) {
        val contents = text.toString()
        _key2.value = contents
    }

    // Updates "key3" property when the user changes input text
    fun onEntryKey3Changed(text: CharSequence) {
        val contents = text.toString()
        _key3.value = contents
    }

    // Updates "pass" property when the user changes input text
    fun onEntryPassChanged(text: CharSequence) {
        val contents = text.toString()
        _pass.value = contents
    }

    // Send this data to db
    fun onSubmit() { _eventSend.value = true }

    // Resets this variable back to what it was
    fun onSubmitComplete() { _eventSend.value = false }

    // Marks when this component has been destroyed
    override fun onCleared() {
        super.onCleared()
        Log.i("AddViewModel", "destroyed")
    }
}