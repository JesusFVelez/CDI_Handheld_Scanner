package com.scannerapp.cdihandheldscannerviewactivity.ProductPhysicalCount

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.exp

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
    private var expireDateEditText: EditText? = null

    // Define the type explicitly for barcodeScannerEditText
    private lateinit var barcodeScannerEditText: EditText

    // Flag to track if the request is from the count button
    private var isFromCountButton: Boolean = false

    // Flag to track if the popup should be displayed
    private var shouldShowPopup: Boolean = false

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Request focus on the itemSearchEditText
        binding.itemSearchEditText.requestFocus()

        // Show the keyboard explicitly
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.itemSearchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onResume() {
        super.onResume()
        binding.itemSearchEditText.text.clear()
        shouldShowPopup = false // Reset the flag to prevent unintended popup displays
        fetchItemsInBin()
    }

    private fun setupUI() {
        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        itemAdapter = ItemAdapter(requireContext(), viewModel, this) { selectedItem ->
            shouldShowPopup = true
            viewModel.setShouldShowPopup(true)
            viewModel.setShouldShowError(true)
            isFromCountButton = false // Reset the flag when selecting an item from the list

            // Convert TtItemInfo to TtItemInf
            val itemInf = TtItemInf(
                itemNumber = selectedItem.itemNumber,
                itemDescription = selectedItem.itemDescription,
                binLocation = selectedItem.binLocation,
                expireDate = selectedItem.expireDate,
                lotNumber = selectedItem.lotNumber ?: "",
                qtyCounted = selectedItem.qtyCounted ?: 0,
                inCount = selectedItem.inCount,
                barCode = selectedItem.barCode,
                weight = selectedItem.weight ?: 0.0,
                doesItemHaveWeight = selectedItem.doesItemHaveWeight,
                isItemInIvlot = selectedItem.isItemInIvlot
            )

            showPopupDialog(itemInf)
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
                shouldShowPopup = true
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
                    shouldShowPopup = true
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
            if (shouldShowPopup) {
                if (itemDetails.isNotEmpty()) {
                    showPopupDialog(itemDetails.first())
                    viewModel.setShouldShowPopup(false)
                    shouldShowPopup = false
                } else {
                    showError("No item found for the provided details.")
                }
            }
        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLastAPICallSuccessful ->
            if (wasLastAPICallSuccessful == false) {
                progressDialog.dismiss()
                if (viewModel.shouldShowError.value == true) {
                    AlerterUtils.startNetworkErrorAlert(requireActivity(), viewModel.networkErrorMessage.value!!)
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
        if (confirmResult.response.wasItemConfirmed) {
            val selectedItemNumberTextView = currentItemNumberTextView
            if (selectedItemNumberTextView != null && viewModel.confirmItemResult.value!!.response.wasItemConfirmed) {
                if (currentItemAmountEditText != null && currentWeightEditText != null) {
                    val currentCount = currentItemAmountEditText?.text.toString().toIntOrNull() ?: 0

                    var newCount = currentCount + 1
                    if(confirmResult.response.UOMQtyInBarcode.toInt() != 0)
                        newCount = currentCount + confirmResult.response.UOMQtyInBarcode.toInt()

                    currentItemAmountEditText?.setText(newCount.toString())

                    barcodeScannerEditText.requestFocus()
//                    barcodeScannerEditText.post {
//                        barcodeScannerEditText.text.clear()
//                    }

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

                    val expDateInBarcode = confirmResult.response.expirationDateInBarcode
                    if(expDateInBarcode != null)
                        expireDateEditText?.setText(expDateInBarcode)

                    //AlerterUtils.startSuccessAlert(requireActivity(), "Success", "Item confirmed")
                }
            }
        } else {
            barcodeScannerEditText.error = "Item confirmation failed: ${confirmResult.response.errorMessage}"
            //showError("Item confirmation failed: ${confirmResult.response.errorMessage}")
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
        val expireDateEditText = dialogView.findViewById<EditText>(R.id.dateEditText)

        itemNumberTextView.text = item.itemNumber
        descriptionTextView.text = item.itemDescription

        // Define date formats
        val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val targetFormat = SimpleDateFormat("MM-dd-yyyy", Locale.US)
        originalFormat.isLenient = false

        // Format expiration date for display
        val expirationDate = item.expireDate?.let {
            try {
                originalFormat.parse(it)?.let { date ->
                    targetFormat.format(date)
                }
            } catch (e: Exception) {
                null
            }
        }
        expirationDateTextView.text = expirationDate ?: "N/A"

        binLocationTextView.text = viewModel.selectedBin.value?.binLocation ?: "N/A"
        lotNumberTextView.text = item.lotNumber ?: "N/A"

        val itemAmountEditText = dialogView.findViewById<EditText>(R.id.itemAmountEditText)
        itemAmountEditText.setText(if (isFromCountButton) "" else (item.qtyCounted?.toString() ?: ""))
        itemAmountEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                lastSelectedEditText = itemAmountEditText
            }
        }
        itemAmountEditText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                barcodeScannerEditText.requestFocus()
                true
            } else {
                false
            }
        }

        currentItemNumberTextView = itemNumberTextView
        currentItemAmountEditText = itemAmountEditText
        this.expireDateEditText = expireDateEditText

        val weightEditText = dialogView.findViewById<EditText>(R.id.weightEditText)

        // Set the text or hint based on the weight value
        if (item.weight == 0.0) {
            weightEditText.setText("")
            weightEditText.hint = "Weight"
        } else {
            weightEditText.setText(item.weight.toString())
        }

        weightEditText.isEnabled = item.doesItemHaveWeight // Enable or disable based on doesItemHaveWeight
        weightEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                lastSelectedEditText = weightEditText
            }
        }
        weightEditText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                barcodeScannerEditText.requestFocus()
                true
            } else {
                false
            }
        }
        currentWeightEditText = weightEditText

        val lotNumberEditText = dialogView.findViewById<EditText>(R.id.lotEditText)
        lotNumberEditText.isEnabled = item.isItemInIvlot // Enable or disable based on isItemInIvlot

        // Set expiration date for EditText
        expireDateEditText.setText(
            if (isFromCountButton) "" else expirationDate ?: ""
        )
        lotNumberEditText.setText(if (isFromCountButton) "" else (item.lotNumber ?: ""))

        expireDateEditText.inputType = InputType.TYPE_CLASS_NUMBER
        expireDateEditText.addTextChangedListener(object : TextWatcher {
            private var previousText: String = ""
            private var lastCursorPosition: Int = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                previousText = s.toString()
                lastCursorPosition = expireDateEditText.selectionStart
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val currentText = s.toString()
                val deleting = currentText.length < previousText.length

                if (currentText.length > 10) {
                    val truncatedText = currentText.substring(0, 10)
                    expireDateEditText.setText(truncatedText)
                    expireDateEditText.setSelection(truncatedText.length)
                    return
                }

                if (!deleting) {
                    when (currentText.length) {
                        2, 5 -> if (!previousText.endsWith("-")) {
                            expireDateEditText.setText("$currentText-")
                            expireDateEditText.setSelection(currentText.length + 1)
                        }
                    }
                } else {
                    if ((currentText.length == 2 || currentText.length == 5) && previousText.endsWith("-")) {
                        expireDateEditText.setText(currentText.dropLast(1))
                        expireDateEditText.setSelection(expireDateEditText.text.length)
                    } else if (lastCursorPosition > 1 && previousText[lastCursorPosition - 1] == '-') {
                        val newPosition = lastCursorPosition - 2
                        val newText = StringBuilder(previousText).apply {
                            deleteCharAt(newPosition)
                        }.toString()
                        expireDateEditText.setText(newText)
                        expireDateEditText.setSelection(newPosition)
                    }
                }
            }
        })

        dialogView.findViewById<AppCompatButton>(R.id.addButton).setOnClickListener {
            // Check if the expiration date is provided
            if (expirationDateTextView.text == "N/A" || expireDateEditText.text.isNullOrEmpty()) {
                expireDateEditText.error = "Please add a date."
                return@setOnClickListener
            }

            val quantity = itemAmountEditText.text.toString().toIntOrNull() ?: 0
            val weight = weightEditText.text.toString().toDoubleOrNull() ?: 0.0

            // Determine which date to use and format it for the API call
            val dateInput = if (expireDateEditText.text.isNotEmpty()) {
                expireDateEditText.text.toString()
            } else {
                expirationDateTextView.text.toString()
            }

            val formattedExpireDate = dateInput.let {
                try {
                    targetFormat.parse(it)?.let { date ->
                        originalFormat.format(date)
                    }
                } catch (e: Exception) {
                    // Handle parsing exceptions (e.g., log the error)
                    it
                }
            } ?: ""

            val lotNumber = lotNumberEditText.text.toString()

            if (quantity >= 0 && weight >= 0.0) {
                // Code for implementing the expiration date popup has been commented here given that the exp date popup appears below the count popup and I can't get it to appear on top
                // for now, the code will work like before. Just a warning message indicating that the date entered is an expired date
//                val onPressOfYes: () -> Unit = {
//                    updateCountForItem(item, quantity, weight, formattedExpireDate, lotNumber, dialog)
//                }
//                val hasDateExpired = DateUtils.isDateExpired(dateInput, onPressOfYes, requireContext(), requireView())
//
//                if(!hasDateExpired)
//                    updateCountForItem(item, quantity, weight, formattedExpireDate, lotNumber, dialog)

                updateCountForItem(item, quantity, weight, formattedExpireDate, lotNumber, dialog)
                // Check if the entered date is in the past
                val enteredDate = targetFormat.parse(dateInput)
                if (enteredDate != null && enteredDate.before(Date())) {
                    AlerterUtils.startWarningAlerter(
                        requireActivity(),
                        "You have entered an expired date!"
                    )
                }
            } else {
                itemAmountEditText.error = "Please enter a valid number"
                weightEditText.error = "Please enter a valid weight"
            }
        }

        barcodeScannerEditText = dialogView.findViewById(R.id.barcodeScannerEditText)
