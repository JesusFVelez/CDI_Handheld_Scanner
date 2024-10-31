package com.scannerapp.cdihandheldscannerviewactivity.MovingProductsBetweenBins

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.itemsInBin
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.scannerapp.cdihandheldscannerviewactivity.databinding.BinMovementMainFragmentBinding


class BinMovementFragment : Fragment() {

    private lateinit var binding: BinMovementMainFragmentBinding

    private lateinit var addButton: FloatingActionButton
    private lateinit var itemsBeingMovedRecyclerView:RecyclerView
    private lateinit var finishButton: Button
    private lateinit var progressDialog: Dialog

    private lateinit var addBinMovementToListPopupWindow: PopupWindow

    private lateinit var adapter: BinMovementAdapter
    private lateinit var itemNumberDropdownAdapter:CustomItemDropDownAdapter

    private val viewModel: BinMovementViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    private fun fillItemNumberSpinnerWithItems( listOfItemsInBin:List<itemsInBin>){
        val itemNumberSpinner = addBinMovementToListPopupWindow.contentView.findViewById<AutoCompleteTextView>(R.id.itemNumberSpinner)
        itemNumberSpinner.setText(listOfItemsInBin[0].itemName)
        itemNumberDropdownAdapter = CustomItemDropDownAdapter(requireContext(), listOfItemsInBin)
        itemNumberSpinner.setAdapter(itemNumberDropdownAdapter)

    }

    private fun insertItemIntoItemSpinner( item:itemsInBin){
        val itemNumberSpinner = addBinMovementToListPopupWindow.contentView.findViewById<AutoCompleteTextView>(R.id.itemNumberSpinner)
        itemNumberSpinner.setText(item.itemName)
        itemNumberDropdownAdapter = CustomItemDropDownAdapter(requireContext(), listOf(item))
        itemNumberSpinner.setAdapter(itemNumberDropdownAdapter)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.bin_movement_main_fragment, container, false)

        initUIElements()
        initObservers()


        // Gets the company id from the Shared Preferences
        val companyID:String = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)

        // Gets the company id from the Shared Preferences
        val warehouseNumber: Int = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
        viewModel.setWarehouseFromSharedPref(warehouseNumber)

        // Gets the username from the Shared Preferences
        val pickerUserName: String = SharedPreferencesUtils.getUserNameFromSharedPref(requireContext())
        viewModel.setPickerUserName(pickerUserName)

        progressDialog.show()
        //getting all items from backend to fill the item number drop down
        viewModel.getAllItemsInAllBinsFromBackend()

        return binding.root
    }


    private fun initObservers(){

        viewModel.allItemsMoved.observe(viewLifecycleOwner) { allMoved ->
            progressDialog.dismiss()
            if (allMoved && viewModel.hasAPIBeenCalled.value!!) {
                // Reset the flag or handle as needed
                var wasAtLeastOneItemNotMoved: Boolean = false
                var wasAtLeastOneItemMoved: Boolean = false
                viewModel.itemsMoved.value!!.map { itemBeingMoved ->
                    if(itemBeingMoved.wasItemMoved)
                        wasAtLeastOneItemMoved = true
                    else{
                        wasAtLeastOneItemNotMoved = true
                        AlerterUtils.startErrorAlerter(requireActivity(), itemBeingMoved.errorMessage)
                    }
                }
                if (!wasAtLeastOneItemNotMoved && wasAtLeastOneItemMoved)
                    AlerterUtils.startSuccessAlert(requireActivity(), "Success","Items have been successfully moved.")
                else if(wasAtLeastOneItemNotMoved && wasAtLeastOneItemMoved)
                    AlerterUtils.startSuccessAlert(requireActivity(), "Success","Items have been successfully moved but some items failed to move.")
                viewModel.resetAllItemsMovedFlag()
                adapter.clearAllItems()
                progressDialog.show()
                viewModel.getAllItemsInAllBinsFromBackend()
            }
            viewModel.resetHasAPIBeenCalled()

        }

        viewModel.wasItemConfirmed.observe(viewLifecycleOwner){ wasItemConfirmed ->
            val itemNumberEditTextInPopupWindow = addBinMovementToListPopupWindow.contentView.findViewById<TextView>(R.id.scanItemTextView)
            if(!wasItemConfirmed && viewModel.hasAPIBeenCalled.value!!) {
                itemNumberEditTextInPopupWindow.error = viewModel.errorMessage.value!!["confirmItem"]!!
            }
            else if(viewModel.hasAPIBeenCalled.value!!){
                itemNumberEditTextInPopupWindow.error = null
                val listWithItemToPresentInSpinner = listOf(viewModel.currentlyChosenItemToMove.value!!)
                insertItemIntoItemSpinner(listWithItemToPresentInSpinner[0])
            }
            viewModel.resetHasAPIBeenCalled()
        }

        viewModel.wasBinConfirmed.observe(viewLifecycleOwner){ wasBinConfirmed ->
            if(!wasBinConfirmed && viewModel.hasAPIBeenCalled.value!!)
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    viewModel.errorMessage.value!!["confirmBin"]!!
                )
            else if(viewModel.hasAPIBeenCalled.value!!){
                finishAddingItemToList()
            }
            viewModel.resetHasAPIBeenCalled()
            viewModel.resetNewItemToBeMoved()
        }

