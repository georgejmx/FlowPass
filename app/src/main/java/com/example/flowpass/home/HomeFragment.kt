package com.example.flowpass.home

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
import com.example.flowpass.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass that defines the Home Screen of the app, where the user is able to
 * view current password entries, and perform CRUD operations. The central point of navigation for
 * the project
 */
class HomeFragment : Fragment() {

    // Initialising binding, model and adapter
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var _adapter: HomeAdapter

    // Mostly boilerplate code for combining all files together. Setting the adapter to the
    // current application context, meaning sqlite is bound to this screen
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.homeViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.eventAdd.observe(viewLifecycleOwner, {
            hasAdded -> if(hasAdded) { processAdd() }
        })
        viewModel.eventExit.observe(viewLifecycleOwner, {
            hasExited -> if(hasExited) { processExit() }
        })
        viewModel.selectionId.observe(viewLifecycleOwner, {
            entryId ->  if (entryId > 0) {
                findNavController().navigate(
                    // The arguments bundle written in Java does not like the Kotlin 'Int' type
                    HomeFragmentDirections.actionHomeFragmentToEntryFragment(entryId.toLong())
                )
                viewModel.onEntrySelectedComplete()
            }
        })
        _adapter = HomeAdapter(
            requireActivity(),
            PasswordListener { entryId -> processSelect(entryId) },
            null
        )
        binding.entryList.adapter = _adapter

        viewModel.entriesCount = _adapter.getTuples().size
        Log.i("HomeFragment", "created")
        Log.i("HomeFragment", "number of entries: ${viewModel.entriesCount}")
        return binding.root
    }

    // Display a short message to the UI
    private fun showWarning(msg: String) {
        Toast.makeText(requireActivity().applicationContext, msg, Toast.LENGTH_LONG).show()
    }

    // Stores information about a clicked password entry, that can be passed to Edit/Delete
    private fun processSelect(entryId: Int) { viewModel.onEntrySelected(entryId) }

    // Switches to the add screen
    private fun processAdd() {
        Log.i("HomeFragment", "switching to add screen")
        findNavController().navigate(R.id.action_HomeFragment_to_AddFragment)
        viewModel.onAddComplete()
    }

    // Exits the app, also secures data behind password
    private fun processExit() {
        Log.i("HomeFragment", "exiting secure app")
        (activity as MainActivity).rvr.close()
        findNavController().navigate(R.id.action_HomeFragment_to_WelcomeFragment)
        val dbFilter = DatabaseFilter(requireActivity())
        if (!dbFilter.exportDb()) {
            showWarning("database backup failed")
        }
        viewModel.onExitComplete()
    }

}