// /*      ============================ This code will be left in comments in the case that it needs to be brought back ================
//        barcodeScannerEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            override fun afterTextChanged(s: Editable?) {
//                val barcode = s.toString().trim()
//                if (barcode.isNotEmpty()) {
//                    val warehouseNO = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
//                    val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
//                    viewModel.confirmItem(scannedCode = barcode, companyID = companyID, warehouseNumber = warehouseNO, actualItemNumber = itemNumberTextView.text.toString())
//
//                    barcodeScannerEditText.post {
//                        barcodeScannerEditText.requestFocus()
//                        barcodeScannerEditText.text.clear()
//                    }
//                }
//            }
//        })

        barcodeScannerEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                // Your code to execute when Enter is pressed
                val barcode = barcodeScannerEditText.text.toString().trim()
                if (barcode.isNotEmpty()) {
                    val warehouseNO = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
                    val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
                    viewModel.confirmItem(scannedCode = barcode, companyID = companyID, warehouseNumber = warehouseNO, actualItemNumber = itemNumberTextView.text.toString())

                    barcodeScannerEditText.post {
                        barcodeScannerEditText.requestFocus()
                        barcodeScannerEditText.text.clear()
                    }
                }
                true  // Return true if you have handled the action
            } else {
                false  // Return false if you haven't handled the action
            }
        }

        dialog.show()
    }

    private fun updateCountForItem(
        item: TtItemInf,
        quantity: Int,
        weight: Double,
        formattedExpireDate: String,
        lotNumber: String,
        dialog: Dialog
    ) {
        val warehouseNO = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        val selectedBinLocation = viewModel.selectedBin.value?.binLocation ?: ""


        viewModel.updateCount(
            pItemNumber = item.itemNumber,
            pWarehouseNo = warehouseNO,
            pBinLocation = selectedBinLocation,
            pQtyCounted = quantity.toDouble(),
            pWeight = weight,
            pCompanyID = companyID,
            pExpireDate = formattedExpireDate,
            pLotNumber = lotNumber
        )
        dialog.dismiss()
        viewModel.getAllItemsInBinForSuggestion(
            pBinLocation = selectedBinLocation,
            pWarehouse = warehouseNO,
            pCompanyID = companyID
        )

        barcodeScannerEditText.post {
            barcodeScannerEditText.requestFocus()
            barcodeScannerEditText.text.clear()
        }
    }


    private fun showError(message: String) {
        if (viewModel.shouldShowError.value == true) {
            AlerterUtils.startErrorAlerter(requireActivity(), message)
            viewModel.setShouldShowError(false)
        }
    }
}
