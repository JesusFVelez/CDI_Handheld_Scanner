package com.scannerapp.cdihandheldscannerviewactivity.ProductPhysicalCount

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.comdist.cdihandheldscannerviewactivity.InventoryCount.InventoryCountViewModel
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseItemConfirmWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.TtItemInf
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

    // Tracking the last focused EditText
    private var lastSelectedEditText: EditText? = null

    // Variables to hold references to the current popup's EditText views
    private var currentItemAmountEditText: EditText? = null
    private var currentWeightEditText: EditText? = null
    private var currentItemNumberTextView: TextView? = null

    // Define the type explicitly for barcodeScannerEditText
    private lateinit var barcodeScannerEditText: EditText

    // Flag to track if the request is from the count button
    private var isFromCountButton: Boolean = false

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

        itemAdapter = ItemAdapter(requireContext(), viewModel, this) { selectedItem ->
            viewModel.setShouldShowPopup(true)
            viewModel.setShouldShowError(true)
            isFromCountButton = false // Reset the flag when selecting an item from the list
            fetchItemDetails(selectedItem.itemNumber)
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
                viewModel.setCountButtonPressed(true)
                viewModel.setShouldShowPopup(true)
                viewModel.setShouldShowError(true)
                isFromCountButton = true
                fetchItemDetails(query)
            } else {
                viewModel.setShouldShowError(true)
                showError("Please enter a valid item number or barcode.")
            }
        }

        binding.itemSearchEditText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val query = binding.itemSearchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.setCountButtonPressed(true)
                    viewModel.setShouldShowPopup(true)
                    viewModel.setShouldShowError(true)
                    isFromCountButton = true
                    fetchItemDetails(query)
                } else {
                    viewModel.setShouldShowError(true)
                    showError("Please enter a valid item number or barcode.")
                }
                true
            } else {
                false
            }
        }
    }

    private fun fetchItemsInBin() {
        val selectedBin = viewModel.selectedBin.value
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
            if (viewModel.shouldShowPopup.value == true && itemDetails.isNotEmpty()) {
                showPopupDialog(itemDetails.first())
                viewModel.setShouldShowPopup(false)
            }
        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLastAPICallSuccessful ->
            if (wasLastAPICallSuccessful == false) {
                progressDialog.dismiss()
                if (viewModel.shouldShowError.value == true) {
                    AlerterUtils.startNetworkErrorAlert(requireActivity())
                    viewModel.setShouldShowError(false)
                }
            }
        }

        viewModel.confirmItemResult.observe(viewLifecycleOwner) { confirmResult ->
            if (confirmResult != null) {
                handleConfirmItemResult(confirmResult)
            }
        }

        viewModel.selectedBin.observe(viewLifecycleOwner) { selectedBin ->
            if (selectedBin != null) {
                fetchItemsInBin()
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

    private fun handleConfirmItemResult(confirmResult: ResponseItemConfirmWrapper) {
        if (confirmResult.response.wasItemConfirmed == true) {
            val selectedItemNumberTextView = currentItemNumberTextView
            if (selectedItemNumberTextView != null && !selectedItemNumberTextView.text.isNullOrEmpty() && confirmResult.response.actualItemNumber == selectedItemNumberTextView.text.toString()) {
                if (currentItemAmountEditText != null && currentWeightEditText != null) {
                    val currentCount = currentItemAmountEditText?.text.toString().toIntOrNull() ?: 0
                    val newCount = currentCount + 1
                    currentItemAmountEditText?.setText(newCount.toString())

                    // After updating the item count, refocus to barcodeScannerEditText
                    barcodeScannerEditText.requestFocus()
                    barcodeScannerEditText.post {
                        barcodeScannerEditText.text.clear()  // Optionally clear text if needed right away
                    }

                    if (currentWeightEditText?.text.toString().isEmpty()) {
                        currentWeightEditText?.setText("0.0")
                    }
                    val currentWeight = currentWeightEditText?.text.toString().toDoubleOrNull() ?: 0.0
                    val weightInBarcode = confirmResult.response.weightInBarcode
                    val uomQtyInBarcode = confirmResult.response.UOMQtyInBarcode

                    val newWeight = if (uomQtyInBarcode > 0) {
                        (uomQtyInBarcode * weightInBarcode) + currentWeight
                    } else {
                        weightInBarcode + currentWeight
                    }

                    currentWeightEditText?.setText(newWeight.toString())

                    AlerterUtils.startSuccessAlert(requireActivity(), "Success", "Item confirmed: ${confirmResult.response.actualItemNumber}")
                } else {
                    showError("Item confirmation failed: Item amount or weight EditText is null")
                }
            } else {
                showError("Item confirmation failed: Item numbers do not match or selected item number is empty")
            }
        } else {
            showError("Item confirmation failed: ${confirmResult.response.errorMessage}")
        }
    }

    private fun showPopupDialog(item: TtItemInf) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.product_physical_count_item_quantity_count_popup, null)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogView)

        val selectedItemView = dialogView.findViewById<View>(R.id.selectedItem)
        val itemNumberTextView = selectedItemView.findViewById<TextView>(R.id.ItemNumberTextView)
        val descriptionTextView = selectedItemView.findViewById<TextView>(R.id.descriptionTextView)
        val expirationDateTextView = selectedItemView.findViewById<TextView>(R.id.orderDateValueTextView)
        val binLocationTextView = selectedItemView.findViewById<TextView>(R.id.binLocationTextView)
        val lotNumberTextView = selectedItemView.findViewById<TextView>(R.id.dateWantedValueTextView)

        itemNumberTextView.text = item.itemNumber
        descriptionTextView.text = item.itemDescription
        expirationDateTextView.text = item.expireDate ?: "N/A"
        binLocationTextView.text = viewModel.selectedBin.value?.binLocation ?: "N/A"
        lotNumberTextView.text = item.lotNumber ?: "N/A"

        val itemAmountEditText = dialogView.findViewById<EditText>(R.id.itemAmountEditText)
        if (isFromCountButton) {
            // Clear the qtyCounted if coming from count button
            itemAmountEditText.setText("")
        } else {
            itemAmountEditText.setText(if (item.qtyCounted != null && item.qtyCounted != 0) item.qtyCounted.toString() else "")
        }
        itemAmountEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                lastSelectedEditText = itemAmountEditText
            }
        }
        itemAmountEditText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                // Prevent default enter behavior
                barcodeScannerEditText.requestFocus()
                true // Handle the event and stop propagation
            } else {
                false // Do not intercept other key events
            }
        }

        currentItemNumberTextView = itemNumberTextView
        currentItemAmountEditText = itemAmountEditText

        val weightEditText = dialogView.findViewById<EditText>(R.id.weightEditText)
        if (isFromCountButton) {
            // Clear the weight if coming from count button
            weightEditText.setText("")
        } else {
            weightEditText.setText(if (item.weight != 0.0) item.weight.toString() else "")
        }
        weightEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                lastSelectedEditText = weightEditText
            }
        }
        weightEditText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                // Prevent default enter behavior
                barcodeScannerEditText.requestFocus()
                true // Handle the event and stop propagation
            } else {
                false // Do not intercept other key events
            }
        }
        currentWeightEditText = weightEditText

        dialogView.findViewById<AppCompatButton>(R.id.addButton).setOnClickListener {
            val quantity = itemAmountEditText.text.toString().toIntOrNull() ?: 0
            val weight = weightEditText.text.toString().toDoubleOrNull() ?: 0.0
            if (quantity >= 0 && weight >= 0.0) {
                val warehouseNO = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
                val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())

                val selectedBinLocation = viewModel.selectedBin.value?.binLocation ?: ""

                viewModel.updateCount(
                    pItemNumber = item.itemNumber,
                    pWarehouseNo = warehouseNO,
                    pBinLocation = selectedBinLocation,
                    pQtyCounted = quantity.toDouble(),
                    pWeight = weight,
                    pCompanyID = companyID
                )
                dialog.dismiss()
                viewModel.getAllItemsInBinForSuggestion(
                    pBinLocation = selectedBinLocation,
                    pWarehouse = warehouseNO,
                    pCompanyID = companyID
                )

                // Focus back on barcodeScannerEditText
                barcodeScannerEditText.post {
                    barcodeScannerEditText.requestFocus()
                    barcodeScannerEditText.text.clear()
                }
            } else {
                itemAmountEditText.error = "Please enter a valid number"
                weightEditText.error = "Please enter a valid weight"
            }
        }

        barcodeScannerEditText = dialogView.findViewById(R.id.barcodeScannerEditText)
        barcodeScannerEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val barcode = s.toString().trim()
                if (barcode.isNotEmpty()) {
                    val warehouseNO = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
                    val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
                    viewModel.confirmItem(scannedCode = barcode, companyID = companyID, warehouseNumber = warehouseNO)

                    // Clear the text and refocus on the barcodeScannerEditText
                    barcodeScannerEditText.post {
                        barcodeScannerEditText.requestFocus()
                        barcodeScannerEditText.text.clear()
                    }
                }
            }
        })

        dialog.show()
    }

    private fun showError(message: String) {
        if (viewModel.shouldShowError.value == true) {
            AlerterUtils.startErrorAlerter(requireActivity(), message)
            viewModel.setShouldShowError(false)
        }
    }
}
