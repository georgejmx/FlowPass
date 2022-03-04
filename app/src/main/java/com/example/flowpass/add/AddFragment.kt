package com.example.flowpass.add

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.flowpass.R
import com.example.flowpass.database.DatabaseFilter
import com.example.flowpass.database.Entry
import com.example.flowpass.databinding.FragmentAddBinding
import java.util.*

/**
 * A simple [Fragment] subclass to define the screen for adding new password entries
 */
class AddFragment : Fragment() {

    // Initialising binding, model and adapter
    private lateinit var binding: FragmentAddBinding
    private lateinit var viewModel: AddViewModel

    // Mostly boilerplate code for combining all files together. Also code that connects
    // [DatabaseAdapter] similar to in [HomeFragment]
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add, container, false
        )
        viewModel = ViewModelProvider(this)[AddViewModel::class.java]
        binding.addViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.eventSend.observe(viewLifecycleOwner, {
            hasSent -> if(hasSent) { processNewEntry() }
        })

        Log.i("AddFragment", "created")
        return binding.root
    }

    // Display a short message to the UI
    private fun showWarning(msg: String) {
        Toast.makeText(requireActivity().applicationContext, msg, Toast.LENGTH_LONG).show()
    }

    // Called when user is finished with the add screen
    private fun processNewEntry() {
        Log.i("AddFragment", "processing new entry")
        Log.i("AddFragment", viewModel.name.toString())

        // If there is a valid entry, add this to db. Else show a warning
        if (viewModel.name.value.toString().isNotEmpty() &&
            viewModel.pass.value.toString().isNotEmpty()) {
            val newEntry = Entry(
                null,
                viewModel.name.value.toString(),
                viewModel.key1.value.toString().lowercase(Locale.getDefault()),
                viewModel.key2.value.toString().lowercase(Locale.getDefault()),
                viewModel.key3.value.toString().lowercase(Locale.getDefault()),
                viewModel.pass.value.toString()
            )
            val dbFilter = DatabaseFilter(requireActivity())
            try {
                dbFilter.addEntry(newEntry)
            } catch (err: InvalidPropertiesFormatException) {
                showWarning("Entry not added; a field is too long for the database. Max " +
                        "length is 15, name and password can be longer. ")
            } catch (err: Exception) {
                Log.i("AddFragment", "Error when interacting with encrypted db")
                err.printStackTrace()
                showWarning("Unable to add entry")
            }
            val action = AddFragmentDirections.actionAddFragmentToHomeFragment()
            NavHostFragment.findNavController(this).navigate(action)
            Log.i("AddFragment", "Entry successfully added")
        } else {
            showWarning("Entry name and password not entered")
        }
        viewModel.onSubmitComplete()
    }
}