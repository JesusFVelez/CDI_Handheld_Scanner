package com.scannerapp.cdihandheldscannerviewactivity.ReceivingProductsIntoWarehouse

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
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
import java.util.Date
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
        val binToBeMovedTo:String,
        val doorBin: String,
        val itemNumber:String,
        val lotNumber:String,
        val weight: Float,
        val quantityOfItemsAddedToDoorBin:Int)


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
        scanItemEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                // Handle the Enter key press here
                progressDialog.show()
                viewModel.confirmItem(scanItemEditText.text.toString())
                true
            } else {
                false
            }
        }
        weightEditText = binding.WeightEditText

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

                    // Step 3: Validate the parsed date
                    if (newExpirationDate != null && DateUtils.isValidDate(expirationDate)) {
                        val newLotNumber = newLotEditText.text.toString()
                        val quantityToAddToDoor = quantityEditText.text.toString().toFloat().toInt()
                        val itemNumber = itemNumberTextView.text.toString()
                        val itemName = itemNameTextView.text.toString()
                        val doorBin = viewModel.currentlyChosenDoorBin.value!!.bin_number
                        val doesItemUseLotNumber = viewModel.itemInfo.value!!.doesItemUseLotNumber

                        var weight = 0f
                        if (weightEditText.text.isNotEmpty())
                            weight = weightEditText.text.toString().toFloat()

                        val itemToAdd = ItemToAddToDoorBin(expirationDate, "00P111", doorBin, itemNumber, newLotNumber, weight, quantityToAddToDoor)
                        progressDialog.show()
                        viewModel.moveItemToDoor(itemToAdd)

                        // Check if the new expiration date is before the current date
                        if (newExpirationDate.before(Date())) {
                            AlerterUtils.startWarningAlerter(requireActivity(), "Item has been added with an expired date!")
                        }
                    } else {
                        // Step 4: Show an error message for invalid date
                        AlerterUtils.startErrorAlerter(requireActivity(), "Invalid date. Please enter a valid date (MM-DD-YYYY).")
                    }
                } catch (e: ParseException) {
                    AlerterUtils.startErrorAlerter(requireActivity(), "Invalid date format. Please use MM-DD-YYYY.")
                }
            } else {
                AlerterUtils.startErrorAlerter(requireActivity(), "Please fill all required fields, excluding lot number.")
            }
        }
    }

    private fun initObservers() {
        viewModel.wasItemConfirmed.observe(viewLifecycleOwner) { wasItemConfirmed ->
            if (wasItemConfirmed && viewModel.hasAPIBeenCalled.value!!) {
                progressDialog.dismiss()

                val quantity = viewModel.UOMQtyInBarcode.value!!
                var decimalQuantityInEditText = 0f
                if (quantityEditText.text.isNotEmpty())
                    decimalQuantityInEditText = quantityEditText.text.toString().toFloat()

                if (quantity > 0)
                    quantityEditText.setText((decimalQuantityInEditText + quantity).toString())
                else
                    quantityEditText.setText((decimalQuantityInEditText + 1).toString())

                val weight = viewModel.weightFromBarcode.value!!
                var currentWeight = 0f
                if (weightEditText.text.isNotEmpty())
                    currentWeight = weightEditText.text.toString().toFloat()
                if (weight > 0)
                    weightEditText.setText((currentWeight + weight).toString())

                viewModel.resetHasAPIBeenCalled()
            } else if (viewModel.hasAPIBeenCalled.value!!) {
                progressDialog.dismiss()
                viewModel.resetHasAPIBeenCalled()
                AlerterUtils.startErrorAlerter(requireActivity(), viewModel.errorMessage.value!!["confirmItem"]!!)
            }
            scanItemEditText.setText("")
        }

        viewModel.wasItemMovedToDoor.observe(viewLifecycleOwner) { wasItemMovedToDoor ->
            progressDialog.dismiss()
            if (wasItemMovedToDoor && viewModel.hasAPIBeenCalled.value!!) {
                viewModel.resetHasAPIBeenCalled()
                AlerterUtils.startSuccessAlert(requireActivity(), "Success", "Item was added successfully to door bin")
                findNavController().navigateUp()
            } else if (viewModel.hasAPIBeenCalled.value!!) {
                viewModel.resetHasAPIBeenCalled()
                AlerterUtils.startErrorAlerter(requireActivity(), viewModel.errorMessage.value!!["wasItemMovedToDoorError"]!!)
            }
            viewModel.resetHasAPIBeenCalled()
        }

        viewModel.wasLasAPICallSuccessful.observe(viewLifecycleOwner) { wasLastAPICallSuccessful ->
            if (!wasLastAPICallSuccessful && viewModel.hasAPIBeenCalled.value!!) {
                viewModel.resetHasAPIBeenCalled()
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
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
