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
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.comdist.cdihandheldscannerviewactivity.InventoryCount.InventoryCountViewModel
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.BinsByClassCodeByVendorAndByItemNumber
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
    private var allBinInfo: List<BinsByClassCodeByVendorAndByItemNumber> = listOf()

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
        applyFilters()
    }

    private fun setupUI() {
        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        binItemAdapter = BinItemAdapter(requireContext()) { selectedBin ->
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
            handleSingleFilterButton(
                binding.classCodeFilterButton,
                viewModel.enteredClassCode,
                { classCode -> applyCombinedFilters() },
                "classCode"
            )
        }

        binding.VendorFilterButton.setOnClickListener {
            handleSingleFilterButton(
                binding.VendorFilterButton,
                viewModel.enteredVendor,
                { vendor -> applyCombinedFilters() },
                "vendor"
            )
        }

        binding.ItemNumberFilterButton.setOnClickListener {
            handleSingleFilterButton(
                binding.ItemNumberFilterButton,
                viewModel.enteredItemNumber,
                { itemNumber -> applyCombinedFilters() },
                "itemNumber"
            )
        }

        binding.binNumberSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val trimmedText = s.toString().trim()
                if (trimmedText.isEmpty()) {
                    applyFilters()
                } else {
                    val filteredList = filteredBinInfoByLane().filter { binInfo ->
                        binInfo.binLocation.contains(trimmedText, ignoreCase = true)
                    }
                    val uniqueBins = filteredList.distinctBy { it.binLocation }
                    binItemAdapter.updateData(uniqueBins)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun handleSingleFilterButton(
        button: View,
        filter: MutableLiveData<String>,
        action: (String) -> Unit,
        filterType: String
    ) {
        val filterValue = binding.binNumberSearchEditText.text.toString().trim()

        // Check if the button is already grey and the edit text is empty, do nothing
        if (button.backgroundTintList == ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray) && filterValue.isEmpty()) {
            return
        }

        if (filterValue.isNotEmpty()) {
            clearOtherFilters(filterType)
            filter.value = filterValue
            progressDialog.show()
            action(filterValue)
            button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Light_Blue)
            binding.binNumberSearchEditText.text.clear() // Clear the text only after filtering
        } else {
            filter.value = ""
            button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)
            val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
            val warehouseNumber = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
            viewModel.getAllBinNumbers(companyID, warehouseNumber)
        }
    }

    private fun clearOtherFilters(activeFilterType: String) {
        if (activeFilterType != "classCode") {
            viewModel.enteredClassCode.value = ""
            binding.classCodeFilterButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)
        }
        if (activeFilterType != "vendor") {
            viewModel.enteredVendor.value = ""
            binding.VendorFilterButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)
        }
        if (activeFilterType != "itemNumber") {
            viewModel.enteredItemNumber.value = ""
            binding.ItemNumberFilterButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)
        }
    }

    private fun initObservers() {
        viewModel.binInfo.observe(viewLifecycleOwner, Observer { newBinInfo: List<BinsByClassCodeByVendorAndByItemNumber> ->
            progressDialog.dismiss()
            Log.d("SearchBinFragment", "New Bin Info: $newBinInfo")
            allBinInfo = newBinInfo
            applyFilters()
            initBinAutoCompleteTextView(newBinInfo)
        })

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner, Observer { wasLastAPICallSuccessful: Boolean ->
            if (!wasLastAPICallSuccessful) {
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }
        })
    }

    private fun initBinAutoCompleteTextView(newBinSuggestion: List<BinsByClassCodeByVendorAndByItemNumber>) {
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
                    val filteredList = filteredBinInfoByLane().filter { binInfo ->
                        binInfo.binLocation.contains(trimmedText, ignoreCase = true)
                    }
                    val uniqueBins = filteredList.distinctBy { it.binLocation }
                    binItemAdapter.updateData(uniqueBins)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun navigateToEditAndSearchItemFragment(selectedBin: BinsByClassCodeByVendorAndByItemNumber) {
        viewModel.setCurrentlySelectedBin(selectedBin)
        view?.findNavController()?.navigate(R.id.EditAndSearchItemProductPhysicalCountFragment)
    }

    private fun showLaneSelectionDialog() {
        val lanes = allBinInfo.map { it.binLocation.take(2) }.distinct().sorted().toMutableList().apply { add(0, "ALL") }.toTypedArray()
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Select Lane")
            setItems(lanes) { _, which ->
                viewModel.selectedLane.value = lanes[which]
                if (viewModel.selectedLane.value != "ALL") {
                    binding.laneFilterButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Light_Blue)
                } else {
                    binding.laneFilterButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)
                }
                applyFilters()
            }
            show()
        }
    }

    private fun filteredBinInfoByLane(): List<BinsByClassCodeByVendorAndByItemNumber> {
        return if (viewModel.selectedLane.value == "ALL") {
            allBinInfo
        } else {
            allBinInfo.filter { it.binLocation.startsWith(viewModel.selectedLane.value ?: "ALL", ignoreCase = true) }
        }
    }

    private fun applyCombinedFilters() {
        val classCode = viewModel.enteredClassCode.value.orEmpty()
        val vendor = viewModel.enteredVendor.value.orEmpty()
        val itemNumber = viewModel.enteredItemNumber.value.orEmpty()
        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        val warehouseNumber = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())

        viewModel.getBinsByClassCodeByVendorAndByItemNumber(classCode, vendor, itemNumber, companyID, warehouseNumber)
    }

    private fun applyFilters() {
        Log.d("SearchBinFragment", "Applying Filters: selectedLane=${viewModel.selectedLane.value}, enteredClassCode=${viewModel.enteredClassCode.value}, enteredVendor=${viewModel.enteredVendor.value}, enteredItemNumber=${viewModel.enteredItemNumber.value}")

        val filteredList = filteredBinInfoByLane().filter { binInfo ->
            val matchesLane = (viewModel.selectedLane.value == "ALL" || binInfo.binLocation.startsWith(viewModel.selectedLane.value ?: "ALL", ignoreCase = true))
            matchesLane
        }

        // Ensure no bin is displayed more than once
        val uniqueBins = filteredList.distinctBy { it.binLocation }

        Log.d("SearchBinFragment", "Filtered List: $uniqueBins")
        binItemAdapter.updateData(uniqueBins)
    }

    private fun restoreFilterStates() {
        // Restore binNumberSearchEditText with the last entered text
        val lastEnteredClassCode = viewModel.enteredClassCode.value ?: ""
        val lastEnteredVendor = viewModel.enteredVendor.value ?: ""
        val lastEnteredItemNumber = viewModel.enteredItemNumber.value ?: ""

        // Determine which text to restore based on the last active filter
        val lastEnteredText = when {
            lastEnteredClassCode.isNotEmpty() -> lastEnteredClassCode
            lastEnteredVendor.isNotEmpty() -> lastEnteredVendor
            lastEnteredItemNumber.isNotEmpty() -> lastEnteredItemNumber
            else -> ""
        }
        binding.binNumberSearchEditText.setText(lastEnteredText)

        // Restore the state of the class code filter button to gray
        binding.classCodeFilterButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)

        // Restore the state of the vendor filter button to gray
        binding.VendorFilterButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)

        // Restore the state of the item number filter button to gray
        binding.ItemNumberFilterButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)

        // Apply filters with the new state
        applyFilters()

        // Restore the state of the lane filter button
        viewModel.selectedLane.value?.let { selectedLane ->
            if (selectedLane.isNotEmpty()) {
                if (selectedLane != "ALL") {
                    binding.laneFilterButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Light_Blue)
                } else {
                    binding.laneFilterButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)
                }
                applyFilters()
            }
        }
    }
}
