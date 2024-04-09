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
import android.widget.EditText
import android.widget.TextView
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
        observeViewModel()

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
        shouldShowMessage = true
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
            // Extract string values from EditText fields correctly
            val itemNumber = viewModel.currentlyChosenItemForSearch.value!!.itemNumber
            val newExpirationDate = binding.NewExpirationDateEditText.text.toString()
            val binNumber = viewModel.currentlyChosenItemForSearch.value!!.binLocation
            val lotNumber = binding.newLotEditText.text.toString()

            progressDialog.show()
            val warehouseNO:Int = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
            viewModel.setWarehouseNOFromSharedPref(warehouseNO)

            val companyID:String = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
            viewModel.setCompanyIDFromSharedPref(companyID)


            // Use extracted String values for checks and ViewModel operations
            if (itemNumber.isNotBlank() && newExpirationDate.isNotBlank() && binNumber.isNotBlank()) {
                hasAPIBeenCalled = true
                viewModel.assignExpirationDate(itemNumber, binNumber, newExpirationDate, lotNumber)
                viewModel.assignLotNumber(itemNumber, warehouseNO, binNumber, lotNumber, companyID)
                viewModel.getItemInfo(itemNumber, binNumber, lotNumber)
            } else {
                progressDialog.dismiss()
                AlerterUtils.startErrorAlerter(requireActivity(), "Make sure date is filled")
            }

        }

        // Add this line in your setupUI function
        binding.NewExpirationDateEditText.inputType = InputType.TYPE_CLASS_NUMBER
        binding.NewExpirationDateEditText.addTextChangedListener(object : TextWatcher {
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
                    binding.NewExpirationDateEditText.setText(previousText)
                    return
                }

                if (!deleting) {
                    // Logic to automatically insert dashes as the user types, ensuring the format MM-DD-YYYY.
                    when (currentText.length) {
                        2 -> {
                            if (!previousText.endsWith("-")) {
                                // Add a dash after the month part automatically if the user hasn't typed it.
                                binding.NewExpirationDateEditText.setText("$currentText-")
                                binding.NewExpirationDateEditText.setSelection(binding.NewExpirationDateEditText.text.length)
                            }
                        }
                        5 -> {
                            if (!previousText.endsWith("-")) {
                                // Add a dash after the day part automatically.
                                binding.NewExpirationDateEditText.setText("$currentText-")
                                binding.NewExpirationDateEditText.setSelection(binding.NewExpirationDateEditText.text.length)
                            }
                        }
                    }
                } else {
                    // Handle the case where a user deletes text, including any dashes.
                    if ((currentText.length == 2 || currentText.length == 5) && previousText.endsWith("-")) {
                        // Remove the last character if it's a dash, maintaining proper date format as the user deletes characters.
                        binding.NewExpirationDateEditText.setText(currentText.dropLast(1))
                        binding.NewExpirationDateEditText.setSelection(binding.NewExpirationDateEditText.text.length)
                    }
                }
            }
        })

    }


    private fun observeViewModel() {

        viewModel.opSuccess.observe(viewLifecycleOwner) {success->
            progressDialog.dismiss()
            if (viewModel.opMessage.value!!.isNotBlank() && !shouldShowMessage && !success) {
                AlerterUtils.startWarningAlerter(requireActivity(), viewModel.opMessage.value!!)
            }else if (viewModel.opMessage.value!!.isNotBlank() && !shouldShowMessage && success){
                AlerterUtils.startSuccessAlert(requireActivity(),"", viewModel.opMessage.value!!)
            }
            else{}
        }


        viewModel.itemInfo.observe(viewLifecycleOwner) { itemInfo ->
            itemInfo.firstOrNull()?.let { item ->
                // Update UI
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
                    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
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

