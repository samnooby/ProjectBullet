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
import com.nooby.projectbullet.databinding.FragmentDailyBinding
import java.util.*

class DailyFragment : Fragment() {

    private val mainData: MainData = MainData(title="Daily Entries", date = "")
    private lateinit var viewModel: BulletViewModel
    private lateinit var binding: FragmentDailyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily, container, false)
        viewModel = ViewModelProviders.of(this).get(BulletViewModel::class.java)

        mainData.date = viewModel.currentDay.date.toString().dropLast(17)

        //Adds data and styling to the fragment
        binding.myApp = mainData
        binding.txtDate.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.txtDailyTitle.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        Log.i("DailyFragment", "DailyFragment created")

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("DailyFragment", "DailyFragment destroyed")
    }
}