package com.example.flowpass.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.flowpass.MainActivity
import com.example.flowpass.R
import com.example.flowpass.databinding.FragmentRegisterBinding
import java.security.MessageDigest
import java.util.regex.Pattern

/**
 * A simple [Fragment] that defines the user registration screen. Where the user must register for
 * FlowPass. Controls password validation and navigation to the main fragment, which unlocks use of
 * th app
 */
class RegisterFragment : Fragment() {

    // Initialising binding and model
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    // Mostly boilerplate code for combining related files
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_register, container, false
        )
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        binding.registerViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.eventRegister.observe(viewLifecycleOwner, {
            hasRegistered -> if(hasRegistered) { onRegister() }
        })

        Log.i("RegisterFragment", "created")
        return binding.root
    }

    // Hash any input String into a ByteArray
    private fun passToHash(pass: String): ByteArray {
        val passInBytes = pass.toByteArray(Charsets.UTF_8)
        val digester = MessageDigest.getInstance("MD5")
        return digester.digest(passInBytes)
    }

    // Generates a random string that has 26873856 possible combinations, for
    // use in encryption. Could be made user generated in the future
    private fun generateRandomPurity(): String {
        val metals = arrayOf(
            "Calcium", "Magnesium", "Lithium", "Zinc", "Strontium", "Iron"
        )
        val getNum = { (1..12).shuffled().first().toString() }
        val getMetalInt = { (0..5).shuffled().first() }
        val resultBuild = StringBuilder()
        for (i in 0..4) {
            resultBuild.append(metals[getMetalInt()])
            resultBuild.append(getNum())
        }
        return resultBuild.toString()
    }

    // Checks whether the user inputted password matches entry criteria or not
    private fun isValid(password: String): Boolean {
        val pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,30}\$")
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }

    // Display an error message if registration cannot be completed
    private fun showWarning(message: String) {
        Toast.makeText(requireActivity().applicationContext, message, Toast.LENGTH_LONG).show()
    }

    // Navigate to the home screen once the app has been unlocked
    private fun onRegister() {
        val pass = viewModel.passwordStrings.value!![0]
        val passCheck = viewModel.passwordStrings.value!![1]
        if (pass.isEmpty()) {
            showWarning("No passcode entered")
        } else if (pass != passCheck) {
            showWarning("Passwords are not equal")
        } else if (!isValid(pass)) {
            showWarning("Password failed validation criteria")
        } else {
            // Create the reservoir; setting security keys and seed
            (activity as MainActivity).rvr.create(passToHash(pass), generateRandomPurity())
            val action = RegisterFragmentDirections.actionRegisterFragmentToWelcomeFragment()
            findNavController(this).navigate(action)
        }
        viewModel.onRegisterComplete()
    }

}