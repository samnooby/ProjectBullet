package com.nooby.projectbullet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.nooby.projectbullet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val mainData: MainData = MainData(title="ProjectBullet")
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        drawerLayout = binding.drawerLayout

        //Adds the main data to the app
        binding.myApp = mainData

        //Sets listeners
        binding.btnMenu.setOnClickListener{ view: View ->
            this.findNavController(R.id.host_fragment).navigate(R.id.action_dailyFragment_to_aboutFragment2)
        }
        Log.i("MainActivity", "MainActivity created")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("MainActivity", "MainActivity destroyed")
    }
}