package com.example.flowpass.load

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * File that handles all non-UI based variables for the Load Backup screen
 */
class LoadViewModel : ViewModel() {


    // Event that triggers completion of registration
    private val _eventLoad = MutableLiveData<Boolean>()
    val eventLoad: LiveData<Boolean>
        get() = _eventLoad

    // Stores what has been entered as the password
    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password

    // Stores what has been entered as the backup path
    private val _path = MutableLiveData<String>()
    val path: LiveData<String>
        get() = _path

    // Stores the inputted seed
    private val _seed = MutableLiveData<String>()
    val seed: LiveData<String>
        get() = _seed

    init {
        _path.value = "reservoir"
        Log.i("LoadViewModel", "created")
    }

    // Gets the UI prompt string
    fun getPrompt(): String {
        return "Password and seed values must match those provided upon " +
                "registration. \nBackup location is the path to backup file."
    }

    // Updates the first entered password upon user input
    fun onPassTextChanged(text: CharSequence) {
        val pass = text.toString()
        _password.value = pass
    }

    // Updates the first entered password upon user input
    fun onPathTextChanged(text: CharSequence) {
        val path = text.toString()
        _path.value = path
    }

    // Updates seed phrase
    fun onSeedTextChanged(text: CharSequence) {
        val seed = text.toString()
        _seed.value = seed
    }

    // Trigger loading of backup
    fun onLoad() { _eventLoad.value = true }

    // Reset this variable to what it was before
    fun onLoadComplete() { _eventLoad.value = false }

    // Marks when this component has been destroyed
    override fun onCleared() {
        super.onCleared()
        Log.i("RegisterViewModel", "destroyed")
    }
}