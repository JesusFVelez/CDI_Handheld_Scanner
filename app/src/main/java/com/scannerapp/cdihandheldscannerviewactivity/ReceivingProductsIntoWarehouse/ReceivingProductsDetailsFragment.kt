package com.scannerapp.cdihandheldscannerviewactivity.ReceivingProductsIntoWarehouse

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.DateUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.scannerapp.cdihandheldscannerviewactivity.databinding.ReceivingDetailsFragmentBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class ReceivingProductsDetailsFragment : Fragment() {

    private lateinit var binding: ReceivingDetailsFragmentBinding
    private val viewModel: ReceivingProductsViewModel by activityViewModels()

    // TextView declarations
    private lateinit var itemNumberTextView: TextView
    private lateinit var itemNameTextView: TextView

    // EditText declarations
    private lateinit var newLotEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var newExpirationDateEditText: EditText
    private lateinit var scanItemEditText: EditText
    private lateinit var weightEditText: EditText

    // Button declarations
    private lateinit var addButton: Button

    // Loading declaration
    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.receiving_details_fragment, container, false)

        initUIElements()
        initObservers()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val bundle = arguments
        val lastFragmentName: String = BundleUtils.getPastFragmentNameFromBundle(bundle)
        if (lastFragmentName == "ReceivingProductsMainFragment" && viewModel.willEditCurrentValues.value!!) {
            populateUpperDivUiElements()
            populateEditTextWithViewModelValues()
            viewModel.willNotEditCurrentValues()
            addButton.text = "Save"
        }
    }

    data class ItemToAddToDoorBin(
        val expirationDate: String,
        val binToBeMovedTo: String,
        val doorBin: String,
        val itemNumber: String,
        val lotNumber: String,
        val weight: Float,
        val quantityOfItemsAddedToDoorBin: Int
    )


    private fun initUIElements() {
        // TextView initializations
        itemNumberTextView = binding.itemNumberTextView
        itemNameTextView = binding.itemNameTextView
        populateUpperDivUiElements()

        // EditText initializations
        newLotEditText = binding.newLotAutoCompleteTextView
        quantityEditText = binding.QuantityEditText
        newExpirationDateEditText = binding.NewExpirationDateEditText
        scanItemEditText = binding.ScanItemEditText
        weightEditText = binding.WeightEditText
        /* ============================ This code will be left in comments in the case that it needs to be brought back ================
                // Set up scanItemEditText to handle barcode input
        //        scanItemEditText.addTextChangedListener(object : TextWatcher {
        //            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        //
        //            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        //
        //            override fun afterTextChanged(s: Editable?) {
        //                val barcode = s.toString().trim()
        //                if (barcode.isNotEmpty()) {
        //                    progressDialog.show()
        //                    viewModel.confirmItem(barcode)
        //
        //                    // Reset the text field for the next input
        //                    scanItemEditText.post {
        //                        scanItemEditText.setText("")
        //                    }
        //                }
        //            }
        //        })
        */
        scanItemEditText.inputType = InputType.TYPE_CLASS_TEXT
        scanItemEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                // Your code to execute when Enter is pressed
                val barcode = scanItemEditText.text.toString().trim()
                if (barcode.isNotEmpty()) {
                    progressDialog.show()
                    viewModel.confirmItem(barcode)
                }
                true  // Return true if you have handled the action
            } else {
                false  // Return false if you haven't handled the action
            }
        }

        // Button initializations
        addButton = binding.addButton

        // Loading initialization
        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        // Validates whether item uses lot number or not
        val canEditLotNumber = viewModel.itemInfo.value!!.doesItemUseLotNumber
        newLotEditText.isEnabled = canEditLotNumber

        // Validates whether item has weight or not
        val doesItemUseWeight = viewModel.itemInfo.value!!.doesItemHaveWeight
        weightEditText.isEnabled = doesItemUseWeight

        newExpirationDateEditText.inputType = InputType.TYPE_CLASS_NUMBER
        newExpirationDateEditText.addTextChangedListener(object : TextWatcher {
            private var previousText: String = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Store the text before any change is applied.
                previousText = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val currentText = s.toString()
                val deleting = currentText.length < previousText.length

                // Enforce a strict limit of 10 characters for the entire input, including dashes.
                if (currentText.length > 10) {
                    // If the user attempts to input more than 10 characters, revert to the previous valid input.
                    newExpirationDateEditText.setText(previousText)
                    newExpirationDateEditText.setSelection(newExpirationDateEditText.text.length)
                    return
                }

                if (!deleting) {
                    // Logic to automatically insert dashes as the user types, ensuring the format MM-DD-YYYY.
                    when (currentText.length) {
                        2 -> {
                            if (!previousText.endsWith("-")) {
                                // Add a dash after the month part automatically if the user hasn't typed it.
                                newExpirationDateEditText.setText("$currentText-")
                                newExpirationDateEditText.setSelection(newExpirationDateEditText.text.length)
                            }
                        }
                        5 -> {
                            if (!previousText.endsWith("-")) {
                                // Add a dash after the day part automatically.
                                newExpirationDateEditText.setText("$currentText-")
                                newExpirationDateEditText.setSelection(newExpirationDateEditText.text.length)
                            }
                        }
                    }
                } else {
                    // Handle the case where a user deletes text, including any dashes.
                    if ((currentText.length == 2 || currentText.length == 5) && previousText.endsWith("-")) {
                        // Remove the last character if it's a dash, maintaining proper date format as the user deletes characters.
                        newExpirationDateEditText.setText(currentText.dropLast(1))
                        newExpirationDateEditText.setSelection(newExpirationDateEditText.text.length)
                    }
                }
            }
        })

        // Setting onClickListeners
        addButton.setOnClickListener {
            val areNecessaryFieldsFilled = verifyIfAllNecessaryFieldsAreFilled()
            if (areNecessaryFieldsFilled) {
                val expirationDate = newExpirationDateEditText.text.toString()

                val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
                try {
                    val newExpirationDate = dateFormat.parse(expirationDate)

                    // Validate the parsed date
                    if (newExpirationDate != null && DateUtils.isValidDate(expirationDate)) {
                        val handleYesPressed: () -> Unit = {
                            // Date is valid and not expired, proceed to validate quantity
                            val itemNumber = itemNumberTextView.text.toString()
                            val quantityEntered = quantityEditText.text.toString().toInt()

                            progressDialog.show()
                            viewModel.validateQuantityAgainstPreReceiving(itemNumber, quantityEntered)
                        }
                        val isDateExpired = DateUtils.isDateExpired(expirationDate, handleYesPressed, requireContext(), requireView())
                        if (!isDateExpired) {
                            // Date is valid and not expired, proceed to validate quantity
                            val itemNumber = itemNumberTextView.text.toString()
                            val quantityEntered = quantityEditText.text.toString().toInt()

                            progressDialog.show()
                            viewModel.validateQuantityAgainstPreReceiving(itemNumber, quantityEntered)
                        }
                    } else {
                        // Show an error message for an invalid date
                        AlerterUtils.startErrorAlerter(
                            requireActivity(),
                            "Invalid date. Please enter a valid date (MM-DD-YYYY)."
                        )
                    }
                } catch (e: ParseException) {
                    AlerterUtils.startErrorAlerter(
                        requireActivity(),
                        "Invalid date format. Please use MM-DD-YYYY."
                    )
                }
            } else {
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    "Please fill all required fields, excluding lot number."
                )
            }
        }
    }

    private fun moveItemToDoorBin(expirationDate: String) {
        val newLotNumber = newLotEditText.text.toString()
        val quantityToAddToDoor =
            quantityEditText.text.toString().toFloat().toInt()
        val itemNumber = itemNumberTextView.text.toString()
        val doorBin = viewModel.currentlyChosenDoorBin.value!!.bin_number
        val weight = weightEditText.text.toString().toFloatOrNull() ?: 0f

        val itemToAdd = ItemToAddToDoorBin(
            expirationDate,
            "00P111",
            doorBin,
            itemNumber,
            newLotNumber,
            weight,
            quantityToAddToDoor
        )

        progressDialog.show()
        viewModel.moveItemToDoor(itemToAdd)
    }

    private fun initObservers() {
        viewModel.isQuantityLessThanPreReceiving.observe(viewLifecycleOwner) { isLessThan ->
            if (viewModel.hasAPIBeenCalled.value == true) {
                progressDialog.dismiss()
                if (isLessThan == true) {
                    // Entered quantity is less than Pre-Receiving quantity, show confirmation popup
                    val errorMsg = viewModel.validateQuantityErrorMessage.value
                        ?: "The quantity entered is less than the Pre-Receiving quantity. Do you want to proceed?"

                    val questionPopup = PopupWindowUtils.createQuestionPopup(
                        requireContext(),
                        errorMsg,
                        "Quantity Mismatch"
                    )

                    questionPopup.contentView.findViewById<Button>(R.id.YesButton).setOnClickListener {
                        // User chooses to proceed
                        questionPopup.dismiss()
                        val expirationDate = newExpirationDateEditText.text.toString()
                        moveItemToDoorBin(expirationDate)
                    }

                    questionPopup.contentView.findViewById<Button>(R.id.NoButton).setOnClickListener {
                        // User chooses not to proceed
                        questionPopup.dismiss()
                        // Do nothing
                    }

                    questionPopup.showAtLocation(requireView(), Gravity.CENTER, 0, 0)
                } else if (isLessThan == false) {
                    // Quantities match or entered quantity is more than Pre-Receiving quantity
                    val expirationDate = newExpirationDateEditText.text.toString()
                    moveItemToDoorBin(expirationDate)
                } else {
                    // Handle any errors
                    AlerterUtils.startErrorAlerter(
                        requireActivity(),
                        viewModel.validateQuantityErrorMessage.value ?: "Unknown error"
                    )
                }
            }
        }

        viewModel.isQuantityDifferentFromPreReceiving.observe(viewLifecycleOwner) { isDifferent ->
            if (viewModel.hasAPIBeenCalled.value == true) {
                progressDialog.dismiss()
                if (isDifferent == true) {
                    // Quantities are different, show confirmation popup
                    val errorMsg = viewModel.validateQuantityErrorMessage.value
                        ?: "The quantity entered differs from the Pre-Receiving quantity. Do you want to proceed?"

                    val questionPopup = PopupWindowUtils.createQuestionPopup(
                        requireContext(),
                        errorMsg,
                        "Quantity Mismatch"
                    )

                    questionPopup.contentView.findViewById<Button>(R.id.YesButton).setOnClickListener {
                        // User chooses to proceed
                        questionPopup.dismiss()
                        val expirationDate = newExpirationDateEditText.text.toString()
                        moveItemToDoorBin(expirationDate)
                    }

                    questionPopup.contentView.findViewById<Button>(R.id.NoButton).setOnClickListener {
                        // User chooses not to proceed
                        questionPopup.dismiss()
                        // Do nothing
                    }

                    questionPopup.showAtLocation(requireView(), Gravity.CENTER, 0, 0)
                } else if (isDifferent == false) {
                    // Quantities match, proceed directly
                    val expirationDate = newExpirationDateEditText.text.toString()
                    moveItemToDoorBin(expirationDate)
                } else {
                    // Handle any errors
                    AlerterUtils.startErrorAlerter(
                        requireActivity(),
                        viewModel.validateQuantityErrorMessage.value ?: "Unknown error"
                    )
                }
                viewModel.resetHasAPIBeenCalled()
            }
        }

        viewModel.wasItemConfirmed.observe(viewLifecycleOwner) { wasItemConfirmed ->
            if (wasItemConfirmed && viewModel.hasAPIBeenCalled.value == true) {
                progressDialog.dismiss()

                val quantity: Float = viewModel.UOMQtyInBarcode.value ?: 0f
                var decimalQuantityInEditText: Float = quantityEditText.text.toString().toFloatOrNull() ?: 0f

                quantityEditText.setText((decimalQuantityInEditText + if (quantity > 0) quantity else 1f).toString())

                val weight: Float = viewModel.weightFromBarcode.value ?: 0f
                var currentWeight: Float = weightEditText.text.toString().toFloatOrNull() ?: 0f

                if (weight > 0) {
                    weightEditText.setText((currentWeight + weight).toString())
                }

                viewModel.resetHasAPIBeenCalled()
            } else if (viewModel.hasAPIBeenCalled.value == true) {
                progressDialog.dismiss()
                viewModel.resetHasAPIBeenCalled()
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    viewModel.errorMessage.value?.get("confirmItem") ?: "Unknown error"
                )
            }
            scanItemEditText.setText("")
        }

        viewModel.wasItemMovedToDoor.observe(viewLifecycleOwner) { wasItemMovedToDoor ->
            if (viewModel.hasAPIBeenCalled.value == true) {
                progressDialog.dismiss()
                if (wasItemMovedToDoor == true) {
                    AlerterUtils.startSuccessAlert(
                        requireActivity(),
                        "Success",
                        "Item was added successfully to door bin"
                    )
                    findNavController().navigateUp()
                } else {
                    AlerterUtils.startErrorAlerter(
                        requireActivity(),
                        viewModel.errorMessage.value?.get("wasItemMovedToDoorError") ?: "Unknown error"
                    )
                }
                viewModel.resetHasAPIBeenCalled() // Reset here
            }
        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLastAPICallSuccessful ->
            if (wasLastAPICallSuccessful == false && viewModel.hasAPIBeenCalled.value == true) {
                viewModel.resetHasAPIBeenCalled()
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity(), viewModel.networkErrorMessage.value!!)
            }
        }
    }

    private fun populateEditTextWithViewModelValues() {
        val adapterPositionInMainFragment = viewModel.currentlyChosenAdapterPosition.value!!
        val expirationDate = viewModel.listOfItemsToMoveInPreReceiving.value!![adapterPositionInMainFragment].expirationDate
        val lotNumber = viewModel.listOfItemsToMoveInPreReceiving.value!![adapterPositionInMainFragment].lotNumber
        val quantityBeingMoved = viewModel.listOfItemsToMoveInPreReceiving.value!![adapterPositionInMainFragment].qtyOnHand.toString()
        val weight = viewModel.listOfItemsToMoveInPreReceiving.value!![adapterPositionInMainFragment].weight

        newExpirationDateEditText.setText(expirationDate)
        newLotEditText.setText(lotNumber)
        quantityEditText.setText(quantityBeingMoved)
        weightEditText.setText(weight.toString())
    }

    private fun populateUpperDivUiElements() {
        itemNameTextView.text = viewModel.itemInfo.value!!.itemDescription
        itemNumberTextView.text = viewModel.itemInfo.value!!.itemNumber
    }

    private fun verifyIfAllNecessaryFieldsAreFilled(): Boolean {
        val doesItemHaveWeight = viewModel.itemInfo.value!!.doesItemHaveWeight
        return quantityEditText.text.isNotEmpty() && newExpirationDateEditText.text.isNotEmpty() && ((doesItemHaveWeight && weightEditText.text.isNotEmpty()) || !doesItemHaveWeight)
    }
}
