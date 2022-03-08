package com.example.flowpass.welcome

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
  * File that handles all non-UI based variables for the Welcome screen
  */
class WelcomeViewModel : ViewModel() {

    // Marks whether the app has been registered or not
    var isRegistered: Boolean = false

    // Event that triggers reistration
    private val _eventSignup = MutableLiveData<Boolean>()
    val eventSignup: LiveData<Boolean>
        get() = _eventSignup

    // Event that triggers loading backup
    private val _eventLoadBackup = MutableLiveData<Boolean>()
    val eventLoadBackup: LiveData<Boolean>
        get() = _eventLoadBackup

    // Event that triggers unlocking of the database
    private val _eventUnlock = MutableLiveData<Boolean>()
    val eventUnlock: LiveData<Boolean>
        get() = _eventUnlock

    // Stores what has been entered as the current password
    private val _passwordString = MutableLiveData<String>()
    val passwordString: LiveData<String>
        get() = _passwordString

    init { Log.i("WelcomeViewModel", "created") }

    // Updates the "passwordString" property when the user changes input text
    fun onUsernameTextChanged(text: CharSequence) {
        val pass = text.toString()
        _passwordString.value = pass
    }

    // Navigate to the register screen
    fun onSignup() { _eventSignup.value = true }

    // Reset this variable to what it was before
    fun onSignupComplete() { _eventSignup.value = false }

    // Navigate to the load screen
    fun onLoadBackup() { _eventLoadBackup.value = true }

    // Reset this variable to what it was before
    fun onLoadBackupComplete() { _eventLoadBackup.value = false }

    // Attempt unlock of the database
    fun onUnlock() { _eventUnlock.value = true }

    // Reset this variable to what it was before
    fun onUnlockComplete() { _eventUnlock.value = false }

    // Marks when this component has been destroyed
    override fun onCleared() {
        super.onCleared()
        Log.i("WelcomeViewModel", "destroyed")
    }
}