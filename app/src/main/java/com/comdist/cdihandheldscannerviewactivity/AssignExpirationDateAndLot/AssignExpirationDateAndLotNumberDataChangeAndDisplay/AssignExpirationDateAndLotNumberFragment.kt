package com.comdist.cdihandheldscannerviewactivity.AssignExpirationDateAndLot.AssignExpirationDateAndLotNumberDataChangeAndDisplay

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.comdist.cdihandheldscannerviewactivity.AssignExpirationDateAndLot.AssignExpirationDateAndLotNumberViewModel
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.comdist.cdihandheldscannerviewactivity.databinding.FragmentAssignExpirationDateAndLotNumberBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class AssignExpirationDateAndLotNumberFragment : Fragment() {
    private lateinit var binding: FragmentAssignExpirationDateAndLotNumberBinding


    /*Batch variables*/
    private lateinit var ToggleButton: Button
    private var shouldShowMessage = true
    private var hasAPIBeenCalled = false
    private var isEnterPressed = false
    private val viewModel: AssignExpirationDateAndLotNumberViewModel by activityViewModels()
    private lateinit var progressDialog: Dialog

    override fun onCreate(saveInstance: Bundle?) {
        super.onCreate(saveInstance)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_assign_expiration_date_and_lot_number,
            container,
            false
        )

        setupUI()
        initObservers()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val bundle = arguments
        val lastFragmentName: String = BundleUtils.getPastFragmentNameFromBundle(bundle)
        if(lastFragmentName == "SearchExpirationDateAndLotNumberFragment") {
            binding.upperDiv.visibility = View.GONE
            shouldShowMessage = false
            bundle?.clear()
        }else{
           shouldShowMessage = true
        }
    }

    override fun onPause() {
        super.onPause()
        shouldShowMessage = false
    }

    private fun setupUI() {
        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        ToggleButton = binding.ToggleButton
        // Set initial toggle button state to "off"
        ToggleButton = binding.ToggleButton.apply {
            background = resources.getDrawable(R.drawable.toggle_switch_outline_off, null) // Use app theme's context for compatibility
            tag = "off" // Use tag to track the state of the toggle button
        }

        // Initially disable New Lot EditText since toggle is off
        binding.newLotEditText.isEnabled = false

        // Toggle Button click listener
        ToggleButton.setOnClickListener {
            if (it.tag == "off") {
                // If the button is currently off, turn it on
                it.background = resources.getDrawable(R.drawable.toggle_switch_on, null) // Use app theme's context for compatibility
                it.tag = "on"
                // Enable the New Lot EditText
                binding.newLotEditText.isEnabled = true
            } else {
                // If the button is currently on, turn it off
                it.background = resources.getDrawable(R.drawable.toggle_switch_outline_off, null) // Use app theme's context for compatibility
                it.tag = "off"
                // Disable the New Lot EditText and clear any text
                binding.newLotEditText.apply {
                    isEnabled = false
                    text.clear()
                }
            }
        }

        // Existing setupUI logic for the enter button
        binding.enterButton.setOnClickListener {
            isEnterPressed = true
            shouldShowMessage = true //NEW
            val itemNumber = viewModel.currentlyChosenItemForSearch.value!!.itemNumber
            val newExpirationDateStr = binding.NewExpirationDateEditText.text.toString()
            val binNumber = viewModel.currentlyChosenItemForSearch.value!!.binLocation
            val lotNumber = binding.newLotEditText.text.toString()
            val oldLot = viewModel.currentlyChosenItemForSearch.value!!.lotNumber

            // Step 1: Validate date format
            val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
            val isValidDateFormat = isValidDate(newExpirationDateStr)

            // Step 2: Check if date format is valid
            if (isValidDateFormat) {
                // Proceed if the date format is valid
                val newExpirationDate = dateFormat.parse(newExpirationDateStr)

                // Step 3: Check if parsed date is valid
                if (newExpirationDate != null) {
                    progressDialog.show()
                    val warehouseNO = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
                    viewModel.setWarehouseNOFromSharedPref(warehouseNO)

                    val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
                    viewModel.setCompanyIDFromSharedPref(companyID)

                    if (newExpirationDateStr.isNotEmpty()) {
                        hasAPIBeenCalled = true
                        if (lotNumber.isNotEmpty()){
                            viewModel.assignLotNumber(itemNumber, warehouseNO, binNumber, lotNumber, companyID, oldLot)
                            viewModel.getItemInfo(itemNumber, binNumber, lotNumber)
                        }
                        viewModel.assignExpirationDate(itemNumber, binNumber, newExpirationDateStr, lotNumber, warehouseNO)
                        viewModel.getItemInfo(itemNumber, binNumber, lotNumber)
                    }
                    if (lotNumber.isEmpty()) {
                        AlerterUtils.startSuccessAlert(requireActivity(), "Success!", "Item information changed.")
                    }
                } else {
                    // If the parsed date is null, display an error message
                    AlerterUtils.startErrorAlerter(requireActivity(), "Invalid date format. Please use MM-DD-YYYY.")
                }
            } else {
                // If the date format is invalid, display an error message
                AlerterUtils.startErrorAlerter(requireActivity(), "Invalid date format. Please use MM-DD-YYYY.")
            }



    }

        // Add this line in your setupUI function
        binding.NewExpirationDateEditText.inputType = InputType.TYPE_CLASS_NUMBER
        /*binding.NewExpirationDateEditText.addTextChangedListener(object : TextWatcher {
            private var previousText: String = ""
            //private var lastCursorPosition: Int = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Store the text before any change is applied and the cursor position.
                previousText = s.toString()
                //lastCursorPosition = binding.NewExpirationDateEditText.selectionStart
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val currentText = s.toString()
                val deleting = currentText.length < previousText.length

                // Enforce a strict limit of 10 characters for the entire input, including dashes.
                if (currentText.length > 10) {
                    binding.NewExpirationDateEditText.setText(previousText)
                    //binding.NewExpirationDateEditText.setSelection(lastCursorPosition)
                    return
                }

                if (!deleting) {
                    // Automatically insert dashes as the user types.
                    when (currentText.length) {
                        2, 5 -> if (!previousText.endsWith("-")) {
                            binding.NewExpirationDateEditText.setText("$currentText-")
                            binding.NewExpirationDateEditText.setSelection(currentText.length + 1)
                        }
                    }
                } else {
                    // Remove the last character if it's a dash, maintaining proper date format as the user deletes characters.
                    if ((currentText.length == 2 || currentText.length == 5) && previousText.endsWith("-")) {
                        binding.NewExpirationDateEditText.setText(currentText.dropLast(1))
                        binding.NewExpirationDateEditText.setSelection(binding.NewExpirationDateEditText.text.length)
                    }/*else if (lastCursorPosition > 1 && previousText[lastCursorPosition - 1] == '-') {//Remove number before the dash using cursor position
                        val newPosition = lastCursorPosition - 2  // Position to remove the number before the dash.
                        val newText = StringBuilder(previousText).apply {
                            deleteCharAt(newPosition)  // Remove the number before the dash.
                        }.toString()
                        binding.NewExpirationDateEditText.setText(newText)
                        binding.NewExpirationDateEditText.setSelection(newPosition)
                    }*/
                }
            }
        })*/

        binding.NewExpirationDateEditText.addTextChangedListener(object : TextWatcher {
            private var previousText: String = ""
            //private var lastCursorPosition: Int = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Store the text before any change is applied and the cursor position.
                previousText = s.toString()
                //lastCursorPosition = binding.NewExpirationDateEditText.selectionStart
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val currentText = s.toString()
                val deleting = currentText.length < previousText.length

                // Enforce a strict limit of 10 characters for the entire input, including dashes.
                if (currentText.length > 10) {
                    binding.NewExpirationDateEditText.setText(previousText)
                    //binding.NewExpirationDateEditText.setSelection(lastCursorPosition)
                    return
                }

                if (!deleting) {
                    // Automatically insert dashes as the user types.
                    when (currentText.length) {
                        2, 5 -> if (!previousText.endsWith("-")) {
                            binding.NewExpirationDateEditText.setText("$currentText-")
                            binding.NewExpirationDateEditText.setSelection(currentText.length + 1)
                        }
                    }
                } else {
                    // Remove the last character if it's a dash, maintaining proper date format as the user deletes characters.
                    if ((currentText.length == 2 || currentText.length == 5) && previousText.endsWith("-")) {
                        binding.NewExpirationDateEditText.setText(currentText.dropLast(1))
                        binding.NewExpirationDateEditText.setSelection(binding.NewExpirationDateEditText.text.length)
                    }/*else if (lastCursorPosition > 1 && previousText[lastCursorPosition - 1] == '-') {//Remove number before the dash using cursor position
                        val newPosition = lastCursorPosition - 2  // Position to remove the number before the dash.
                        val newText = StringBuilder(previousText).apply {
                            deleteCharAt(newPosition)  // Remove the number before the dash.
                        }.toString()
                        binding.NewExpirationDateEditText.setText(newText)
                        binding.NewExpirationDateEditText.setSelection(newPosition)
                    }*/
                }
            }
        })
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

    private fun initObservers() {

        viewModel.opSuccess.observe(viewLifecycleOwner) { success ->
            // Always dismiss the progress dialog when the observer is triggered
            progressDialog.dismiss()
            // Ensure the message is fetched fresh when needed
            val message = viewModel.opMessage.value ?: "No message available"

            if (isEnterPressed && shouldShowMessage) {
                if (success ) {
                    // If the operation was successful, show a success alert
                    AlerterUtils.startSuccessAlert(requireActivity(), "Success!", "Item information changed.")
                } else if (!success) {
                    // If the operation failed, show an error alert
                    AlerterUtils.startErrorAlerter(requireActivity(), message)
                }

                // After handling, reset the enter button state
                isEnterPressed = false
            }
        }


        viewModel.itemInfo.observe(viewLifecycleOwner) { itemInfo ->
            itemInfo.firstOrNull()?.let { item ->
                // Update UI
                binding.itemNumberTextView.text = item.itemNumber
                binding.itemNameTextView.text = item.itemDescription
                binding.binLocationTextView.text = item.binLocation
                binding.expirationDateTextView.text = item.expireDate ?: "N/A"
                binding.lotTextView.text = item.lotNumber ?: "N/A"
            }
        }

        // Inside observeViewModel function, modify the observer for currentlyChosenItemForSearch
        viewModel.currentlyChosenItemForSearch.observe(viewLifecycleOwner) { selectedItem ->
            selectedItem?.let { item ->
                binding.apply {

                    itemNumberTextView.text = item.itemNumber
                    itemNameTextView.text = item.itemDescription
                    binLocationTextView.text = item.binLocation
                    expirationDateTextView.text = item.expireDate ?: "N/A"
                    lotTextView.text = item.lotNumber ?: "N/A"

                    // Parsing and formatting the date
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Adjust this format to match the incoming date format
                    val outputFormat = SimpleDateFormat("MM-dd-yyyy"


                        , Locale.getDefault())
                    val date: Date? = item.expireDate?.let { inputFormat.parse(it) }
                    val formattedDate = if (date != null) outputFormat.format(date) else ""

                    NewExpirationDateEditText.setText(formattedDate)
                    newLotEditText.setText(item.lotNumber ?: "")

                    upperDiv.visibility = View.VISIBLE

                    val lotExists = !item.lotNumber.isNullOrEmpty()
                    newLotEditText.isEnabled = lotExists
                    ToggleButton.apply {
                        if (lotExists) {
                            background = resources.getDrawable(R.drawable.toggle_switch_on, null)
                            tag = "on"
                        } else {
                            background = resources.getDrawable(R.drawable.toggle_switch_outline_off, null)
                            tag = "off"
                        }
                    }
                    upperDiv.visibility = View.VISIBLE
                }
            }
        }


        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLasAPICallSuccessful ->
            progressDialog.dismiss()
            if (!wasLasAPICallSuccessful && hasAPIBeenCalled) {
                //AlerterUtils.startErrorAlerter(requireActivity(), "There was an error with last operation. Try again.")
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }
        }
    }


}

