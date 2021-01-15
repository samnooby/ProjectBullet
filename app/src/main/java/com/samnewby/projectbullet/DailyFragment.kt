package com.samnewby.projectbullet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.samnewby.projectbullet.daily.DailyViewModel
import com.samnewby.projectbullet.database.BulletsDatabase
import com.samnewby.projectbullet.databinding.FragmentDailyBinding

class DailyFragment : Fragment() {

    private lateinit var binding: FragmentDailyBinding
    private lateinit var dailyViewModel: DailyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_daily, container, false)

        //Creates the view model
        val application = requireNotNull(activity).application
        val bulletDao = BulletsDatabase.getDatabase(requireContext()).bulletDao
        val dayDao = BulletsDatabase.getDatabase(requireContext()).dayDao
        val viewModelFactory =
            BulletViewModelFactory(application, bulletAccess = bulletDao, dayAccess = dayDao)
        dailyViewModel = ViewModelProviders.of(this, viewModelFactory).get(DailyViewModel::class.java)
        binding.lifecycleOwner = this
        binding.dailyViewModel = dailyViewModel

        return binding.root
    }
}