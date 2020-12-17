package com.nooby.projectbullet.daily

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nooby.projectbullet.BulletAdapter
import com.nooby.projectbullet.BulletViewModelFactory
import com.nooby.projectbullet.MainData
import com.nooby.projectbullet.R
import com.nooby.projectbullet.database.BulletDatabase
import com.nooby.projectbullet.database.BulletType
import com.nooby.projectbullet.databinding.FragmentDailyBinding

class DailyFragment : Fragment() {

    private val mainData: MainData = MainData(title="Daily Entries")
    private lateinit var binding: FragmentDailyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily, container, false)

        //Gets the application and data source for the view model
        val application = requireNotNull(this.activity).application
        val dataSource = BulletDatabase.getInstance(application).bulletDatabaseDao

        //Gets a new factory and creates the daily view model
        val viewModelFactory = BulletViewModelFactory(dataSource, application)
        val dailyViewModel = ViewModelProviders.of(this, viewModelFactory).get(DailyViewModel::class.java)

        val adapter = BulletAdapter()
        //Tells the adapter to observe the data in view model
        dailyViewModel.bullets.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.bullets = it
            }
        })

        //Binds the data to the application
        binding.bulletList.adapter = adapter
        binding.myApp = mainData
        binding.dailyViewModel = dailyViewModel
        binding.lifecycleOwner = this

        //Set up event listeners
        binding.btnBulletType.setOnClickListener{
            setupPopup().showAsDropDown(binding.btnBulletType, 0, -425)
        }
        binding.btnCreateBullet.setOnClickListener {
            binding.dailyViewModel?.createBullet(binding.txtAddBullet.text.toString())
        }

        Log.i("DailyFragment", "DailyFragment created")

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("DailyFragment", "DailyFragment destroyed")
    }

    //setupPopup creates the popup that is showed when selecting a different bullet type
    private fun setupPopup(): PopupWindow {
        val window = PopupWindow(requireNotNull(this.activity))
        val view = layoutInflater.inflate(R.layout.bullet_type_menu, null)
        window.contentView = view
        window.isFocusable = true

        //Sets up button event listeners
        var currentButton = view.findViewById<ImageButton>(R.id.btn_bullet_type_note)
        currentButton.setOnClickListener {
            binding.dailyViewModel?.newBulletType = BulletType.NOTE
            binding.btnBulletType.setImageResource(R.drawable.bullet_icon_note)
            window.dismiss()
        }
        currentButton = view.findViewById<ImageButton>(R.id.btn_bullet_type_task)
        currentButton.setOnClickListener {
            binding.dailyViewModel?.newBulletType = BulletType.INCOMPLETETASK
            binding.btnBulletType.setImageResource(R.drawable.bullet_icon_task)
            window.dismiss()
        }
        currentButton = view.findViewById<ImageButton>(R.id.btn_bullet_type_complete)
        currentButton.setOnClickListener {
            binding.dailyViewModel?.newBulletType = BulletType.COMPLETETASK
            binding.btnBulletType.setImageResource(R.drawable.bullet_icon_complete)
            window.dismiss()
        }
        currentButton = view.findViewById<ImageButton>(R.id.btn_bullet_type_event)
        currentButton.setOnClickListener {
            binding.dailyViewModel?.newBulletType = BulletType.EVENT
            binding.btnBulletType.setImageResource(R.drawable.bullet_icon_event)
            window.dismiss()
        }

        Log.i("DailyFragment", "Popup window created")
        return window
    }
}