package com.example.flowpass

import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import com.example.flowpass.database.DatabaseFilter
import kotlin.system.exitProcess

/**
 * A class for handling user interactions and the behaviour of the options menu
 */
class MenuController(val activity: MainActivity) {

    // Booleans that track the state of user interactions with the options menu
    var isEntryMarkedDeleted = MutableLiveData<Boolean>()

    // Reset all app data to a blank install, force closes the app in the process of this
    private fun resetApp() {
        try {
            activity.rvr.clearData()
            val dbFilter = DatabaseFilter(activity)
            dbFilter.deleteAllEntries()
            exitProcess(0)
        } catch (err: ReservoirClosedException) {
            Toast.makeText(
                activity.applicationContext, "You must login first", Toast.LENGTH_LONG
            ).show()
        } catch (err: Exception) {
            err.printStackTrace()
            Toast.makeText(
                activity.applicationContext, "Unable to reset app", Toast.LENGTH_LONG
            ).show()
        }
    }

    // Render a dialog onto the UI that responds to the options button that has been clicked
    private fun basicAlert(header: String, body: String, whichDialog: Int) {
        val builder = AlertDialog.Builder(activity)
        with (builder) {
            setTitle(header)
            setMessage(body)
            when (whichDialog) {
                0 -> {
                    setPositiveButton("Ok", null)
                }
                1 -> {
                    val ok = { _: DialogInterface, _: Int -> resetApp() }
                    setPositiveButton("Proceed", DialogInterface.OnClickListener(ok))
                    setNegativeButton("Cancel", null)
                }
                2 -> {
                    setPositiveButton("Ok", null)
                }
                3 -> {
                    val ok = { _: DialogInterface, _: Int -> isEntryMarkedDeleted.value = true }
                    setPositiveButton("Proceed", DialogInterface.OnClickListener(ok))
                    setNegativeButton("Cancel", null)
                }
            }
            show()
        }
    }

    // Respond to when the user clicks the help option
    fun showHelp() {
        val title = "FlowPass Help Guide"
        val msg = "-First signup using the registration button to set your password\n" +
                "-Then login using the Welcome page each time you want to access password data\n" +
                "-Add and view passwords from the Home page; which stores all data in the app\n" +
                "-Clicking on a row allows editing and deleting that entry via a new page."
        basicAlert(title, msg, 0)
    }

    // Respond to when the user clicks the reset option
    fun showReset() {
        val title = "RESET APP"
        val msg = "Are you sure? Proceeding will delete all user data from this device...\n\n" +
                "Note: FlowPass will be force closed after this. Open again to a blank install."
        basicAlert(title, msg, 1)
    }

    // Respond to when the user clicks the about option
    fun showAbout() {
        val title = "About"
        val msg = "FlowPass; a light and secure password storage app\n\n" +
                "Version: 0.4\n" +
                "Created and maintained by: peteburns\n" +
                "Encryption standard: MD5 hashing, obfuscated AES symmetric data storage"
        basicAlert(title, msg, 2)
    }

    // Respond to when the user attempts to delete an entry, called from [EntryFragment]
    fun showConfirmDelete() {
        val title = "Delete Entry"
        val msg = "Are you sure? Proceeding will completely remove this entry from the encrypted " +
                "database, meaning existing password data is lost forever.."
        basicAlert(title, msg, 3)
    }

}