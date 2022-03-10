package com.example.flowpass.load

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
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
        if (viewModel.password.value.isNullOrEmpty() || viewModel.seed.value.isNullOrEmpty()) {
            showWarning("Passcode and seed must be entered.")
            return
        }

        // Requesting permission to load files
        if (checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showWarning("need permission to read passwords backup")
            }
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 96674)
        } else {
            importCreateAndCheck()
        }
    }

    // Watches for when permission result comes through
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != RESULT_CANCELED && requestCode == 96674) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                importCreateAndCheck()
            } else {
                showWarning("permission not granted")
                viewModel.onLoadComplete()
            }
        }
    }

    // Does the import and setup if permissions have come through
    private fun importCreateAndCheck() {
        val pass = viewModel.password.value!!
        val location = viewModel.path.value!!
        val seed = viewModel.seed.value!!

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