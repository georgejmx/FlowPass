package com.example.flowpass.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * File that handles all non-UI based variables for the Register screen
 */
class RegisterViewModel : ViewModel() {

    var seed = ""

    // Event that triggers completion of registration
    private val _eventRegister = MutableLiveData<Boolean>()
    val eventRegister: LiveData<Boolean>
        get() = _eventRegister

    // Stores what has been entered as the password
    private val _passwordStrings = MutableLiveData<Array<String>>()
    val passwordStrings: LiveData<Array<String>>
        get() = _passwordStrings

    // Stores the registration type
    private val _eventTypeChange = MutableLiveData<Int>()
    val eventTypeChange: LiveData<Int>
        get() = _eventTypeChange

    init {
        Log.i("RegisterViewModel", "created")
        _passwordStrings.value = arrayOf("", "")
    }

    // Gets the alert prompt for when password criteria needs to be made clear
    fun getPasswordPrompt(): String {
        return "-Password and seed must be from 8 to 30 characters long\n" +
                "-Atleast 1 number must be included\n" +
                "-Must be atleast 1 uppercase and 1 lowercase letter\n" +
                "-No spaces are allowed"

    }

    // Updates the first entered password upon user input
    fun onPassTextChanged(num: Int, text: CharSequence) {
        val pass = text.toString()
        _passwordStrings.value!![num-1] = pass
    }

    // Updates seed phrase
    fun onSeedTextChanged(text: CharSequence) {
        seed = text.toString()
    }

    // Trigger registration of the app
    fun onRegister() {
        _eventRegister.value = true
        if (_passwordStrings.value!![0].isNotEmpty()) {
            Log.i("RegisterViewModel", _passwordStrings.value!![0])
        }
    }

    // Reset this variable to what it was before
    fun onRegisterComplete() { _eventRegister.value = false }

    // Marks when this component has been destroyed
    override fun onCleared() {
        super.onCleared()
        Log.i("RegisterViewModel", "destroyed")
    }
}