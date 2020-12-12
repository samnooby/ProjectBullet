package com.nooby.projectbullet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.nooby.projectbullet.databinding.ActivityMainBinding
import java.time.LocalDate
import java.util.*

const val KEY_CURRENT_PAGE = "key_page"

class MainActivity : AppCompatActivity() {

    private val mainData: MainData = MainData(title="ProjectBullet")
    private lateinit var binding: ActivityMainBinding
    private var currentPage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //If there is application data get it from the saved instance state
        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getString(KEY_CURRENT_PAGE) ?: "dailyFragment"
        }

        //Adds the main data to the app
        binding.myApp = mainData
        Log.i("MainActivity", "MainActivity created")
    }

    //onSaveInstanceState saves current application data for when the application is relaunched
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CURRENT_PAGE, currentPage)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("MainActivity", "MainActivity destroyed")
    }
}