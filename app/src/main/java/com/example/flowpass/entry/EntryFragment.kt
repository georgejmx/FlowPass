package com.example.flowpass.entry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.flowpass.MainActivity
import com.example.flowpass.R
import com.example.flowpass.database.DatabaseFilter
import com.example.flowpass.database.Entry
import com.example.flowpass.databinding.FragmentEntryBinding
import java.util.*

/**
 * A simple [Fragment] subclass to define a zoomed in view for when the user clicks on an entry
 * from the home view
 */
class EntryFragment : Fragment() {

    // Initialising binding, model and arguments
    private lateinit var binding: FragmentEntryBinding
    private lateinit var viewModel: EntryViewModel
    private lateinit var args: EntryFragmentArgs

    // Mostly boilerplate code for combining all files together. Also code that connects
    // [DatabaseAdapter] similar to in [HomeFragment]
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        args = EntryFragmentArgs.fromBundle(requireArguments())
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_entry, container, false
        )
        viewModel = ViewModelProvider(this)[EntryViewModel::class.java]
        initViewModel()
        binding.entryViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.eventEdit.observe(viewLifecycleOwner, {
            hasSent -> if(hasSent) { processEditEntry() }
        })
        viewModel.eventDelete.observe(viewLifecycleOwner, {
            hasSent -> if(hasSent) { processDeleteEntry() }
        })
        (activity as MainActivity).menu.isEntryMarkedDeleted.observe(viewLifecycleOwner, {
            hasConfirmed -> if(hasConfirmed) { handleDeleteEntry() }
        })

        Log.i("EntryFragment", "created and initialised")
        return binding.root
    }

    // Called on object creation, initialises [EntryViewModel] with data for viewing
    private fun initViewModel() {
        val entryIdInt = args.entryId.toInt()
        Log.i("EntryViewModel", args.entryId.toString())
        viewModel.entryId = entryIdInt
        viewModel.initialiseEntry(requireActivity())
    }

    // Display a short message to the UI
    private fun showWarning(msg: String) {
        Toast.makeText(requireActivity().applicationContext, msg, Toast.LENGTH_LONG).show()
    }

    // Called when user deletes an entry. Updates the db, navigates back Home
    private fun processEditEntry() {
        Log.i("EntryFragment", "processing entry edit")
        val dbFilter = DatabaseFilter(requireActivity())
        try {
            dbFilter.editEntry(Entry(
                args.entryId.toInt(),
                viewModel.name.value.toString(),
                viewModel.key1.value.toString(),
                viewModel.key2.value.toString(),
                viewModel.key3.value.toString(),
                viewModel.pass.value.toString()
            ))
        } catch (err: InvalidPropertiesFormatException) {
            showWarning("Entry not edited; a proposed field is too long for the database. " +
                    "Max length is 15, name and password can be longer. ")
        } catch (err: Exception) {
            Log.i("EntryFragment", "Error when interacting with encrypted db")
            err.printStackTrace()
            showWarning("Unable to edit entry")
        }
        viewModel.onEditComplete()
        findNavController().navigate(EntryFragmentDirections.actionEntryFragmentToHomeFragment())
    }

    // Called when user deletes an entry using the popup dialog. Updates the db, navigates back Home
    private fun handleDeleteEntry() {
        Log.i("EntryFragment", "processing entry delete")
        val dbFilter= DatabaseFilter(requireActivity())
        try {
            dbFilter.deleteEntry(args.entryId.toInt())
        } catch (err: Exception) {
            err.printStackTrace()
            showWarning("Unable to delete entry")
        }
        (activity as MainActivity).menu.isEntryMarkedDeleted.value = false
        viewModel.onDeleteComplete()
        findNavController().navigate(EntryFragmentDirections.actionEntryFragmentToHomeFragment())
    }

    // Called when user attempts to delete entry
    private fun processDeleteEntry() {
        Log.i("EntryFragment", "processing delete button click")
        (activity as MainActivity).menu.showConfirmDelete()
    }

}