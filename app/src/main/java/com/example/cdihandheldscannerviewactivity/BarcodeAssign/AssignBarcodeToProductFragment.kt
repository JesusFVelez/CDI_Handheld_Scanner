package com.example.cdihandheldscannerviewactivity.BarcodeAssign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.example.cdihandheldscannerviewactivity.databinding.FragmentAssignBarcodeToProductBinding

class AssignBarcodeToProductFragment: Fragment() {

    private lateinit var itemNumberEditText: EditText
    private lateinit var itemBarcodeEditText: EditText
    private lateinit var searchProductButton: Button
    private lateinit var addButton: Button
    private lateinit var binding: FragmentAssignBarcodeToProductBinding

    private val viewModel: AssignBarcodeViewModel by activityViewModels()

    private var hasPageJustStarted: Boolean = false
    private var wasSearchStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_assign_barcode_to_product, container, false)

        initUIElements()
        initObservers()

        return binding.root
    }

    // Handle onResume lifecycle event
    override fun onResume() {
        super.onResume()
        val bundle = arguments
        val lastFragmentName : String = BundleUtils.getPastFragmentNameFromBundle(bundle)
        if(lastFragmentName == "HomeScreen"){
            bundle?.clear()
        }
        hasPageJustStarted = false
        itemNumberEditText.text.clear()
    }

    // Handle onPause lifecycle event
    override fun onPause() {
        super.onPause()

        wasSearchStarted = false
    }

    private fun initUIElements() {
        itemNumberEditText = binding.itemNumberEditText
        itemBarcodeEditText = binding.itemBarcodeEditText

        searchProductButton = binding.searchProductButton
        searchProductButton.setOnClickListener{
            searchForItem()
        }

        addButton = binding.addButton
        addButton.setOnClickListener{
            addBarcode()
        }

        itemNumberEditText.requestFocus()
    }

    private fun initObservers(){

    }

    private fun searchForItem(){

    }

    private fun addBarcode(){

    }
}
