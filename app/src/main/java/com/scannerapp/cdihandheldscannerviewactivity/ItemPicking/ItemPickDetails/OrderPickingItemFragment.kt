package com.scannerapp.cdihandheldscannerviewactivity.ItemPicking.ItemPickDetails
import android.os.Bundle
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
import com.scannerapp.cdihandheldscannerviewactivity.databinding.ItemPickingItemDetailsFragmentBinding

class OrderPickingItemFragment :Fragment(){
    private lateinit var binding: ItemPickingItemDetailsFragmentBinding
    private val viewModel: ItemPickingViewModel by activityViewModels()

    // View Variable declarations
    private lateinit var itemNumberEditText: EditText
    private lateinit var pickItemButton:Button
    private lateinit var itemAmountEditText: EditText
    private lateinit var itemNameTextView: TextView
    private lateinit var itemNumberTextView: TextView
    private lateinit var binNumberTextView: TextView
    private lateinit var totalQuantityToBePickedTextView: TextView

    private var hasPageJustStarted:Boolean = false



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.item_picking_item_details_fragment,container, false)
        initUIElements()
        setUIValues()
        initObservers()

        val currentlyChosenAdapterPosition = viewModel.currentlyChosenAdapterPosition.value!!


        return binding.root
    }

    private fun initUIElements(){
        itemNumberEditText = binding.itemNumberEditText
        itemNumberEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                // Handle the Enter key press here
                hasPageJustStarted = true
                viewModel.confirmItemInBackend(itemNumberEditText.text.toString())
                true
            } else {
                false
            }
        }
        pickItemButton = binding.pickingButton
        pickItemButton.setOnClickListener{
            hasPageJustStarted = true
            val amountToBePickedString = itemAmountEditText.text.toString()
            if(amountToBePickedString == "")
                AlerterUtils.startErrorAlerter(requireActivity(),"Quantity to be picked can not be empty!")
            else {
                val amountToBePicked = amountToBePickedString.toFloat()
                viewModel.finishPickingForSingleItem(amountToBePicked)
                viewModel.updatePickingTimer("update")
            }
        }
        itemAmountEditText = binding.itemAmountEditText
        itemNameTextView = binding.ItemName
        itemNumberTextView = binding.itemNumberTextView
        binNumberTextView = binding.binLocationText
        totalQuantityToBePickedTextView = binding.totalQuantityOfItemsToBePickedText
    }

    private fun initObservers(){
        viewModel.wasItemConfirmed.observe(viewLifecycleOwner){wasItemConfirmed ->
            if(wasItemConfirmed && hasPageJustStarted){
                var valueToBeDisplayed: Float = 0.0f
                valueToBeDisplayed = if(viewModel.UOMQtyInBarcode.value == 0.0f)
                    itemAmountEditText.text.toString().toFloat() + viewModel.currentlyChosenItem.value!!.howManyIndividualQtysPerUOM
                else
                    itemAmountEditText.text.toString().toFloat() + viewModel.UOMQtyInBarcode.value!!
                itemAmountEditText.setText(valueToBeDisplayed.toString())
                itemNumberEditText.setText("")
            }else if(hasPageJustStarted)
                AlerterUtils.startErrorAlerter(requireActivity(), viewModel.errorMessage.value!!["confirmItem"]!!)
        }

        viewModel.wasPickingSuccesfulyFinished.observe(viewLifecycleOwner){wasPickingDoneSuccessfully ->
            if(wasPickingDoneSuccessfully && hasPageJustStarted){
                AlerterUtils.startSuccessAlert(requireActivity(), "Picking Successful", "Item '" + viewModel.currentlyChosenItem.value!!.itemName + "' was successfully picked")
                findNavController().navigateUp()
            }else if(hasPageJustStarted)
                AlerterUtils.startErrorAlerter(requireActivity(), viewModel.errorMessage.value!!["finishPickingForSingleItem"]!!)
        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner){wasLastAPICallSuccessful ->
            if(!wasLastAPICallSuccessful && hasPageJustStarted){
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }

        }

    }


    private fun setUIValues(){

        itemNameTextView.text = viewModel.currentlyChosenItem.value!!.itemName
        itemNumberTextView.text = viewModel.currentlyChosenItem.value!!.itemNumber
        binNumberTextView.text = viewModel.currentlyChosenItem.value!!.binLocation
        itemAmountEditText.setText(viewModel.currentlyChosenItem.value!!.quantityPicked.toInt().toString())
        totalQuantityToBePickedTextView.text = viewModel.currentlyChosenItem.value!!.totalQuantityToBePicked.toInt().toString()
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}