//        viewModel.wasItemMovedSuccessfully.observe(viewLifecycleOwner){wasItemMoved ->
//            if (!wasItemMoved && viewModel.hasAPIBeenCalled.value!!) {
//                AlerterUtils.startErrorAlerter(
//                    requireActivity(),
//                    viewModel.errorMessage.value!!["moveItemBetweenBins"]!!
//                )
//            }
//        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner){wasLastAPICallSuccessful ->
            if(!wasLastAPICallSuccessful && viewModel.hasAPIBeenCalled.value!!){
                viewModel.resetHasAPIBeenCalled()
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity(), viewModel.networkErrorMessage.value!!)
            }
        }

        viewModel.listOfAllItemsInAllBins.observe(viewLifecycleOwner){newListOfItemsInBin ->
            progressDialog.dismiss()
            val fromBinNumber = addBinMovementToListPopupWindow.contentView.findViewById<AutoCompleteTextView>(R.id.fromBinNumber)
            if(fromBinNumber.text.toString().isNotEmpty()) {
                fillItemNumberSpinnerWithItems(newListOfItemsInBin)
            }
        }
    }

    private fun finishAddingItemToList() {
        val newItemToAdd = viewModel.newItemToBeMoved.value!!
        val positionOfItemToMove = viewModel.positionOfItemToMove.value!!
        if(positionOfItemToMove == -1 && !viewModel.willUpdateItemToMove.value!!) {
            adapter.addItem(newItemToAdd)
            resetBinMovementPopUp()
            addBinMovementToListPopupWindow.dismiss()
        }else if(viewModel.willUpdateItemToMove.value!!){
            viewModel.resetWillUpdateItemToMove()
            adapter.updateItem(viewModel.positionOfItemToMove.value!! ,newItemToAdd)
            viewModel.resetPositionOfItemToMove()
            resetBinMovementPopUp()
            addBinMovementToListPopupWindow.dismiss()
        }
    }

    fun filterItemsByBinLocation(binLocation: String): List<itemsInBin> {
        return viewModel.listOfAllItemsInAllBins.value!!.filter { it.binLocation.lowercase() == binLocation.lowercase() }
    }


    private fun initAddItemToListPopup(){
        addBinMovementToListPopupWindow = PopupWindowUtils.getCustomPopup(requireContext(),R.layout.bin_movement_add_new_item_popup,)
        val popupContentView = addBinMovementToListPopupWindow.contentView

        val itemNumberSpinner = popupContentView.findViewById<AutoCompleteTextView>(R.id.itemNumberSpinner)
        itemNumberSpinner.setOnItemClickListener{ parent, view, position, id ->
//            val selectedItem = itemNumberDropdownAdapter.getItem(position) as itemsInBin
//            viewModel.setCurrentlyChosenItemToMoveFromSpinner(selectedItem)
//            itemNumberSpinner.setText(selectedItem.itemName,false)
        }

        val fromBinAutoCompleteTextView = popupContentView.findViewById<AutoCompleteTextView>(R.id.fromBinNumber)

        fromBinAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Code to execute before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                // Code to execute as text is changing
//                val filteredList = filterItemsByBinLocation(s.toString())
//                fillItemNumberSpinnerWithItems(filteredList)
//                itemNumberSpinner.text.clear()
//                viewModel.resetNewItemToBeMoved()
            }

            override fun afterTextChanged(s: Editable?) {
                // Code to execute after text has changed
            }
        })

        val scanItemAutoCompleteTextView = popupContentView.findViewById<AutoCompleteTextView>(R.id.scanItemTextView)
        scanItemAutoCompleteTextView.inputType = InputType.TYPE_CLASS_TEXT
        scanItemAutoCompleteTextView.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val scannedBarcode = s.toString().trim()
                if(scannedBarcode.isNotEmpty()){
                    //TODO("Add confirm item method for confirming the barcode scanned")
                    if(fromBinAutoCompleteTextView.text.isEmpty())
                        fromBinAutoCompleteTextView.error = "Field cannot be left empty when scanning an item"
                    else
                        viewModel.confirmBarcodeAndValidateIfItemIsInBin(binLocation = fromBinAutoCompleteTextView.text.toString(), scannedCode = scannedBarcode)

                    scanItemAutoCompleteTextView.text.clear()
                }
            }

        })

        val toBinAutoCompleteTextView = popupContentView.findViewById<AutoCompleteTextView>(R.id.toBinNumber)

        val amountToMoveEditText = popupContentView.findViewById<EditText>(R.id.itemAmountEditText)

        val addButtonPopup = popupContentView.findViewById<Button>(R.id.addButton)
        addButtonPopup.setOnClickListener {
            val quantityToMoveStr = amountToMoveEditText.text.toString()
            val fromBin = fromBinAutoCompleteTextView.text.toString()
            val toBin = toBinAutoCompleteTextView.text.toString()
            val itemNumber = itemNumberSpinner.text.toString()

            // Clear previous errors
            amountToMoveEditText.error = null
            fromBinAutoCompleteTextView.error = null
            toBinAutoCompleteTextView.error = null
            itemNumberSpinner.error = null

            // Check for empty fields
            val isAtLeastOneEditTextEmpty = verifyIfAtLeastOneEditTextIsEmpty(fromBin, quantityToMoveStr, itemNumber)
            if (isAtLeastOneEditTextEmpty) {
                if (fromBin.isEmpty()) {
                    fromBinAutoCompleteTextView.error = "From Bin cannot be empty."
                }
                if (itemNumber.isEmpty()) {
                    itemNumberSpinner.error = "Item Number cannot be empty."
                }
                if (quantityToMoveStr.isEmpty()) {
                    amountToMoveEditText.error = "Item Amount cannot be empty."
                }
            } else if (fromBin.equals(toBin, ignoreCase = true)) {
                toBinAutoCompleteTextView.error = "Destination Bin cannot be the same as From Bin."
            } else {
                val quantityToMove = quantityToMoveStr.toInt()
                val quantityToBePicked = viewModel.currentlyChosenItemToMove.value!!.quantityInPicking.toInt()
                val quantityOnHand = viewModel.currentlyChosenItemToMove.value!!.quantityOnHand.toInt()
                val quantityAvailable = quantityOnHand - quantityToBePicked

                val itemName = viewModel.currentlyChosenItemToMove.value!!.itemName
                val rowIDOfItem = viewModel.currentlyChosenItemToMove.value!!.rowID

                // Calculate total quantity already being moved for this item and bin
                val isUpdatingItem = viewModel.willUpdateItemToMove.value!!
                val positionOfItemToUpdate = viewModel.positionOfItemToMove.value!!

                val totalQuantityAlreadyBeingMoved = adapter.data
                    .filterIndexed { index, item ->
                        (item.itemNumber == viewModel.currentlyChosenItemToMove.value!!.itemNumber &&
                                item.fromBinNumber.equals(fromBin, ignoreCase = true)) &&
                                (!isUpdatingItem || index != positionOfItemToUpdate)
                    }
                    .sumOf { it.qtyToMoveFromBinToBin }

                val adjustedQuantityAvailable = quantityAvailable - totalQuantityAlreadyBeingMoved

                if (quantityToMove > adjustedQuantityAvailable) {
                    amountToMoveEditText.error = "Cannot move $quantityToMove units, only $adjustedQuantityAvailable units available."
                } else {
                    val newItemToAdd = BinMovementDataClass(itemName, viewModel.currentlyChosenItemToMove.value!!.itemNumber, rowIDOfItem, quantityToMove, fromBin, toBin)
                    viewModel.setNewItemToBeMoved(newItemToAdd)
                    if (toBin.isNotEmpty())
                        viewModel.confirmIfBinExistsInDB(toBin)
                    else {
                        finishAddingItemToList()
                    }
                }
            }
        }
    }


    private fun initUIElements(){

        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        initAddItemToListPopup()
        addButton = binding.addButton
        addButton.imageTintList = ColorStateList.valueOf(Color.WHITE)
        addButton.setOnClickListener{
            resetBinMovementPopUp()
            addBinMovementToListPopupWindow.showAtLocation(requireView(), Gravity.CENTER, 0, 0)
        }

        finishButton = binding.finishButton
        finishButton.setOnClickListener {
            val questionPopup = PopupWindowUtils.createQuestionPopup(requireContext(), "Are you sure you would like to commit the movements?", "Move Items?")
            questionPopup.contentView.findViewById<Button>(R.id.YesButton).setOnClickListener{
                var isAtLeastOneToBinNotChosen = false
                adapter.data.map {item ->
                    if(item.toBinNumber.isEmpty())
                        isAtLeastOneToBinNotChosen = true
                }
                progressDialog.show()
                questionPopup.dismiss()
                if(isAtLeastOneToBinNotChosen){
                    progressDialog.dismiss()
                    AlerterUtils.startErrorAlerter(requireActivity(), "Please make sure all items have a destination bin before finishing.")
                }else {
                    adapter.data.map {itemToMove ->

                    }
                    viewModel.moveItemsBetweenBins(adapter.data)
                }
            }

            questionPopup.contentView.findViewById<Button>(R.id.NoButton).setOnClickListener{
                questionPopup.dismiss()
            }

            questionPopup.showAtLocation(requireView(), Gravity.CENTER, 0, 0)


        }

        val listener = object : ItemToMoveOnClickListener {
            override fun onItemClickListener(view: View, position: Int) {
                addBinMovementToListPopupWindow.showAtLocation(requireView(), Gravity.CENTER, 0, 0)
                val fromBinNumberEditText = addBinMovementToListPopupWindow.contentView.findViewById<AutoCompleteTextView>(R.id.fromBinNumber)
                val toBinNumberEditText = addBinMovementToListPopupWindow.contentView.findViewById<AutoCompleteTextView>(R.id.toBinNumber)
                val itemAmountEditText = addBinMovementToListPopupWindow.contentView.findViewById<EditText>(R.id.itemAmountEditText)
                val itemNumberSpinner = addBinMovementToListPopupWindow.contentView.findViewById<AutoCompleteTextView>(R.id.itemNumberSpinner)
                val item = adapter.data[position]
                fromBinNumberEditText.setText(item.fromBinNumber)
                toBinNumberEditText.setText(item.toBinNumber)
                itemAmountEditText.setText(item.qtyToMoveFromBinToBin.toString())
                itemNumberSpinner.setText(item.itemName)


                val filteredList = filterItemsByBinLocation(fromBinNumberEditText.text.toString())
                fillItemNumberSpinnerWithItems(filteredList)

                viewModel.setPositionOfItemToMove(position)
                viewModel.setWillUpdateItemToMove(true)

                val itemFromDropDown = itemNumberDropdownAdapter.getItemByRowID(item.rowIDOfItemInFromBin)
                if(itemFromDropDown != null)
                    viewModel.setCurrentlyChosenItemToMoveFromSpinner(itemFromDropDown)
                else
                    AlerterUtils.startErrorAlerter(requireActivity(), "There has been an error. Please contact CDI for further assistance.")


                val addButtonPopup = addBinMovementToListPopupWindow.contentView.findViewById<Button>(R.id.addButton)
                addButtonPopup.text = "Update"
            }
        }
        adapter = BinMovementAdapter ({ hasItems ->
            finishButton.isEnabled = hasItems
        }, listener )

        itemsBeingMovedRecyclerView = binding.itemsBeingMovedRecyclerView
        itemsBeingMovedRecyclerView.adapter = adapter

    }

    private fun verifyIfAtLeastOneEditTextIsEmpty(fromBinNumber:String, itemAmount:String, itemNumber:String):Boolean{
        return fromBinNumber.isEmpty() || itemAmount.isEmpty() || itemNumber.isEmpty()
    }

    private fun resetBinMovementPopUp(){
        addBinMovementToListPopupWindow.contentView.findViewById<AutoCompleteTextView>(R.id.fromBinNumber).text.clear()
        addBinMovementToListPopupWindow.contentView.findViewById<AutoCompleteTextView>(R.id.toBinNumber).text.clear()
        addBinMovementToListPopupWindow.contentView.findViewById<EditText>(R.id.itemAmountEditText).text.clear()
        addBinMovementToListPopupWindow.contentView.findViewById<AutoCompleteTextView>(R.id.itemNumberSpinner).text.clear()
        addBinMovementToListPopupWindow.contentView.findViewById<Button>(R.id.addButton).text = "Add"

    }


    class CustomItemDropDownAdapter(context: Context, private var suggestions: List<itemsInBin>): ArrayAdapter<itemsInBin>(context, 0, suggestions){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.bin_movement_items_in_bin_suggestion_view, parent, false)
            val itemDescriptionTextView = view.findViewById<TextView>(R.id.itemDescriptionTextView)
            val itemNumberTextView = view.findViewById<TextView>(R.id.itemNumberTextView)
            val binLocationTextView = view.findViewById<TextView>(R.id.binLocationValueTextView)
            val quantityOnHandTextView = view.findViewById<TextView>(R.id.QuantityInHandValueTextView)
            val quantityInPickingTextView = view.findViewById<TextView>(R.id.QuantityInPickingValueTextView)
            val quantityAvailableToMoveTextView = view.findViewById<TextView>(R.id.QuantityAvailableValueTextView)
            val barcodeTextView = view.findViewById<TextView>(R.id.BarcodeValueTextView)
            val weightTextView = view.findViewById<TextView>(R.id.WeightValueTextView)
            val expirationDateTextView = view.findViewById<TextView>(R.id.ExpirationDateValueTextView)
            val lotNumberTextView = view.findViewById<TextView>(R.id.LotNumberValueTextView)

            val item = suggestions[position]
            itemDescriptionTextView.text = item.itemName
            itemNumberTextView.text = item.itemNumber
            binLocationTextView.text = item.binLocation
            quantityOnHandTextView.text = item.quantityOnHand.toString()
            quantityInPickingTextView.text = item.quantityInPicking.toString()
            quantityAvailableToMoveTextView.text = (item.quantityOnHand - item.quantityInPicking).toString()
            barcodeTextView.text = item.itemBarcode
            if(item.isItemByWeight && !item.isItemByLot)
                weightTextView.text = item.weight.toString() + " lb"
            else
                weightTextView.text = "N/A"

            if(item.isItemByLot && !item.isItemByWeight)
                lotNumberTextView.text = item.lotNumber
            else
                lotNumberTextView.text = "N/A"

            expirationDateTextView.text = item.expireDate



            return view
        }

        fun getItemByRowID(rowID:String):itemsInBin?{
            for(item in suggestions){
                if(item.rowID == rowID)
                    return item
            }
            return null
        }
    }


}