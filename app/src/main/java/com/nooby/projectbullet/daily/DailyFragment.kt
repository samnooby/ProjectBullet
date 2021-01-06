package com.nooby.projectbullet.daily

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nooby.projectbullet.*
import com.nooby.projectbullet.bullet.*
import com.nooby.projectbullet.database.Bullet
import com.nooby.projectbullet.database.BulletDatabase
import com.nooby.projectbullet.databinding.FragmentDailyBinding
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

class DailyFragment : Fragment(), BulletEditMenu.EditListener {

    private val mainData: MainData = MainData(title = "Daily Entries")
    private lateinit var binding: FragmentDailyBinding
    private lateinit var dailyPagerCallback: DailyPagerCallback
    private var isStartUp = true


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
        val dailyViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(DailyViewModel::class.java)

//        val adapter = BulletAdapter(BulletListener { bullet ->
//            setupEditPopup(bullet).show(parentFragmentManager, "Edit")
//        })
//        //Tells the adapter to observe the data in view model
//        dailyViewModel.bullets.observe(viewLifecycleOwner, Observer {
//            it.let {
//                Log.i("DailyViewModel", "Changed bullets list")
//                adapter.bullets = it
//            }
//        })

        //Binds the data to the application
//        binding.bulletList.adapter = adapter

        //Create the adapter for the pageviewer and get it to constantly observe the dailyViewModelWeek
        val viewPageAdapter = DailyPageAdapter(DailyPageListener { bullet ->
            setupEditPopup(bullet).show(parentFragmentManager, "Edit")
        })
        dailyViewModel.currentWeek.observe(viewLifecycleOwner, Observer {
            it.let {
                Log.i("DailyFragment", "Updated viewpager week $it")
//                for (day in it) {
////                    Log.i("DailyFragment", "${day.name} Bullets ${day.bullets.value}")
//                }
                viewPageAdapter.days = it
            }
        })
        //Sets the callback for the viewpager to allow it to be infinite scrolling
        dailyPagerCallback = DailyPagerCallback {
            binding.viewPager.post {
                Log.i("DailyFragment", "Called page change function")
                if (isStartUp) {
                    Log.i("DailyFragment", "First time")
                    isStartUp = false
                    binding.viewPager.setCurrentItem(PAGE_LIMIT / 2, false)
                } else {
                    if (it == PAGE_LIMIT) {
                        Log.i("DailyFragment", "Going back one week")
                        dailyViewModel.getWeek(dailyViewModel.currentWeekNumber - 1)
                    } else if (it == 1) {
                        Log.i("DailyFragment", "Going forward one week")
                        dailyViewModel.getWeek(dailyViewModel.currentWeekNumber + 1)
                    }
                    binding.viewPager.setCurrentItem(it, false)
                }
            }
        }

        binding.viewPager.adapter = viewPageAdapter
        Log.i("DailyFragment", "Got day ${Calendar.getInstance().get(Calendar.DAY_OF_WEEK)}")
        binding.viewPager.registerOnPageChangeCallback(dailyPagerCallback)
        //Binds the data to the layout
        binding.myApp = mainData
        binding.dailyViewModel = dailyViewModel
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)

        //creates a new bullet and resets the textboxes
        fun addNewBullet() {
            if (binding.txtAddBullet.text.isNotEmpty()) {
                binding.dailyViewModel?.createBullet(
                    binding.txtAddBullet.text.toString(),
                    binding.viewPager.currentItem
                )
            }
            binding.txtAddBullet.setText("")
            binding.txtAddBullet.clearFocus()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        //Set up event listeners
        binding.btnBulletType.setOnClickListener {
            setupTypePopup().showAsDropDown(binding.btnBulletType)
        }
        binding.btnCreateBullet.setOnClickListener {
            addNewBullet()
        }
        binding.backDayBtn.setOnClickListener {
            binding.viewPager.setCurrentItem(binding.viewPager.currentItem - 1, true)
        }
        binding.forwardDayBtn.setOnClickListener {
            binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1, true)
        }
        binding.dailyHomeBtn.setOnClickListener {
            dailyViewModel.getWeek(newCurrentDay = LocalDate.now())
            binding.viewPager.setCurrentItem(
                PAGE_LIMIT/2,
                true
            )
        }
        binding.dailyDateBtn.setOnClickListener {
            val datePicker = DatePicker {
                dailyViewModel.getWeek(newCurrentDay = it)
                binding.viewPager.setCurrentItem(
                    PAGE_LIMIT / 2 ,
                    true
                )
            }
            datePicker.show(parentFragmentManager, "datePicker")
        }
        binding.txtAddBullet.setOnKeyListener { _, keyCode, event ->
            when {
                ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) -> {
                    addNewBullet()
                    return@setOnKeyListener true
                }
                else -> false
            }
        }

        Log.i("DailyFragment", "DailyFragment created")

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("DailyFragment", "DailyFragment destroyed")
    }

    //setupPopup creates the popup that is showed when selecting a different bullet type
    private fun setupTypePopup(): PopupWindow {
        val window = BulletTypeMenu(requireActivity())
        //Adds observer to change image when a option is selected
        window.currentBulletImg.observe(viewLifecycleOwner, Observer {
            it.let {
                binding.btnBulletType.setImageResource(it)
            }
        })
        //Adds observer to update viewModel when clicked
        window.currentBulletType.observe(viewLifecycleOwner, Observer {
            it.let {
                binding.dailyViewModel?.newBulletType = it
            }
        })

        Log.i("DailyFragment", "Popup window created")
        return window
    }

    //setupEditPopup creates the popup dialog to edit the selected bullet
    private fun setupEditPopup(bullet: Bullet): DialogFragment {
        val editFragment = BulletEditMenu(bullet, viewLifecycleOwner)
        editFragment.setTargetFragment(this, 0)
        return editFragment
    }

    //Updates the bullet when the popup is closed
    override fun onDialogClose(dialog: BulletEditMenu) {
        if (dialog.deleteBullet) {
            Log.i("DailyFragment", "Deleting bullet")
            binding.dailyViewModel?.deleteBullet(dialog.bullet, binding.viewPager.currentItem)
        } else {
            Log.i("DailyFragment", "Updating bullet")
            val updateBullet = dialog.bullet
            updateBullet.message = dialog.bulletText
            updateBullet.bulletType = dialog.bulletType
            updateBullet.bulletDate = dialog.bulletDate

            binding.dailyViewModel?.changeBullet(updateBullet, binding.viewPager.currentItem)
        }

    }
}