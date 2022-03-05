package com.example.flowpass.welcome

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.flowpass.MainActivity
import com.example.flowpass.R
import com.example.flowpass.database.DatabaseFilter
import com.example.flowpass.databinding.FragmentWelcomeBinding
import java.io.File
import java.security.MessageDigest

/**
 * A simple [Fragment] subclass to define the entrypoint of the application. Where the user enters
 * (or registers for) the password manager. Controls password validation and navigation to the main
 * fragment
 */
class WelcomeFragment : Fragment() {

    // Initialising binding and model
    private lateinit var binding: FragmentWelcomeBinding
    private lateinit var viewModel: WelcomeViewModel

    // Mostly boilerplate code for combining related files
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_welcome, container, false
        )
        viewModel = ViewModelProvider(this) [WelcomeViewModel::class.java]
        binding.welcomeViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if (File(context?.filesDir, "vZx45c").exists()) {
            viewModel.isRegistered = true
        }
        viewModel.eventSignup.observe(viewLifecycleOwner, {
                hasSigned -> if(hasSigned) { onSignup() }
        })
        viewModel.eventUnlock.observe(viewLifecycleOwner, {
            hasUnlocked -> if(hasUnlocked) { onUnlock() }
        })

        Log.i("WelcomeFragment", "created")
        return binding.root
    }

    // Hash any input String into a ByteArray
    private fun passToHash(pass: String): ByteArray {
        val passInBytes = pass.toByteArray(Charsets.UTF_8)
        val digester = MessageDigest.getInstance("MD5")
        return digester.digest(passInBytes)
    }

    // Navigate to the register screen when the user wants to
    private fun onSignup() {
        if (viewModel.isRegistered) {
            showWarning("Already registered. Either login with passcode " +
                    "or click options menu to reset")
        } else {
            val action = WelcomeFragmentDirections.actionWelcomeFragmentToRegisterFragment()
            findNavController(this).navigate(action)
        }
        viewModel.onSignupComplete()
    }

    // Navigate to the home screen once the app has been unlocked
    private fun onUnlock() {
        val pass = viewModel.passwordString.value
        Log.i("WelcomeFragment", "Inputted password: $pass")
        if (pass.isNullOrEmpty()) {
            showWarning("No passcode entered")
        } else if (!viewModel.isRegistered) {
            showWarning("You must first set a passcode")
        } else if ((activity as MainActivity).rvr.open(passToHash(pass))) {
            // open reservoir
            val action = WelcomeFragmentDirections.actionWelcomeFragmentToHomeFragment()
            findNavController(this).navigate(action)
        } else showWarning("Invalid passcode")
        viewModel.onUnlockComplete()
    }

    // Display an error message if login cannot be completed
    private fun showWarning(message: String) {
        Toast.makeText(requireActivity().applicationContext, message, Toast.LENGTH_LONG).show()
    }

}