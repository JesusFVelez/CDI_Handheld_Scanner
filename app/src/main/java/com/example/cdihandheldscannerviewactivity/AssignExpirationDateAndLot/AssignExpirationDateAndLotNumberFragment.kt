package com.example.cdihandheldscannerviewactivity.AssignExpirationDateAndLot

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.example.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.example.cdihandheldscannerviewactivity.databinding.FragmentAssignExpirationDateAndLotNumberBinding


class AssignExpirationDateAndLotNumberFragment : Fragment() {
    private lateinit var binding: FragmentAssignExpirationDateAndLotNumberBinding

    private lateinit var itemNumberTextView: TextView
    private lateinit var itemNameTextView: TextView
    private lateinit var expirationDateTextView: TextView
    private lateinit var binLocationTextView: TextView
    private lateinit var BinNumberEditText: EditText
    private lateinit var itemNumberEditText: EditText
    private lateinit var NewExpirationDateEditText: EditText
    private lateinit var enterButton: Button

    /*Batch variables*/
    private lateinit var ToggleButton: Button
    private lateinit var newBatchEditText: EditText
    private lateinit var batchTextView: TextView

    private var shouldShowMessage = true

    private val viewModel: AssignExpirationDateAndLotNumberViewModel by activityViewModels()


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
            val itemNumber = viewModel.itemInfo.value!![0].itemNumber
            val newExpirationDate = binding.NewExpirationDateEditText.text.toString()
            val binNumber = viewModel.itemInfo.value!![0].binLocation

            // Use extracted String values for checks and ViewModel operations
            if (itemNumber.isNotBlank() && newExpirationDate.isNotBlank() && binNumber.isNotBlank()) {
                viewModel.assignExpirationDate(itemNumber, binNumber, newExpirationDate)
                viewModel.getItemInfo(itemNumber, binNumber)
            } else {
                AlerterUtils.startErrorAlerter(requireActivity(), "Make sure everything is filled")
            }
        }

    }


    private fun observeViewModel() {
        viewModel.opSuccess.observe(viewLifecycleOwner) {success->
                if (viewModel.opMessage.value!!.isNotBlank() && !shouldShowMessage && !success) {
                    AlerterUtils.startWarningAlerter(requireActivity(), viewModel.opMessage.value!!)
                }else if (viewModel.opMessage.value!!.isNotBlank() && !shouldShowMessage && success){
                    AlerterUtils.startSuccessAlert(requireActivity(),"", viewModel.opMessage.value!!)
                }
                else{}
        }

        viewModel.itemInfo.observe(viewLifecycleOwner) { items ->
            // Check if the list is not empty
            if (items.isNotEmpty()) {
                val firstItem = items.first()
                binding.apply {
                    itemNumberTextView.text = firstItem.itemNumber
                    itemNameTextView.text = firstItem.itemDescription
                    expirationDateTextView.text = firstItem.expireDate
                    binLocationTextView.text = firstItem.binLocation
                    binding.lotTextView.text = firstItem.lotNumber ?: "N/A" // Ensure this TextView exists and is correctly bound

                    //To make upperDiv visible
                    upperDiv.visibility = View.VISIBLE
                }
            }
        }


        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLasAPICallSuccessful ->
            if (!wasLasAPICallSuccessful) {
                AlerterUtils.startErrorAlerter(requireActivity(), "There was an error with last operation. Try again.")
            }
        }
    }
}

