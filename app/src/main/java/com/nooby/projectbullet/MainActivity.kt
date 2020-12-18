package com.nooby.projectbullet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.nooby.projectbullet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val mainData: MainData = MainData(title="ProjectBullet")
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //Adds the main data to the app
        binding.myApp = mainData

        Log.i("MainActivity", "MainActivity created")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("MainActivity", "MainActivity destroyed")
    }
}