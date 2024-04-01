package com.comdist.cdihandheldscannerviewactivity.MovingProductsBetweenBins

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.WarehouseInfo
import com.comdist.cdihandheldscannerviewactivity.databinding.FragmentBinMovementBinding
import org.w3c.dom.Text


    class BinMovementFragment : Fragment() {

    private lateinit var binding: FragmentBinMovementBinding

    private lateinit var itemNumberEditText: EditText
    private lateinit var itemAmountEditText: EditText
    private lateinit var fromBinNumber:EditText
    private lateinit var toBinNumber:EditText
    private lateinit var addButton:Button
    private lateinit var itemsBeingMovedRecyclerView:RecyclerView
    private lateinit var continueButton: Button
    private lateinit var clearButton: Button

    private lateinit var adapter: BinMovementAdapter

    private val viewModel: MovingProductsBetweenBinsViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bin_movement, container, false)

        initUIElements()

        return binding.root
    }


    private fun initUIElements(){
        itemNumberEditText = binding.itemNumberSpinner
        itemAmountEditText = binding.itemAmountEditText
        fromBinNumber = binding.fromBinNumber
        toBinNumber = binding.toBinNumber
        addButton = binding.addButton
        continueButton = binding.continueButton
        clearButton = binding.clearButton


        adapter = BinMovementAdapter(View.OnClickListener {
            // TODO - Remove the item from the adapter data when the trashcan button is clicked
        })
        itemsBeingMovedRecyclerView = binding.itemsBeingMovedRecyclerView
        itemsBeingMovedRecyclerView.adapter = adapter


    }




}