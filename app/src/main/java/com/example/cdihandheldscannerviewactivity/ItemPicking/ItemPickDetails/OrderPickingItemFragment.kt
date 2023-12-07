package com.example.cdihandheldscannerviewactivity.ItemPicking.ItemPickDetails
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
import com.example.cdihandheldscannerviewactivity.ItemPicking.ItemPickingViewModel
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.example.cdihandheldscannerviewactivity.Utils.Network.ItemsInOrderInfo
import com.example.cdihandheldscannerviewactivity.databinding.FragmentOrderPickingItemBinding

class OrderPickingItemFragment :Fragment(){
    private lateinit var binding: FragmentOrderPickingItemBinding
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_picking_item,container, false)
        initUIElements()
        setUIValues()
        initObservers()

        val currentlyChosenAdapterPosition = viewModel.currentlyChosenAdapterPosition.value!!
        hasPageJustStarted = true

        return binding.root
    }

    private fun initUIElements(){
        itemNumberEditText = binding.itemNumberEditText
        itemNumberEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                // Handle the Enter key press here
                viewModel.confirmItemInBackend(itemNumberEditText.text.toString())
                true
            } else {
                false
            }
        }
        pickItemButton = binding.pickingButton
        pickItemButton.setOnClickListener{
            viewModel.finishPickingForSingleItem(itemAmountEditText.text.toString().toFloat())
        }
        itemAmountEditText = binding.itemAmountEditText
        itemNameTextView = binding.ItemName
        itemNumberTextView = binding.itemNumber
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

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner){wasLastAPICallSuccessfull ->
            if(!wasLastAPICallSuccessfull && hasPageJustStarted){
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }

        }

    }

    // TODO - Me quede en que estaba probando el "Pick Item" button en este fragmet y me estaba saliendo un error de HTTP 500
    // Tengo que arreglar eso y ver como hacer que despues de que se confirme el picking que me tire para la pantalla de atras
    // para seguir pickeando mas items
    // TODO - falta tambien añadir los otro API calls incluyendo los de start picker timer y end picker timer y algunos otros que no
    // se han anadido a la pistola

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