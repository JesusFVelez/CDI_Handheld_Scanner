package com.scannerapp.cdihandheldscannerviewactivity.EditItem.EditBarcode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.scannerapp.cdihandheldscannerviewactivity.HomeScreen.HomeScreenViewModel
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemData
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemInfo
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.scannerapp.cdihandheldscannerviewactivity.databinding.EditItemEditBarcodeFragmentBinding

class EditBarcodeFragment : Fragment() {

    private lateinit var binding: EditItemEditBarcodeFragmentBinding
    private lateinit var itemNumberTextView: TextView
    private lateinit var itemNameTextView: TextView
    private lateinit var additionalBarcodeRecyclerView: RecyclerView
    private lateinit var mainBarcodeTextView: TextView
    private lateinit var viewModel: EditBarcodeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.edit_item_edit_barcode_fragment, container, false)


        val args: EditBarcodeFragmentArgs by navArgs()
        val itemInfo = args.itemInfo
        initViewModel(itemInfo)
        initUIElements()
        initObservers()

        return binding.root
    }


    private fun initObservers(){
        viewModel.mainBarcode.observe(viewLifecycleOwner) { mainBarcode ->
            if(mainBarcode!!.isNotEmpty())
                mainBarcodeTextView.text = mainBarcode

        }
    }

    private fun populateUIWithItemInfo(){
        itemNumberTextView.text = viewModel.itemInfo.value?.itemNumber
        itemNameTextView.text = viewModel.itemInfo.value?.itemDescription
    }


    private fun initViewModel(itemInfo: ItemData?){

        // Create the ViewModelFactory with optional parameters
        val factory = EditBarcodeViewModelFactory(itemInfo)

        // Setting the view model
        viewModel = ViewModelProvider(this, factory)[EditBarcodeViewModel::class.java]

        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)


    }

    private fun initUIElements(){
        itemNumberTextView = binding.itemNumberTextView
        itemNameTextView = binding.itemNameTextView
        additionalBarcodeRecyclerView = binding.additionalBarcodeRecyclerView
        mainBarcodeTextView = binding.mainBarcodeLayout.findViewById(R.id.barcodeTextView)

        populateUIWithItemInfo()
    }



}