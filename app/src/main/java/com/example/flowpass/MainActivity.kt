package com.example.flowpass

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.flowpass.databinding.ActivityMainBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

/**
  * A class that manages macro UI features of the whole app, such as the navigation bar and
  * main UI binding
  */
class MainActivity : AppCompatActivity() {

    // Stores all persistent security information of the app
    lateinit var rvr: Reservoir

    // Stores all code about the app's menu
    lateinit var menu: MenuController

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rvr = Reservoir(applicationContext.filesDir)
        menu = MenuController(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        menu.isEntryMarkedDeleted.value = false
    }

    // Connect the navigation menu to the other files
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Configure the options menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handle action bar item clicks here. Codes logic for the options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_help -> { menu.showHelp(); true }
            R.id.menu_reset -> { menu.showReset(); true }
            R.id.menu_about -> { menu.showAbout(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

}