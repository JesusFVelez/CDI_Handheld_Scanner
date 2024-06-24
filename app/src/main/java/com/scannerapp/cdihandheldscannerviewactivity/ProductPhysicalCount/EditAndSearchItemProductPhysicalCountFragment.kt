package com.scannerapp.cdihandheldscannerviewactivity.ProductPhysicalCount

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.comdist.cdihandheldscannerviewactivity.InventoryCount.InventoryCountViewModel
import com.scannerapp.cdihandheldscannerviewactivity.Adapter.ItemAdapter
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.scannerapp.cdihandheldscannerviewactivity.databinding.ProductPhysicalCountItemListFragmentBinding

class EditAndSearchItemProductPhysicalCountFragment : Fragment() {
    private lateinit var binding: ProductPhysicalCountItemListFragmentBinding
    private val viewModel: InventoryCountViewModel by activityViewModels()
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var progressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.product_physical_count_item_list_fragment, container, false
        )
        setupUI()
        initObservers()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.itemSearchEditText.text.clear()
        fetchItemsInBin()
    }

    private fun setupUI() {
        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        itemAdapter = ItemAdapter(requireContext(), viewModel) { selectedItem ->
            // Handles item click if needed for future use
        }
        binding.itemSearchList.adapter = itemAdapter

        binding.itemSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val trimmedText = s.toString().trim()
                filterItemList(trimmedText)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.countButton.setOnClickListener {
            val query = binding.itemSearchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                Log.d("EditAndSearchFragment", "Count button clicked, fetching item details for: $query")
                fetchItemDetails(query)
            } else {
                AlerterUtils.startErrorAlerter(requireActivity(), "Please enter a valid item number or barcode.")
            }
        }

        binding.itemSearchEditText.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val query = binding.itemSearchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    Log.d("EditAndSearchFragment", "Enter key pressed, fetching item details for: $query")
                    fetchItemDetails(query)
                } else {
                    AlerterUtils.startErrorAlerter(requireActivity(), "Please enter a valid item number or barcode.")
                }
                true
            } else {
                false
            }
        }
    }

    private fun fetchItemsInBin() {
        val selectedBin = viewModel.setCurrentlySelectedBin.value
        val warehouseNO = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
        viewModel.setWarehouseNumberFromSharedPref(warehouseNO)

        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)
        if (selectedBin != null) {
            progressDialog.show()
            viewModel.getAllItemsInBinForSuggestion(
                pBinLocation = selectedBin.binLocation,
                pWarehouse = warehouseNO,
                pCompanyID = companyID
            )
        }
    }

    private fun fetchItemDetails(query: String) {
        val warehouseNO = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        progressDialog.show()
        viewModel.getItemDetailsForPopup(pItemNumberOrBarCode = query, pWarehouse = warehouseNO, pCompanyID = companyID)
    }

    private fun initObservers() {
        viewModel.itemInfo.observe(viewLifecycleOwner) { newItemInfo ->
            progressDialog.dismiss()
            itemAdapter.updateData(newItemInfo)
        }

        viewModel.itemInfoPopUp.observe(viewLifecycleOwner) { itemDetails ->
            progressDialog.dismiss()
            Log.d("EditAndSearchFragment", "Item details received for popup: $itemDetails")
            if (itemDetails.isNotEmpty()) {
                itemAdapter.showPopupDialogForItemSearch(itemDetails.first())
            } else {
                AlerterUtils.startErrorAlerter(requireActivity(), "No item details found.")
            }
        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLastAPICallSuccessful ->
            if (!wasLastAPICallSuccessful) {
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }
        }
    }

    private fun filterItemList(query: String) {
        val filteredList = viewModel.itemInfo.value?.filter { item ->
            item.itemNumber.contains(query, ignoreCase = true) ||
                    item.barCode?.contains(query, ignoreCase = true) == true
        }
        itemAdapter.updateData(filteredList ?: listOf())
    }
}
