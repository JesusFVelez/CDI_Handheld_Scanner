package com.example.cdihandheldscannerviewactivity.BinsWIthProduct

import android.annotation.SuppressLint
import android.app.Dialog
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.cdihandheldscannerviewactivity.Network.WarehouseInfo
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Storage.BundleUtils
import com.example.cdihandheldscannerviewactivity.Storage.SharedPreferencesUtils
import com.example.cdihandheldscannerviewactivity.databinding.FragmentSearchForBinsWithProductBinding


class SearchBinsWithProductFragment : Fragment() {

    private lateinit var warehouseSpinner: Spinner
    private lateinit var itemNumberEditText: EditText
    private lateinit var searcItemInBinButton: Button
    private lateinit var binding: FragmentSearchForBinsWithProductBinding
    private val viewModel: BinsWithProductViewModel by activityViewModels()
    private lateinit var progressDialog: Dialog

    // Network-related variables
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback : ConnectivityManager.NetworkCallback
    private lateinit var networkRequest: NetworkRequest
    private var hasPageJustStarted: Boolean = false

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
        initNetworkRelatedComponents()

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
        // Register the callback
        if (connectivityManager != null) {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        }
    }

    // Handle onPause lifecycle event
    override fun onPause() {
        super.onPause()

        // Unregister the callback
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
    private fun initUIElements(){
        warehouseSpinner = binding.warehouseSpinner
        itemNumberEditText = binding.itemNumberEditText
        searcItemInBinButton = binding.searchBinButton
        searcItemInBinButton.setOnClickListener{
            viewModel.getItemDetailsForBinSearchFromBackend(warehouseSpinner.selectedItem.toString(), itemNumberEditText.text.toString())
            viewModel.getBinsThatHaveProductFromBackend(warehouseSpinner.selectedItem.toString(), itemNumberEditText.text.toString())
            progressDialog.show()
        }
        progressDialog = Dialog(requireContext()).apply{
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
            show()
        }
    }

    private fun initNetworkRelatedComponents(){
        // Initialize network-related components
        connectivityManager = requireContext().getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkRequest = NetworkRequest.Builder().build()
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            // Handle network availability
            override fun onAvailable(network: Network) {
                // Handle connection
                if (hasPageJustStarted)
                    Toast.makeText(requireContext(), resources.getString(R.string.internet_restored), Toast.LENGTH_SHORT).show()
                else
                    hasPageJustStarted = true

                if(warehouseSpinner.selectedItem == null) {
                    activity?.runOnUiThread {
                        progressDialog.show()
                        viewModel.getWarehousesFromBackendForSpinner()
                    }
                }
            }
            // Handle network loss
            override fun onLost(network: Network) {
                // Handle disconnection
                hasPageJustStarted = true
                Toast.makeText(requireContext(), resources.getString(R.string.internet_lost), Toast.LENGTH_SHORT).show()
            }
        }
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
                Toast.makeText(requireContext(),resources.getString(R.string.network_request_error_message), Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.listOfWarehouses.observe(viewLifecycleOwner) {newWarehousesList ->
            progressDialog.dismiss()
            fillSpinnerWithWarehouses(newWarehousesList)
        }


        viewModel.haveBothAPIBeenCalled.observe(viewLifecycleOwner){haveBothAPIsBeenCalled ->
            if(viewModel.haveBothAPIBeenCalled.value!!){
                progressDialog.dismiss()
                val bundle = BundleUtils.getBundleToSendFragmentNameToNextFragment("SearchBinsWithProductFragment")
                findNavController().navigate(R.id.action_searchBinsWithProductFragment_to_binsThatHaveProductFragment, bundle)
            }

        }
        viewModel.listOfBinsThatHaveProduct.observe(viewLifecycleOwner){newListOfBins ->


        }

        viewModel.itemDetails.observe(viewLifecycleOwner){ newItemDetails ->

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

}