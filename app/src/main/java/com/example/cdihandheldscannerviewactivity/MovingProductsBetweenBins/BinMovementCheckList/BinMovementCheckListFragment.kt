package com.example.cdihandheldscannerviewactivity.MovingProductsBetweenBins.BinMovementCheckList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.databinding.FragmentBinMovementCheckListBinding


class BinMovementCheckListFragment : Fragment() {

    private lateinit var binding: FragmentBinMovementCheckListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bin_movement_check_list,container, false)

        return binding.root
    }

}