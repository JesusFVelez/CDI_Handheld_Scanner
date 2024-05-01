package com.comdist.cdihandheldscannerviewactivity.ReceivingProductsIntoBin
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.comdist.cdihandheldscannerviewactivity.databinding.ReceivingDetailsFragmentBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class ReceivingProductsDetailsFragment : Fragment() {

    private lateinit var binding: ReceivingDetailsFragmentBinding

    private val viewModel: ReceivingProductsViewModel by activityViewModels()

    // TextView declarations
    private lateinit var itemNumberTextView: TextView
    private lateinit var itemNameTextView: TextView


    //EditText declarations
    private lateinit var newLotAutoCompleteTextView: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var newExpirationDateEditText: EditText

    // Button declarations
    private lateinit var addButton: Button

    //Loading declaration
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
        val lastFragmentName : String = BundleUtils.getPastFragmentNameFromBundle(bundle)
        if(lastFragmentName == "ReceivingProductsMainFragment" && viewModel.willEditCurrentValues.value!!){
            populateUpperDivUiElements()
            populateEditTextWithViewModelValues()
            viewModel.willNotEditCurrentValues()
            addButton.text = "Save"
        }
    }
    // Function to validate the date
    private fun isValidDate(dateStr: String): Boolean {
        val parts = dateStr.split("-")
        if (parts.size == 3) {
            val month = parts[0].toInt()
            val day = parts[1].toInt()
            val year = parts[2].toInt()

            if (month !in 1..12) return false

            val maxDays = when (month) {
                1, 3, 5, 7, 8, 10, 12 -> 31
                4, 6, 9, 11 -> 30
                2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
                else -> return false
            }

            if (day !in 1..maxDays) return false
        } else {
            return false
        }

        return true
    }

    private fun initUIElements(){
        // text view initializations
        itemNumberTextView = binding.itemNumberTextView
        itemNameTextView = binding.itemNameTextView
        populateUpperDivUiElements()

        // edit text initializations
        newLotAutoCompleteTextView = binding.newLotAutoCompleteTextView
        quantityEditText = binding.QuantityEditText
        newExpirationDateEditText = binding.NewExpirationDateEditText

        // button initializations
        addButton = binding.addButton

        // Loading initialization
        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())


        // Validates whether item uses lot number or not
        val canEditLotNumber = viewModel.itemInfo.value!!.doesItemUseLotNumber
        newLotAutoCompleteTextView.isEnabled = canEditLotNumber

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
            if(areNecessaryFieldsFilled){
                val expirationDate = newExpirationDateEditText.text.toString()

                val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
                try {
                    val newExpirationDate = dateFormat.parse(expirationDate)

                    // Step 3: Validate the parsed date
                    if (newExpirationDate != null && isValidDate(expirationDate)) {
                        val expirationDate = newExpirationDateEditText.text.toString()
                        val newLotNumber = newLotAutoCompleteTextView.text.toString()
                        val quantityToAddToDoor = quantityEditText.text.toString().toInt()
                        val itemNumber = itemNumberTextView.text.toString()
                        val itemName = itemNameTextView.text.toString()
                        val doorBin = viewModel.currentlyChosenDoorBin.value!!.bin_number
                        val doesItemUseLotNumber = viewModel.itemInfo.value!!.doesItemUseLotNumber
                        val itemToAdd = itemsInDoorBinAdapter.ItemInDoorBinDataClass(expirationDate, itemName, "9970F", doorBin , itemNumber, newLotNumber, quantityToAddToDoor, doesItemUseLotNumber ,"N/A")
                        progressDialog.show()
                        viewModel.moveItemToDoor(itemToAdd)
                    } else {
                        // Step 4: Show an error message for invalid date
                        AlerterUtils.startErrorAlerter(requireActivity(), "Invalid date. Please enter a valid date (MM-DD-YYYY).")
                    }
                } catch (e: ParseException) {
                    AlerterUtils.startErrorAlerter(requireActivity(), "Invalid date format. Please use MM-DD-YYYY.")
                }
            }else{
                AlerterUtils.startErrorAlerter(requireActivity(), "Please fill all required fields, excluding lot number")
            }
        }

    }

    private fun initObservers(){
        viewModel.wasItemMovedToDoor.observe(viewLifecycleOwner){wasItemMovedToDoor ->
            progressDialog.dismiss()
            if(wasItemMovedToDoor && viewModel.hasAPIBeenCalled.value!!){
                viewModel.resetHasAPIBeenCalled()
                AlerterUtils.startSuccessAlert(requireActivity(), "Success", "Item was added successfully to door bin")
                findNavController().navigateUp()
            }else if(viewModel.hasAPIBeenCalled.value!!){
                viewModel.resetHasAPIBeenCalled()
                AlerterUtils.startErrorAlerter(requireActivity(), viewModel.errorMessage.value!!["wasItemMovedToBinError"]!!)
            }
            viewModel.resetHasAPIBeenCalled()
        }

        viewModel.wasLasAPICallSuccessful.observe(viewLifecycleOwner){wasLastAPICallSuccessful ->
            if(!wasLastAPICallSuccessful && viewModel.hasAPIBeenCalled.value!!){
                viewModel.resetHasAPIBeenCalled()
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }
        }


    }

    private fun populateEditTextWithViewModelValues(){
        val adapterPositionInMainFragment = viewModel.currentlyChosenAdapterPosition.value!!
        val expirationDate = viewModel.listOfItemsToMoveInPreReceiving.value!![adapterPositionInMainFragment].expirationDate
        val lotNumber = viewModel.listOfItemsToMoveInPreReceiving.value!![adapterPositionInMainFragment].lotNumber
        val quantityBeingMoved = viewModel.listOfItemsToMoveInPreReceiving.value!![adapterPositionInMainFragment].quantityOfItemsAddedToDoorBin.toString()

        newExpirationDateEditText.setText(expirationDate)
        newLotAutoCompleteTextView.setText(lotNumber)
        quantityEditText.setText(quantityBeingMoved)
    }
    private fun populateUpperDivUiElements(){
        itemNameTextView.text = viewModel.itemInfo.value!!.itemDescription
        itemNumberTextView.text = viewModel.itemInfo.value!!.itemNumber
    }

    private fun verifyIfAllNecessaryFieldsAreFilled():Boolean{
        return quantityEditText.text.isNotEmpty() && newExpirationDateEditText.text.isNotEmpty()
    }

}