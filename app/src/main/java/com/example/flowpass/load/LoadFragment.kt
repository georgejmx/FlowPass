package com.example.flowpass.load

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
import com.example.flowpass.MainActivity
import com.example.flowpass.R
import com.example.flowpass.database.DatabaseFilter
import com.example.flowpass.databinding.FragmentLoadBinding
import com.example.flowpass.register.RegisterFragmentDirections
import java.security.GeneralSecurityException
import java.security.MessageDigest

/**
 * A simple [Fragment] that defines load backup screen
 */
class LoadFragment : Fragment() {

    // Initialising binding and model
    private lateinit var binding: FragmentLoadBinding
    private lateinit var viewModel: LoadViewModel

    // Mostly boilerplate code for combining related files
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_load, container, false
        )
        viewModel = ViewModelProvider(this)[LoadViewModel::class.java]
        binding.loadViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.eventLoad.observe(viewLifecycleOwner, {
                hasLoaded -> if(hasLoaded) { onLoad() }
        })

        Log.i("LoadFragment", "created")
        return binding.root
    }

    // Hash any input String into a ByteArray
    private fun passToHash(pass: String): ByteArray {
        val passInBytes = pass.toByteArray(Charsets.UTF_8)
        val digester = MessageDigest.getInstance("MD5")
        return digester.digest(passInBytes)
    }

    // Display an error message if registration cannot be completed
    private fun showWarning(message: String) {
        Toast.makeText(requireActivity().applicationContext, message, Toast.LENGTH_LONG).show()
    }

    // Navigate to the home screen once the app has been unlocked
    private fun onLoad() {
        val pass = viewModel.password.value
        val location = viewModel.path.value!!
        val seed = viewModel.seed.value
        if (pass.isNullOrEmpty() || seed.isNullOrEmpty()) {
            showWarning("Passcode and seed must be entered.")
            return
        }

        // Import a backup database to bind to registration
        val dbFilter = DatabaseFilter(requireActivity())
        if (!dbFilter.importDb(location)) {
            showWarning("database could not be imported")
            return
        }

        // Create the reservoir; setting security keys and seed
        try {
            (activity as MainActivity).rvr.create(passToHash(pass), seed)
            (activity as MainActivity).rvr.open(passToHash(pass))
        } catch (err: Exception) {
            err.printStackTrace()
            showWarning("unable to setup reservoir")
            return
        }

        // Trying decryption of existing database
        try {
            val entries = dbFilter.getEntries()
            if (entries.isEmpty()) {
                throw GeneralSecurityException("unable to get entries")
            }
        } catch (err: Exception) {
            err.printStackTrace()
            showWarning("loading backup failed; null or incompatible entries")
            return
        }

        // After complete validation, navigate to home page
        val action = LoadFragmentDirections.actionLoadFragmentToHomeFragment()
        showWarning("app configured to backup database")
        NavHostFragment.findNavController(this).navigate(action)
        viewModel.onLoadComplete()
    }

}