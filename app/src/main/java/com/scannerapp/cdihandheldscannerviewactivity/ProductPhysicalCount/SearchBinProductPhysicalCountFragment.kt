package com.scannerapp.cdihandheldscannerviewactivity.ProductPhysicalCount

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.comdist.cdihandheldscannerviewactivity.InventoryCount.InventoryCountViewModel
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.BinInfo
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
    private var allBinInfo: List<BinInfo> = listOf() // Initialize with empty list

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.product_physical_count_bin_list_fragment, container, false
        )
        setupUI()
        initObservers()

        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        val warehouseNumber = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())

        viewModel.setCompanyIDFromSharedPref(companyID)
        viewModel.setWarehouseNumberFromSharedPref(warehouseNumber)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        restoreFilterStates()
        binding.binNumberSearchEditText.text.clear()
    }

    private fun setupUI() {
        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        binItemAdapter = BinItemAdapter(requireContext()) { selectedBin: BinInfo ->
            navigateToEditAndSearchItemFragment(selectedBin)
        }
        binding.binSearchList.adapter = binItemAdapter

        progressDialog.show()
        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        val warehouseNumber = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
        viewModel.getAllBinNumbers(companyID, warehouseNumber)

        binding.laneFilterButton.setOnClickListener {
            showLaneSelectionDialog()
        }

        binding.classCodeFilterButton.setOnClickListener {
            handleFilterButton(
                binding.classCodeFilterButton,
                viewModel.enteredClassCode,
                { classCode -> viewModel.getBinsByClassCode(classCode, companyID, warehouseNumber) }
            )
        }

        binding.VendorFilterButton.setOnClickListener {
            handleFilterButton(
                binding.VendorFilterButton,
                viewModel.enteredVendor,
                { vendor -> viewModel.getBinsByVendor(vendor, companyID, warehouseNumber) }
            )
        }

        binding.ItemNumberFilterButton.setOnClickListener {
            handleFilterButton(
                binding.ItemNumberFilterButton,
                viewModel.enteredItemNumber,
                { itemNumber -> viewModel.getBinsByItemNumber(itemNumber, companyID, warehouseNumber) }
            )
        }

        binding.binNumberSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val trimmedText = s.toString().trim()
                if (trimmedText.isEmpty()) {
                    applyFilters()
                } else {
                    val filteredList = filteredBinInfoByLane().filter { binInfo: BinInfo ->
                        binInfo.binLocation.contains(trimmedText, ignoreCase = true)
                    }
                    binItemAdapter.updateData(filteredList)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun handleFilterButton(button: View, filter: MutableLiveData<String>, action: (String) -> Unit) {
        val filterValue = binding.binNumberSearchEditText.text.toString().trim()
        if (filter.value?.isNotEmpty() == true && filterValue.isEmpty()) {
            filter.value = ""
            button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)
            val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
            val warehouseNumber = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
            viewModel.getAllBinNumbers(companyID, warehouseNumber)
        } else {
            filter.value = filterValue
            if (filterValue.isNotEmpty()) {
                binding.binNumberSearchEditText.text.clear()
                progressDialog.show()
                action(filterValue)
                button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Light_Blue)
            }
        }
    }

    private fun initObservers() {
        viewModel.binInfo.observe(viewLifecycleOwner) { newBinInfo: List<BinInfo> ->
            progressDialog.dismiss()
            Log.d("SearchBinFragment", "New Bin Info: $newBinInfo")
            allBinInfo = newBinInfo
            applyFilters()
            initBinAutoCompleteTextView(newBinInfo)
        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLastAPICallSuccessful: Boolean ->
            if (!wasLastAPICallSuccessful) {
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }
        }
    }

    private fun initBinAutoCompleteTextView(newBinSuggestion: List<BinInfo>) {
        val autoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, newBinSuggestion.map { it.binLocation })
        (binding.binNumberSearchEditText as? AutoCompleteTextView)?.apply {
            setAdapter(autoCompleteAdapter)
            threshold = 1

            onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>, _, position: Int, _ ->
                val selectedBinLocation = parent.getItemAtPosition(position) as String
                val selectedBin = newBinSuggestion.first { it.binLocation == selectedBinLocation }
                binItemAdapter.updateData(listOf(selectedBin))
                navigateToEditAndSearchItemFragment(selectedBin)
            }
        }

        binding.binNumberSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val trimmedText = s.toString().trim()
                if (trimmedText.isEmpty()) {
                    applyFilters()
                } else {
                    val filteredList = filteredBinInfoByLane().filter { binInfo: BinInfo ->
                        binInfo.binLocation.contains(trimmedText, ignoreCase = true)
                    }
                    binItemAdapter.updateData(filteredList)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun navigateToEditAndSearchItemFragment(selectedBin: BinInfo) {
        viewModel.setCurrentlySelectedBin(selectedBin)
        view?.findNavController()?.navigate(R.id.EditAndSearchItemProductPhysicalCountFragment)
    }

    private fun showLaneSelectionDialog() {
        val lanes = allBinInfo.map { it.binLocation.take(2) }.distinct().sorted().toMutableList().apply { add(0, "ALL") }.toTypedArray()
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Select Lane")
            setItems(lanes) { _, which ->
                viewModel.selectedLane.value = lanes[which]
                applyFilters()
            }
            show()
        }
    }

    private fun filteredBinInfoByLane(): List<BinInfo> {
        return if (viewModel.selectedLane.value == "ALL") {
            allBinInfo
        } else {
            allBinInfo.filter { it.binLocation.startsWith(viewModel.selectedLane.value ?: "ALL", ignoreCase = true) }
        }
    }

    private fun applyFilters() {
        Log.d("SearchBinFragment", "Applying Filters: selectedLane=${viewModel.selectedLane.value}, enteredClassCode=${viewModel.enteredClassCode.value}, enteredVendor=${viewModel.enteredVendor.value}, enteredItemNumber=${viewModel.enteredItemNumber.value}")

        val filteredList = allBinInfo.filter { binInfo ->
            val matchesLane = (viewModel.selectedLane.value == "ALL" || binInfo.binLocation.startsWith(viewModel.selectedLane.value ?: "ALL", ignoreCase = true))
            matchesLane
        }

        Log.d("SearchBinFragment", "Filtered List: $filteredList")
        binItemAdapter.updateData(filteredList)
    }

    private fun restoreFilterStates() {
        viewModel.enteredClassCode.value?.let { classCode ->
            if (classCode.isNotEmpty()) {
                binding.binNumberSearchEditText.setText(classCode)
                binding.classCodeFilterButton.performClick()
            }
        }
        viewModel.enteredVendor.value?.let { vendor ->
            if (vendor.isNotEmpty()) {
                binding.binNumberSearchEditText.setText(vendor)
                binding.VendorFilterButton.performClick()
            }
        }
        viewModel.enteredItemNumber.value?.let { itemNumber ->
            if (itemNumber.isNotEmpty()) {
                binding.binNumberSearchEditText.setText(itemNumber)
                binding.ItemNumberFilterButton.performClick()
            }
        }
        viewModel.selectedLane.value?.let { selectedLane ->
            if (selectedLane.isNotEmpty()) {
                applyFilters()
            }
        }
    }
}
