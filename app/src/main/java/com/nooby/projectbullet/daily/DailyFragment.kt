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

class DailyFragment : Fragment(), BulletEditMenu.EditListener {

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

        val adapter = BulletAdapter(BulletListener { bullet ->
            setupEditPopup(bullet).show(parentFragmentManager, "Edit")
        })
        //Tells the adapter to observe the data in view model
        dailyViewModel.bullets.observe(viewLifecycleOwner, Observer {
            it.let {
                Log.i("DailyViewModel", "Changed bullets list")
                adapter.bullets = it
            }
        })

        //Binds the data to the application
        binding.bulletList.adapter = adapter
        binding.myApp = mainData
        binding.dailyViewModel = dailyViewModel
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)

        //creates a new bullet and resets the textboxes
        fun addNewBullet() {
            if (binding.txtAddBullet.text.isNotEmpty()) {
                binding.dailyViewModel?.createBullet(binding.txtAddBullet.text.toString())
            }
            binding.txtAddBullet.setText("")
            binding.txtAddBullet.clearFocus()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        //Set up event listeners
        binding.btnBulletType.setOnClickListener{
            setupTypePopup().showAsDropDown(binding.btnBulletType)
        }
        binding.btnCreateBullet.setOnClickListener {
            addNewBullet()
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

    private fun setupEditPopup(bullet: Bullet): DialogFragment {
        val editFragment = BulletEditMenu(bullet, viewLifecycleOwner)
        editFragment.setTargetFragment(this, 0)
        return editFragment
    }

    //Updates the bullet when the popup is closed
    override fun onDialogClose(dialog: BulletEditMenu) {
        if (dialog.deleteBullet) {
            binding.dailyViewModel?.deleteBullet(dialog.bullet)
        } else {
            val updateBullet = dialog.bullet
            updateBullet.message = dialog.bulletText
            updateBullet.BulletType = dialog.bulletType
            updateBullet.bulletDate = dialog.bulletDate

            binding.dailyViewModel?.changeBullet(updateBullet)
        }

    }
}