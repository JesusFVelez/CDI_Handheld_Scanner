package com.example.cdihandheldscannerviewactivity.MovingProductsBetweenBins.BinMovementCheckList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.cdihandheldscannerviewactivity.MovingProductsBetweenBins.MovingProductsBetweenBinsViewModel
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.databinding.FragmentBinMovementCheckListBinding


class BinMovementCheckListFragment : Fragment() {

    private lateinit var binding: FragmentBinMovementCheckListBinding
    private lateinit var doneButton:Button

    private lateinit var itemsBeingMovedRecyclerView:RecyclerView


    private val viewModel: MovingProductsBetweenBinsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bin_movement_check_list,container, false)
        initUIElements()

        return binding.root
    }
    private fun initUIElements(){
        doneButton = binding.doneButton
        itemsBeingMovedRecyclerView = binding.itemsBeingMovedRecyclerView

    }
}
