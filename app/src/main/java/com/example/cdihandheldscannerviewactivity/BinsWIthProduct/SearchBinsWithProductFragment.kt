package com.example.cdihandheldscannerviewactivity.BinsWIthProduct

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.example.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.example.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.example.cdihandheldscannerviewactivity.databinding.FragmentSearchForBinsWithProductBinding


class SearchBinsWithProductFragment : Fragment() {

    private lateinit var itemNumberEditText: EditText
    private lateinit var searcItemInBinButton: Button
    private lateinit var binding: FragmentSearchForBinsWithProductBinding
    private val viewModel: BinsWithProductViewModel by activityViewModels()
    private lateinit var progressDialog: Dialog


    private var hasSearchButtonBeenPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_for_bins_with_product, container, false)

        // Gets the company id from the Shared Preferences
        val companyID:String = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)

        // Gets the company id from the Shared Preferences
        val warehouseNumber: Int = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
        viewModel.setWarehouseNumberFromSharedPref(warehouseNumber)


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
        itemNumberEditText.text.clear()
    }



    // Handle onPause lifecycle event
    override fun onPause() {
        super.onPause()
    }

    private fun searchForItem(){
            viewModel.getItemDetailsForBinSearchFromBackend(itemNumberEditText.text.toString())
            progressDialog.show()
            hasSearchButtonBeenPressed = true
    }
    private fun initUIElements(){
        itemNumberEditText = binding.itemNumberEditText
        itemNumberEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                // Handle the Enter key press here
                searchForItem()
                true
            } else {
                false
            }
        }
        searcItemInBinButton = binding.searchBinButton
        searcItemInBinButton.setOnClickListener{
           searchForItem()
        }
        progressDialog = Dialog(requireContext()).apply{
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
        }

        itemNumberEditText.requestFocus()
    }



    private fun initObservers(){
        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner){wasAPICallSuccessful ->
            if(!wasAPICallSuccessful){
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
                hasSearchButtonBeenPressed = false
            }
        }


        viewModel.itemDetails.observe(viewLifecycleOwner){ newItemDetails ->
            Log.i("itemDetails observer", "I have gotten new ItemDetails -> ${newItemDetails.toString()}")
            var itemNumber: String? = null
            if (newItemDetails.isNotEmpty())
                itemNumber = newItemDetails[0]?.itemNumber
            if (itemNumber != null && viewModel.isBarCodeValid.value!! && hasSearchButtonBeenPressed) {
                viewModel.getBinsThatHaveProductFromBackend(itemNumber)
            }else
                hasSearchButtonBeenPressed = false
        }



        viewModel.isBarCodeValid.observe(viewLifecycleOwner) {isBarcodeValid ->
            if (!isBarcodeValid && hasSearchButtonBeenPressed){
                Log.i("isBarCodeValid.observe", "${viewModel.barcodeErrorMessage.value} - ${viewModel.isBarCodeValid.value}")
                progressDialog.dismiss()
                AlerterUtils.startErrorAlerter(requireActivity(),viewModel.barcodeErrorMessage.value!!)
                hasSearchButtonBeenPressed = false
            }
        }

        viewModel.listOfBinsThatHaveProduct.observe(viewLifecycleOwner){ newListOfBins ->
            Log.i("listOfBinsThatHaveProduct.observe", "I have new bins -> ${newListOfBins.toString()}")
            progressDialog.dismiss()
            if(viewModel.isBarCodeValid.value == true && hasSearchButtonBeenPressed){
                hasSearchButtonBeenPressed = false
                val bundle = BundleUtils.getBundleToSendFragmentNameToNextFragment("SearchBinsWithProductFragment")
                findNavController().navigate(R.id.action_searchBinsWithProductFragment_to_binsThatHaveProductFragment, bundle)
            }
        }
    }






}