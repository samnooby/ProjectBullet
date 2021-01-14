package com.samnewby.projectbullet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.samnewby.projectbullet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //Gets the views from the binding
        drawerLayout = binding.drawerLayout
        setSupportActionBar(binding.toolbar)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //Sets up the navigation controller to the navigation drawer and prepares the action bar
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfig = AppBarConfiguration.Builder(
            setOf(
                R.id.dailyFragment,
                R.id.tagFragment,
                R.id.taskFragment
            )
        ).setOpenableLayout(drawerLayout).build()
        binding.toolbar.setupWithNavController(navController, appBarConfig)
        binding.navView.setupWithNavController(navController)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //If the item selected is linked to a fragment, navigate to it or do the default
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}