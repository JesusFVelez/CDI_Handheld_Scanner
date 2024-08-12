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
import android.widget.Button
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

        val companyID = "" // Obtain this value from your app logic
        val warehouseNumber = 0 // Obtain this value from your app logic

        viewModel.setCompanyIDFromSharedPref(companyID)
        viewModel.setWarehouseNumberFromSharedPref(warehouseNumber)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFilterStates(requireContext()) // Load filter states when the fragment is resumed
        restoreFilterStates()
        applyFilters()
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveFilterStates(requireContext()) // Save filter states when the fragment is paused
    }

    private fun setupUI() {
        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        binItemAdapter = BinItemAdapter(requireContext()) { selectedBin ->
            viewModel.setSelectedBin(selectedBin)
            navigateToEditAndSearchItemFragment()
        }
        binding.binSearchList.adapter = binItemAdapter

        progressDialog.show()

        binding.laneFilterButton.setOnClickListener {
            showLaneSelectionDialog()
        }

        binding.classCodeFilterButton.setOnClickListener {
            handleSingleFilterButton(
                binding.classCodeFilterButton,
                viewModel.enteredClassCode,
                "Class Code"
            )
        }

        binding.VendorFilterButton.setOnClickListener {
            handleSingleFilterButton(
                binding.VendorFilterButton,
                viewModel.enteredVendor,
                "Vendor"
            )
        }

        binding.ItemNumberFilterButton.setOnClickListener {
            handleSingleFilterButton(
                binding.ItemNumberFilterButton,
                viewModel.enteredItemNumber,
                "Item Number"
            )
        }

        binding.binNumberSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val trimmedText = s.toString().trim().toUpperCase()
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
        prefix: String
    ) {
        val filterValue = binding.binNumberSearchEditText.text.toString().trim().toUpperCase()

        if (filterValue.isNotEmpty()) {
            filter.value = filterValue
            button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Light_Blue)
            (button as? Button)?.text = "$prefix: $filterValue"
        } else {
            filter.value = ""
            button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)
            (button as? Button)?.text = prefix
        }
        applyCombinedFilters()
        binding.binNumberSearchEditText.text.clear() // Clear the text after applying filters
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

        // Add observers for filter states
        viewModel.enteredClassCode.observe(viewLifecycleOwner, Observer { checkFilters() })
        viewModel.enteredVendor.observe(viewLifecycleOwner, Observer { checkFilters() })
        viewModel.enteredItemNumber.observe(viewLifecycleOwner, Observer { checkFilters() })
    }

    private fun initBinAutoCompleteTextView(newBinSuggestion: List<BinsByClassCodeByVendorAndByItemNumber>) {
        val autoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, newBinSuggestion.map { it.binLocation.toUpperCase() })
        (binding.binNumberSearchEditText as? AutoCompleteTextView)?.apply {
            setAdapter(autoCompleteAdapter)
            threshold = 1

            onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>, _, position: Int, _ ->
                val selectedBinLocation = parent.getItemAtPosition(position) as String
                val selectedBin = newBinSuggestion.first { it.binLocation.equals(selectedBinLocation, ignoreCase = true) }
                viewModel.setSelectedBin(selectedBin)
                binItemAdapter.updateData(listOf(selectedBin))
                navigateToEditAndSearchItemFragment()
            }
        }

        binding.binNumberSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val trimmedText = s.toString().trim().toUpperCase()
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

    private fun navigateToEditAndSearchItemFragment() {
        view?.findNavController()?.navigate(R.id.EditAndSearchItemProductPhysicalCountFragment)
    }

    private fun showLaneSelectionDialog() {
        val lanes = allBinInfo.map { it.binLocation.take(2).toUpperCase() }.distinct().sorted().toMutableList().apply { add(0, "ALL") }.toTypedArray()
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Select Lane")
            setItems(lanes) { _, which ->
                viewModel.selectedLane.value = lanes[which]
                if (viewModel.selectedLane.value != "ALL") {
                    binding.laneFilterButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Light_Blue)
                    binding.laneFilterButton.text = "Lane: ${viewModel.selectedLane.value}"
                } else {
                    binding.laneFilterButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)
                    binding.laneFilterButton.text = "Lane"
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

        if (classCode.isEmpty() && vendor.isEmpty() && itemNumber.isEmpty()) {
            viewModel.getAllBinNumbers(companyID, warehouseNumber)
        } else {
            viewModel.getBinsByClassCodeByVendorAndByItemNumber(classCode, vendor, itemNumber, companyID, warehouseNumber)
        }
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

        // Restore the state of the class code filter button
        binding.classCodeFilterButton.backgroundTintList = if (lastEnteredClassCode.isNotEmpty()) {
            binding.classCodeFilterButton.text = "Class Code: $lastEnteredClassCode"
            ContextCompat.getColorStateList(requireContext(), R.color.CDI_Light_Blue)
        } else {
            binding.classCodeFilterButton.text = "Class Code"
            ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)
        }

        // Restore the state of the vendor filter button
        binding.VendorFilterButton.backgroundTintList = if (lastEnteredVendor.isNotEmpty()) {
            binding.VendorFilterButton.text = "Vendor: $lastEnteredVendor"
            ContextCompat.getColorStateList(requireContext(), R.color.CDI_Light_Blue)
        } else {
            binding.VendorFilterButton.text = "Vendor"
            ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)
        }

        // Restore the state of the item number filter button
        binding.ItemNumberFilterButton.backgroundTintList = if (lastEnteredItemNumber.isNotEmpty()) {
            binding.ItemNumberFilterButton.text = "Item Number: $lastEnteredItemNumber"
            ContextCompat.getColorStateList(requireContext(), R.color.CDI_Light_Blue)
        } else {
            binding.ItemNumberFilterButton.text = "Item Number"
            ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)
        }

        // Restore the state of the lane filter button
        viewModel.selectedLane.value?.let { selectedLane ->
            binding.laneFilterButton.backgroundTintList = if (selectedLane != "ALL") {
                binding.laneFilterButton.text = "Lane: $selectedLane"
                ContextCompat.getColorStateList(requireContext(), R.color.CDI_Light_Blue)
            } else {
                binding.laneFilterButton.text = "Lane"
                ContextCompat.getColorStateList(requireContext(), R.color.CDI_Gray)
            }
        }

        // Apply the filters after restoring states
        applyFilters()
    }

    private fun checkFilters() {
        val classCode = viewModel.enteredClassCode.value.orEmpty()
        val vendor = viewModel.enteredVendor.value.orEmpty()
        val itemNumber = viewModel.enteredItemNumber.value.orEmpty()
        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        val warehouseNumber = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())

        if (classCode.isEmpty() && vendor.isEmpty() && itemNumber.isEmpty()) {
            viewModel.getAllBinNumbers(companyID, warehouseNumber)
        } else {
            viewModel.getBinsByClassCodeByVendorAndByItemNumber(classCode, vendor, itemNumber, companyID, warehouseNumber)
        }
    }
}
