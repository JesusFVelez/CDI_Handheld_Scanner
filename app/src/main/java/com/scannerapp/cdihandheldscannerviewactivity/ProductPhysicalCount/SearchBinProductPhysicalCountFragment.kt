package com.scannerapp.cdihandheldscannerviewactivity.ProductPhysicalCount

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.comdist.cdihandheldscannerviewactivity.InventoryCount.InventoryCountViewModel
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.TtBinInfo
import com.comdist.cdihandheldscannerviewactivity.adapters.BinItemAdapter
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.scannerapp.cdihandheldscannerviewactivity.databinding.ProductPhysicalCountBinListFragmentBinding

class SearchBinProductPhysicalCountFragment : Fragment() {
    private lateinit var binding: ProductPhysicalCountBinListFragmentBinding
    private val viewModel: InventoryCountViewModel by activityViewModels()
    private lateinit var binItemAdapter: BinItemAdapter
    private lateinit var progressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.product_physical_count_bin_list_fragment, container, false
        )
        setupUI()
        initObservers()

        // Retrieve company ID and warehouse number from shared preferences
        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        val warehouseNumber = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())

        // Set these values in the ViewModel
        viewModel.setCompanyIDFromSharedPref(companyID)
        viewModel.setWarehouseNumberFromSharedPref(warehouseNumber)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.binNumberSearchEditText.text.clear()
    }

    private fun setupUI() {
        val warehouseNO = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
        viewModel.setWarehouseNumberFromSharedPref(warehouseNO)

        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)

        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        binItemAdapter = BinItemAdapter(requireContext()) { selectedBin ->
            // Handle bin item click takes you places
        }
        binding.binSearchList.adapter = binItemAdapter

        progressDialog.show()
        viewModel.getAllBinNumbers(companyID, warehouseNO)
    }

    private fun initObservers() {
        viewModel.binInfo.observe(viewLifecycleOwner) { newBinInfo ->
            progressDialog.dismiss()
            initBinAutoCompleteTextView(newBinInfo)
            binItemAdapter.updateData(newBinInfo) // Ensure initial data is loaded into the adapter
        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLastAPICallSuccessful ->
            if (!wasLastAPICallSuccessful) {
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }
        }
    }

    private fun retrieveUserDetailsFromSharedPrefs() {
        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        val warehouseNumber = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())

        viewModel.setCompanyIDFromSharedPref(companyID)
        viewModel.setWarehouseNumberFromSharedPref(warehouseNumber)
    }

    private fun initBinAutoCompleteTextView(newBinSuggestion: List<TtBinInfo>) {
        val autoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, newBinSuggestion.map { it.binLocation })
        (binding.binNumberSearchEditText as? AutoCompleteTextView)?.apply {
            setAdapter(autoCompleteAdapter)
            threshold = 1

            onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedBinLocation = parent.getItemAtPosition(position) as String
                binItemAdapter.updateData(listOf(newBinSuggestion.first { it.binLocation == selectedBinLocation }))
            }
        }

        binding.binNumberSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val trimmedText = s.toString().trim()
                if (trimmedText.isEmpty()) {
                    binItemAdapter.updateData(newBinSuggestion)
                } else {
                    val filteredList = newBinSuggestion.filter {
                        it.binLocation.contains(trimmedText, ignoreCase = true)
                    }
                    binItemAdapter.updateData(filteredList)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
