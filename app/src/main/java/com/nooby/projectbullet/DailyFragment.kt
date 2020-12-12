package com.nooby.projectbullet

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.nooby.projectbullet.database.BulletDatabase
import com.nooby.projectbullet.database.DayDao
import com.nooby.projectbullet.databinding.FragmentDailyBinding
import java.util.*

class DailyFragment : Fragment() {

    private val mainData: MainData = MainData(title="Daily Entries")
    private lateinit var binding: FragmentDailyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily, container, false)

        //Gets the application context and a instance of the dao from the database
        val application = requireNotNull(this.activity).application
        val dataSource = BulletDatabase.getInstance(application).dayDao

        //Creates the factory for view models with the dao and application context then makes a new view model
        val viewModelFactory = BulletViewModelFactory(dataSource, application)
        val bulletViewModel = ViewModelProviders.of(this, viewModelFactory).get(BulletViewModel::class.java)

        binding.bulletViewModel = bulletViewModel
        binding.setLifecycleOwner(this)

        Log.i("DailyFragment", "DailyFragment created")

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("DailyFragment", "DailyFragment destroyed")
    }
}