package com.scannerapp.cdihandheldscannerviewactivity.ItemPicking.ItemPickDetails
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
import com.scannerapp.cdihandheldscannerviewactivity.ItemPicking.ItemPickingViewModel
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.scannerapp.cdihandheldscannerviewactivity.databinding.ItemPickingItemDetailsFragmentBinding

class OrderPickingItemFragment : Fragment() {
    private lateinit var binding: ItemPickingItemDetailsFragmentBinding
    private val viewModel: ItemPickingViewModel by activityViewModels()

    // View Variable declarations
    private lateinit var itemNumberEditText: EditText
    private lateinit var pickItemButton: Button
    private lateinit var itemAmountEditText: EditText
    private lateinit var itemNameTextView: TextView
    private lateinit var itemNumberTextView: TextView
    private lateinit var binNumberTextView: TextView
    private lateinit var weightEditText: EditText
    private lateinit var totalQuantityToBePickedTextView: TextView

    private var hasPageJustStarted: Boolean = false
    private lateinit var progressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.item_picking_item_details_fragment, container, false)
        initUIElements()
        setUIValues()
        initObservers()

        val doesItemHaveInvalidLineUp = viewModel.currentlyChosenItem.value!!.hasInvalidLineUp
        if (doesItemHaveInvalidLineUp)
            AlerterUtils.startErrorAlerter(
                requireActivity(),
                "The selected item cannot be processed because it is either not physically present in this warehouse (Line up W) or has been discontinued (Line up D). \n" +
                        "Please check the item details and choose a different item for picking."
            )

        return binding.root
    }

    private fun initUIElements() {
        itemNumberEditText = binding.itemNumberEditText

/*      ============================ This code will be left in comments in the case that it needs to be brought back ================
        // Disable manual typing in itemNumberEditText
        //itemNumberEditText.inputType = InputType.TYPE_CLASS_TEXT

//        itemNumberEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                val barcode = s.toString().trim()
//                if (barcode.isNotEmpty()) {
//                    hasPageJustStarted = true
//                    viewModel.confirmItemInBackend(barcode)
//                    itemNumberEditText.text.clear()
//                }
//            }
//        })
*/
        itemNumberEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                // Your code to execute when Enter is pressed
                    val barcode = itemNumberEditText.text.toString().trim()
                    if (barcode.isNotEmpty()) {
                        hasPageJustStarted = true
                        progressDialog.show()
                        viewModel.confirmItemInBackend(barcode)
                        itemNumberEditText.text.clear()
                    }
                true  // Return true if you have handled the action
            } else {
                false  // Return false if you haven't handled the action
            }
        }


        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        pickItemButton = binding.pickingButton
        val isLineUpInvalid = viewModel.currentlyChosenItem.value!!.hasInvalidLineUp
        pickItemButton.isEnabled = !isLineUpInvalid
        if (isLineUpInvalid)
            pickItemButton.text = "Invalid"

        pickItemButton.setOnClickListener {
            hasPageJustStarted = true
            val amountToBePickedString = itemAmountEditText.text.toString()
            val weightToBePickedString = weightEditText.text.toString()
            val doesItemHaveWeight = viewModel.currentlyChosenItem.value!!.doesItemHaveWeight
            if (amountToBePickedString.isEmpty() || (doesItemHaveWeight && weightToBePickedString.isEmpty()))
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    "There are missing fields! Please make sure all necessary fields are filled"
                )
            else {
                val amountToBePicked = amountToBePickedString.toFloat()
                val weightToBePicked = weightToBePickedString.toFloat()
                progressDialog.show()
                viewModel.finishPickingForSingleItem(amountToBePicked, weightToBePicked)
                viewModel.updatePickingTimer("update")
            }
        }

        itemAmountEditText = binding.itemAmountEditText
        itemNameTextView = binding.ItemName
        itemNumberTextView = binding.itemNumberTextView
        binNumberTextView = binding.binLocationText
        totalQuantityToBePickedTextView = binding.totalQuantityOfItemsToBePickedText
        weightEditText = binding.WeightEditText
        val doesItemHaveWeight = viewModel.currentlyChosenItem.value!!.doesItemHaveWeight
        weightEditText.isEnabled = doesItemHaveWeight
    }

    private fun initObservers() {
        viewModel.wasItemConfirmed.observe(viewLifecycleOwner) { wasItemConfirmed ->
            if (wasItemConfirmed && hasPageJustStarted) {
                // Handle Qty uom from barcode
                if(itemAmountEditText.text.toString().isEmpty())
                    itemAmountEditText.setText("0")
                val valueToBeDisplayed: Float = if (viewModel.UOMQtyInBarcode.value == 0.0f)
                    itemAmountEditText.text.toString().toFloat() + viewModel.currentlyChosenItem.value!!.howManyIndividualQtysPerUOM
                else
                    itemAmountEditText.text.toString().toFloat() + viewModel.UOMQtyInBarcode.value!!

                // Handle weight from baracode
                val doesItemHaveWeight = viewModel.currentlyChosenItem.value!!.doesItemHaveWeight
                val weightString = weightEditText.text.toString()

                val weightToBeDisplayed: Float = if (viewModel.weightInBarcode.value == 0.0f)
                    weightString.toFloat()
                else
                    weightString.toFloat() + viewModel.weightInBarcode.value!!


                weightEditText.setText(weightToBeDisplayed.toString())
                itemAmountEditText.setText(valueToBeDisplayed.toInt().toString())
                itemAmountEditText.isEnabled = true

                itemNumberEditText.setText("")
                itemNumberEditText.requestFocus()
                progressDialog.dismiss()

            } else if (hasPageJustStarted) {
                progressDialog.dismiss()
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    viewModel.errorMessage.value!!["confirmItem"]!!
                )
            }
        }

        viewModel.wasPickingSuccesfulyFinished.observe(viewLifecycleOwner) { wasPickingDoneSuccessfully ->
            if (wasPickingDoneSuccessfully && hasPageJustStarted) {
                progressDialog.dismiss()
                AlerterUtils.startSuccessAlert(
                    requireActivity(),
                    "Picking Successful",
                    "Item '" + viewModel.currentlyChosenItem.value!!.itemName + "' was successfully picked"
                )
                findNavController().navigateUp()
            } else if (hasPageJustStarted) {
                progressDialog.dismiss()
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    viewModel.errorMessage.value!!["finishPickingForSingleItem"]!!
                )
            }
        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLastAPICallSuccessful ->
            if (!wasLastAPICallSuccessful && hasPageJustStarted) {
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity(), viewModel.networkErrorMessage.value!!)
            }
        }
    }

    private fun setUIValues() {
        itemNameTextView.text = viewModel.currentlyChosenItem.value!!.itemName
        itemNumberTextView.text = viewModel.currentlyChosenItem.value!!.itemNumber
        binNumberTextView.text = viewModel.currentlyChosenItem.value!!.binLocation
        itemAmountEditText.setText(viewModel.currentlyChosenItem.value!!.quantityPicked.toInt().toString())
        weightEditText.setText(viewModel.currentlyChosenItem.value!!.weightPicked.toString())
        totalQuantityToBePickedTextView.text = viewModel.currentlyChosenItem.value!!.totalQuantityToBePicked.toInt().toString()
    }

    override fun onResume() {
        super.onResume()
        itemNumberEditText.requestFocus()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
