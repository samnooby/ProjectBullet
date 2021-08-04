package ca.newbys.bullet.daily

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.newbys.bullet.ViewModelFactory
import ca.newbys.bullet.database.BulletDatabase
import ca.newbys.bullet.databinding.DailyFragmentBinding

class Daily : Fragment() {

    companion object {
        fun newInstance() = Daily()
    }

    private lateinit var viewModel: DailyViewModel
    private lateinit var binding: DailyFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflates the layout of the fragment using the binding
        binding = DailyFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //Gets access to the database and creates a view model with the ability to access the database
        val bulletDao = BulletDatabase.getInstance(requireNotNull(this.activity).application).bulletDao
        viewModel = ViewModelProvider(this, ViewModelFactory(bulletDao)).get(DailyViewModel::class.java)


    }
}