package com.example.cdihandheldscannerviewactivity.BinsWIthProduct

import android.annotation.SuppressLint
import android.app.Dialog
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.cdihandheldscannerviewactivity.Utils.Network.WarehouseInfo
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.example.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.example.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.example.cdihandheldscannerviewactivity.databinding.FragmentSearchForBinsWithProductBinding


class SearchBinsWithProductFragment : Fragment() {

    private lateinit var warehouseSpinner: Spinner
    private lateinit var itemNumberEditText: EditText
    private lateinit var searcItemInBinButton: Button
    private lateinit var binding: FragmentSearchForBinsWithProductBinding
    private val viewModel: BinsWithProductViewModel by activityViewModels()
    private lateinit var progressDialog: Dialog


    private var hasPageJustStarted: Boolean = false
    private var wasSearchStarted = false

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

        initUIElements()
        initObservers()
        initSpinner()


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

    private fun searchForItem(){
        if (warehouseSpinner.selectedItem != null) {
            viewModel.getItemDetailsForBinSearchFromBackend(warehouseSpinner.selectedItem.toString(), itemNumberEditText.text.toString())
            progressDialog.show()
            wasSearchStarted = true
        }else  // This assumes that the fact that there are no warehouse selected, implies that the internet is not connected because if there was internet, there would be at least a single warehouse chosen
            AlerterUtils.startInternetLostAlert(requireActivity())
    }
    private fun initUIElements(){
        warehouseSpinner = binding.warehouseSpinner
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
            show()
        }

        itemNumberEditText.requestFocus()
    }

    // Initialize Spinner behavior
    @SuppressLint("ClickableViewAccessibility")
    private fun initSpinner(){

        val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            // Reset background when spinner closes
            warehouseSpinner.setBackgroundResource(R.drawable.drop_down_background)
            viewModel.setIsSpinnerArrowUp(false)
        }

        // set what happens whenever the Spinner is clicked
        warehouseSpinner.setOnTouchListener{view, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    warehouseSpinner.setBackgroundResource(R.drawable.drop_down_arrow_up)
                    warehouseSpinner.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
                    viewModel.setIsSpinnerArrowUp(true)
                }
            }
            false
        }

        // set what happens whenever an item is clicked in the spinner
        warehouseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                warehouseSpinner.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
                viewModel.setIsSpinnerArrowUp(false)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Reset the background when the user touches outside of the Spinner
        binding.root.setOnTouchListener { _, _ ->
            if (viewModel.isSpinnerArrowUp.value!!) {
                warehouseSpinner.setBackgroundResource(R.drawable.drop_down_background)
                warehouseSpinner.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
                viewModel.setIsSpinnerArrowUp(false)
            }
            false
        }
    }

    private fun initObservers(){
        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner){wasAPICallSuccesfull ->
            if(!wasAPICallSuccesfull){
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }
        }
        viewModel.listOfWarehouses.observe(viewLifecycleOwner) {newWarehousesList ->
            progressDialog.dismiss()
            fillSpinnerWithWarehouses(newWarehousesList)
        }

        viewModel.itemDetails.observe(viewLifecycleOwner){ newItemDetails ->
            Log.i("itemDetails observer", "I have gotten new ItemDetails -> ${newItemDetails.toString()}")
            var itemNumber: String? = null
            if (newItemDetails.size != 0)
                itemNumber = newItemDetails[0]?.itemNumber
            val selectedWarehouse = warehouseSpinner.selectedItem
            if (selectedWarehouse != null && itemNumber != null && viewModel.isBarCodeValid.value!! && wasSearchStarted) {
                viewModel.getBinsThatHaveProductFromBackend(selectedWarehouse.toString(), itemNumber)
            }
        }



        viewModel.isBarCodeValid.observe(viewLifecycleOwner) {isBarcodeValid ->
            if (!isBarcodeValid && wasSearchStarted){
                Log.i("isBarCodeValid.observe", "${viewModel.barcodeErrorMessage.value} - ${viewModel.isBarCodeValid.value}")
                progressDialog.dismiss()
                AlerterUtils.startErrorAlerter(requireActivity(),viewModel.barcodeErrorMessage.value!!)
            }
        }

        viewModel.listOfBinsThatHaveProduct.observe(viewLifecycleOwner){ newListOfBins ->
            Log.i("listOfBinsThatHaveProduct.observe", "I have new bins -> ${newListOfBins.toString()}")
            progressDialog.dismiss()
            if(viewModel.isBarCodeValid.value == true && wasSearchStarted){
                val bundle = BundleUtils.getBundleToSendFragmentNameToNextFragment("SearchBinsWithProductFragment")
                findNavController().navigate(R.id.action_searchBinsWithProductFragment_to_binsThatHaveProductFragment, bundle)
            }
        }
    }

    private fun removeAllObservers(){
        viewModel.listOfWarehouses.removeObservers(viewLifecycleOwner)
        viewModel.listOfBinsThatHaveProduct.removeObservers(viewLifecycleOwner)
        viewModel.itemDetails.removeObservers(viewLifecycleOwner)
        viewModel.isBarCodeValid.removeObservers(viewLifecycleOwner)
        viewModel.wasLastAPICallSuccessful.removeObservers(viewLifecycleOwner)

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

}