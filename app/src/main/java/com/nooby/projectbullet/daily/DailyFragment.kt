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
import com.nooby.projectbullet.database.BulletType
import com.nooby.projectbullet.database.Day
import com.nooby.projectbullet.databinding.FragmentDailyBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

class DailyFragment : Fragment(), BulletEditMenu.EditListener, BulletNoteEditMenu.EditNoteListener {

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

        //Binds viewmodel and data to the activity
        binding.myApp = mainData
        binding.dailyViewModel = dailyViewModel
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)

        //Sets up the ViewPager which shows the different days
        val viewPageAdapter = setupDayPager(dailyViewModel)
        binding.viewPager.adapter = viewPageAdapter
        binding.viewPager.registerOnPageChangeCallback(dailyPagerCallback)

        setupEventListeners(dailyViewModel)

        Log.i("DailyFragment", "DailyFragment created")

        return binding.root
    }

    //setupEventListeners creates all clicklisteners on the fragment
    private fun setupEventListeners(dailyViewModel: DailyViewModel) {
        //Changing the bullet type
        binding.btnBulletType.setOnClickListener {
            setupTypePopup().showAsDropDown(binding.btnBulletType)
        }
        //Adding Plus button
        binding.btnCreateBullet.setOnClickListener {
            addNewBullet()
        }
        //Day navigation buttons
        binding.backDayBtn.setOnClickListener {
            binding.viewPager.setCurrentItem(binding.viewPager.currentItem - 1, true)
        }
        binding.forwardDayBtn.setOnClickListener {
            binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1, true)
        }
        //Top of page date changing buttons
        binding.dailyHomeBtn.setOnClickListener {
            dailyViewModel.getWeek(newCurrentDay = LocalDate.now())
            binding.viewPager.setCurrentItem(
                PAGE_LIMIT / 2,
                true
            )
        }
        binding.dailyDateBtn.setOnClickListener {
            val datePicker = DatePicker {
                dailyViewModel.getWeek(newCurrentDay = it)
                binding.viewPager.setCurrentItem(
                    PAGE_LIMIT / 2,
                    true
                )
            }
            datePicker.show(parentFragmentManager, "datePicker")
        }
        //Key listener for enter key
        binding.txtAddBullet.setOnKeyListener { _, keyCode, event ->
            when {
                ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) -> {
                    addNewBullet()
                    return@setOnKeyListener true
                }
                else -> false
            }
        }
    }

    //addNewBullet gets the viewmodel to create a new bullet and resets the page
    private fun addNewBullet() {
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

    //setupDayPager creates the listeners and observers for the day viewpager
    private fun setupDayPager(dailyViewModel: DailyViewModel): DailyPageAdapter {
        //bulletClickListener controls what happens when a bullet is clicked
        val bulletClickListener: (bullet: Bullet) -> Unit =
            { setupEditPopup(it).show(parentFragmentManager, "Edit") }

        //taskListener controls what happens when the task icon is clicked
        val taskListener: (bullet: Bullet) -> Unit = {
            if (it.bulletType == BulletType.INCOMPLETETASK || it.bulletType == BulletType.EVENT) {
                it.bulletType = BulletType.COMPLETETASK
                binding.dailyViewModel?.changeBullet(it, binding.viewPager.currentItem)
            }
        }

        //noteListener listens for a new note and creates it
        val noteListener: (bullet: Bullet, note: String) -> Unit = { bullet, note ->
            Log.i("DailyFragment", "Adding note $note")
            bullet.bulletNotes = bullet.bulletNotes.plus(note)
            binding.dailyViewModel?.changeBullet(bullet, binding.viewPager.currentItem)
        }

        //editNoteListener creates the edit note popup
        val editNoteListener: (bullet: Bullet, notePosition: Int) -> Unit =
            { bullet: Bullet, notePosition: Int ->
                val editFragment =
                    BulletNoteEditMenu(bullet, notePosition, bullet.bulletNotes[notePosition])
                editFragment.setTargetFragment(this, 0)
                editFragment.show(parentFragmentManager, "EditNote")
            }

        //dragListener edits the list when a bullet is dragged
        val dragListener: (bulletOrder: List<Long>, day: Day) -> Unit = { list: List<Long>, day: Day ->
            Log.i("DailyFragment", "Got changes $list for day $day")
            if (list.isNotEmpty()) {
                dailyViewModel.addBulletOrder(list, day)
            }
        }

        val viewPageAdapter =
            DailyPageAdapter(
                DailyPageListener(
                    bulletClickListener,
                    taskListener,
                    noteListener,
                    editNoteListener,
                    dragListener
                )
            )

        //Constantly watches the current week and if it updates update the adapter
        dailyViewModel.currentWeek.observe(viewLifecycleOwner, Observer {
            it.let {
                Log.i("DailyFragment", "Updated viewpager week $it")
                viewPageAdapter.days = it
            }
        })

        //Watches every page turn and when we reach the first or last page refresh and cycle
        dailyPagerCallback = DailyPagerCallback {
            binding.viewPager.post {
                Log.i("DailyFragment", "Called page change function")
                if (isStartUp) {
                    isStartUp = false
                    binding.viewPager.setCurrentItem(PAGE_LIMIT / 2, false)
                } else {
                    if (it == PAGE_LIMIT) {
                        dailyViewModel.getWeek(dailyViewModel.currentWeekNumber - 1)
                    } else if (it == 1) {
                        dailyViewModel.getWeek(dailyViewModel.currentWeekNumber + 1)
                    }
                    binding.viewPager.setCurrentItem(it, false)
                }
            }
        }

        return viewPageAdapter
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

    //onDialogClose updates a bullet when its edit dialog is closed
    override fun onDialogClose(dialog: BulletEditMenu) {
        //Checks what type of edit
        if (dialog.deleteBullet) {
            Log.i("DailyFragment", "Deleting bullet")
            binding.dailyViewModel?.deleteBullet(dialog.bullet, binding.viewPager.currentItem)
        } else {
            Log.i("DailyFragment", "Updating bullet")
            val updateBullet = dialog.bullet
            updateBullet.message = dialog.bulletText
            updateBullet.bulletType = dialog.bulletType
            val isDateChange = updateBullet.bulletDate != dialog.bulletDate

            updateBullet.bulletDate = dialog.bulletDate

            binding.dailyViewModel?.changeBullet(updateBullet, binding.viewPager.currentItem)
            if (isDateChange) {
                binding.dailyViewModel?.getWeek(newCurrentDayNumber = binding.viewPager.currentItem)
                binding.viewPager.setCurrentItem(
                    PAGE_LIMIT / 2,
                    false
                )
            }
            }
    }

    override fun onDialogClose(dialog: BulletNoteEditMenu) {
        val bullet = dialog.bullet
        val tmpList = bullet.bulletNotes.toMutableList()
        //If deleting the note remove it from the bullets notes
        if (dialog.isDelete) {
            Log.i("DailyFragment", "Deleting note")
            tmpList.removeAt(dialog.notePosition)
            bullet.bulletNotes = tmpList
        } else {
            //Change value of note
            Log.i("DailyFragment", "changing new text ${dialog.editText} into ${dialog.notePosition} in $tmpList")
            tmpList[dialog.notePosition] = dialog.editText
            bullet.bulletNotes = tmpList
        }

        //Updates the bullet in the database and refreshes the list
        binding.dailyViewModel?.changeBullet(bullet, binding.viewPager.currentItem)

    }
}