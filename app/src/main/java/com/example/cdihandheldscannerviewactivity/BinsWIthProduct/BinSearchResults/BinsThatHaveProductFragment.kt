package com.example.cdihandheldscannerviewactivity.BinsWIthProduct.BinSearchResults

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.cdihandheldscannerviewactivity.BinsWIthProduct.BinsWithProductViewModel
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.databinding.FragmentBinsThatHaveProductBinding

class BinsThatHaveProductFragment : Fragment() {

    private val viewModel: BinsWithProductViewModel by activityViewModels()
    private lateinit var itemNumberTextView: TextView
    private lateinit var barcodeTextView: TextView
    private lateinit var pickingBinTextView: TextView
    private lateinit var quantityOnHandTextView: TextView
    private lateinit var itemNameTextView: TextView
    private lateinit var inventoryTypeTextView: TextView
    private lateinit var vendorItemNumberTextView: TextView
    private lateinit var binding: FragmentBinsThatHaveProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bins_that_have_product, container, false)
        initUIElements()
        fillItemDetailsWithViewModelInfo()
        Log.i("BinsThatHaveProductFragment", "I am here")
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initUIElements(){
        itemNumberTextView = binding.itemNumber
        barcodeTextView = binding.barCode
        pickingBinTextView = binding.pickingBinLocation
        quantityOnHandTextView = binding.QtyOnHandText
        itemNameTextView = binding.itemName
        inventoryTypeTextView = binding.inventoryType
        vendorItemNumberTextView = binding.VendorItemNumberText
    }

    private fun fillItemDetailsWithViewModelInfo(){
        itemNumberTextView.text = viewModel.itemDetails.value!![0].itemNumber
        barcodeTextView.text = viewModel.itemDetails.value!![0].barCode
        pickingBinTextView.text = viewModel.itemDetails.value!![0].binForPicking
        quantityOnHandTextView.text = viewModel.itemDetails.value!![0].totalQuantityOnHand.toInt().toString()
        itemNameTextView.text = viewModel.itemDetails.value!![0].itemName
        inventoryTypeTextView.text = "Inv. Type: ${viewModel.itemDetails.value!![0].inventoryType} | ${viewModel.itemDetails.value!![0].unitOfMeasurement}"
        vendorItemNumberTextView.text = "Vendor Item Num: ${viewModel.itemDetails.value!![0].vendorItemNumber}"
    }
}