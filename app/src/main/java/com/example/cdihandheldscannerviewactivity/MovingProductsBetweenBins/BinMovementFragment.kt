package com.example.cdihandheldscannerviewactivity.MovingProductsBetweenBins

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.Network.WarehouseInfo
import com.example.cdihandheldscannerviewactivity.databinding.FragmentBinMovementBinding
import org.w3c.dom.Text


    class BinMovementFragment : Fragment() {

    private lateinit var binding: FragmentBinMovementBinding

    private lateinit var warehouseSpinner: Spinner
    private lateinit var itemNumberEditText: EditText
    private lateinit var itemAmountEditText: EditText
    private lateinit var fromBinNumber:EditText
    private lateinit var toBinNumber:EditText
    private lateinit var addButton:Button
    private lateinit var itemsBeingMovedRecyclerView:RecyclerView
    private lateinit var continueButton: Button

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
        initObservers()
        viewModel.getWarehousesFromBackendForSpinner()




        return binding.root
    }

    private fun initObservers(){
        viewModel.listOfWarehouses.observe(viewLifecycleOwner){ newListOfWarehouses ->
            fillSpinnerWithWarehouses(newListOfWarehouses)
        }
    }


    // Populate Spinner with warehouse data
    private fun fillSpinnerWithWarehouses( newWarehouseList : List<WarehouseInfo>){
        val warehouses = mutableListOf<String>()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, warehouses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        warehouseSpinner.adapter = adapter
        for (aWarehouse in viewModel.listOfWarehouses.value!!) {
            warehouses.add(aWarehouse.warehouseName)
        }
        adapter.notifyDataSetChanged()
    }

    private fun initUIElements(){
        warehouseSpinner = binding.warehouseSpinner
        itemNumberEditText = binding.itemNumberEditText
        itemAmountEditText = binding.itemAmountEditText
        fromBinNumber = binding.fromBinNumber
        toBinNumber = binding.toBinNumber
        addButton = binding.addButton
        itemsBeingMovedRecyclerView = binding.itemsBeingMovedRecyclerView
        continueButton = binding.continueButton

    }




}