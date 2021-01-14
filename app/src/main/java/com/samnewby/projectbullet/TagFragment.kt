package com.samnewby.projectbullet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.samnewby.projectbullet.databinding.FragmentTagBinding

class TagFragment : Fragment() {

    private lateinit var binding: FragmentTagBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tag, container, false)
        return binding.root
    }

}